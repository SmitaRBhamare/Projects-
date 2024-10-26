class CreateWeatherData < ActiveRecord::Migration[7.1]
  def change
    create_table :weather_data do |t|
      t.string :city
      t.string :main
      t.float :temp
      t.float :feels_like
      t.datetime :recorded_at

      t.timestamps
    end
  end
end
