class CreateDailyWeatherSummaries < ActiveRecord::Migration[7.1]
  def change
    create_table :daily_weather_summaries do |t|
      t.string :city
      t.float :avg_temp
      t.float :max_temp
      t.float :min_temp
      t.string :dominant_condition
      t.date :recorded_on

      t.timestamps
    end
  end
end
