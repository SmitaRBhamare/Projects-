# app/services/weather_data_service.rb
require 'net/http'
require 'json'
require 'time'
require 'active_support/all'

ALERT_THRESHOLD = 35
CONSECUTIVE_THRESHOLD = 2
KELVIN_TO_CELSIUS = ->(k) { k - 273.15 }
INTERVAL = 300 # 5 minutes in seconds

class WeatherDataService
  def initialize
    @daily_summaries = Hash.new { |hash, key| hash[key] = [] }
    @recent_alerts = Hash.new(0)
  end

  def fetch_weather(city)
    url = URI("http://api.openweathermap.org/data/2.5/weather?q=#{city}&appid=65df703855e5d9d57b8719191b0c0472")
    response = Net::HTTP.get(url)
    data = JSON.parse(response)

    raise data["message"] if data['cod'] == '401'

    # Convert temperatures from Kelvin to Celsius
    temperature_celsius = KELVIN_TO_CELSIUS.call(data['main']['temp'])
    feels_like_celsius = KELVIN_TO_CELSIUS.call(data['main']['feels_like'])

    WeatherData.create!(
      city: city,
      main: data['weather'][0]['main'],
      temp: temperature_celsius.round(2),
      feels_like: feels_like_celsius.round(2),
      recorded_at: Time.now
    )

    check_thresholds(city, temperature_celsius)
    save_daily_summary(city) if Date.today.end_of_day == DateTime.now
  end


  def check_thresholds(city, temp)
    if temp > ALERT_THRESHOLD
      @recent_alerts[city] += 1
      if @recent_alerts[city] >= CONSECUTIVE_THRESHOLD
        puts "Alert: #{city} temperature exceeded #{ALERT_THRESHOLD}°C for #{CONSECUTIVE_THRESHOLD} consecutive readings"
        @recent_alerts[city] = 0
      end
    else
      @recent_alerts[city] = 0
    end
  end

  def generate_daily_summary
    WeatherData.group(:city, "DATE(recorded_at)").each do |city, date, records|
      temperatures = records.map(&:temp)
      conditions = records.map(&:main)

      dominant_condition = conditions.group_by(&:itself).values.max_by(&:size).first

      DailyWeatherSummary.create!(
        city: city,
        avg_temp: temperatures.sum / temperatures.size,
        max_temp: temperatures.max,
        min_temp: temperatures.min,
        dominant_condition: dominant_condition,
        recorded_on: date
      )
    end
  end

  def calculate_daily_summary(city)
    weather_data = WeatherData.where( created_at: DateTime.now.all_day)
    temperatures = weather_data.map {|data|data[:temp] }
    weather_conditions = weather_data.map {|data|data[:main] }

    raise if temperatures.size < 2
    {
      city: city,
      avg_temp: temperatures.sum / temperatures.size,
      max_temp: temperatures.max,
      min_temp: temperatures.min,
      dominant_condition: weather_conditions.group_by(&:itself).values.max_by(&:size).first
    }
  end

  def save_daily_summary(city)
    summary = calculate_daily_summary(city)
    DailyWeatherSummary.create!(
      city: summary[:city],
      avg_temp: summary[:avg_temp],
      max_temp: summary[:max_temp],
      min_temp: summary[:min_temp],
      dominant_condition: summary[:dominant_condition],
      recorded_on: Date.today
    )
  end

  def start
    puts "Enter City Name: "
    city = gets.chomp
    loop do
      fetch_weather(city)
      sleep INTERVAL
    end
  end
end
