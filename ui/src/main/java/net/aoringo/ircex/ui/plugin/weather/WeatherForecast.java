/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui.plugin.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * An entity of weather forecast.
 *
 * @see http://openweathermap.org/weather-data#5days
 *
 * @author mikan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherForecast {

    private String cod;
    private City city;

    @JsonProperty("list")
    private List<Forecast> forecasts;

    /**
     * @return Number of lines returned by this API call
     */
    public String getCod() {
        return cod;
    }

    /**
     * @return City object
     */
    public City getCity() {
        return city;
    }

    public List<Forecast> getForecasts() {
        return forecasts;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class City {

        private String id;
        private String name;
        private String country;
        private Coord coord;

        /**
         * @return City identification
         */
        public String getId() {
            return id;
        }

        /**
         * @return City name
         */
        public String getName() {
            return name;
        }

        /**
         * @return Country (GB, JP etc.)
         */
        public String getCountry() {
            return country;
        }

        /**
         * @return Coord object
         */
        public Coord getCoord() {
            return coord;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Coord {

            private String lat;
            private String lon;

            /**
             * @return City geo location, lat
             */
            public String getLat() {
                return lat;
            }

            /**
             * @return City geo location, lon
             */
            public String getLon() {
                return lon;
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Forecast {
        
        private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

        private long dt;
        
        @JsonProperty("main")
        private Main main;
        
        @JsonProperty("weather")
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        private List<Weather> weathers;
        
        @JsonProperty("clouds")
        private Clouds clouds;
        
        @JsonProperty("wind")
        private Wind wind;
        
        @JsonProperty("rain")
        private Rain rain;
        
        @JsonProperty("snow")
        private Snow snow;

        @JsonProperty("dt_txt")
        private String dtText;
        
        public LocalDateTime getDateTime() {
            return LocalDateTime.parse(dtText, 
                    DateTimeFormatter.ofPattern(DATETIME_FORMAT));
        }
        
        public LocalDateTime getJSTDateTime() {
            return getDateTime().plusHours(9);
        }

        /**
         * @return Data receiving time, unix time, GMT
         */
        public long getDateTimeAsLong() {
            return dt;
        }

        /**
         * @return Data/time in UTC
         */
        public String getDateTimeAsString() {
            return dtText;
        }

        /**
         * @return Temperature, Kelvin (subtract 273.15 to convert to Celsius)
         */
        public float getTempAsKelvin() {
            return main.temp;
        }
        
        public float getTempAsCelsius() {
            return main.temp - 273.15f;
        }

        /**
         * @return Minimum temperature at the moment. This is deviation from
         * current temp that is possible for large cities and megalopolises
         * geographically expanded (use these parameter optionally)
         */
        public float getMinTemp() {
            return main.minTemp;
        }

        /**
         * @return Maximum temperature at the moment. This is deviation from
         * current temp that is possible for large cities and megalopolises
         * geographically expanded (use these parameter optionally)
         */
        public float getMaxTemp() {
            return main.maxTemp;
        }

        /**
         * @return Atmospheric pressure (on the sea level, if there is no
         * sea_level or grnd_level data), hPa
         */
        public float getPressure() {
            return main.pressure;
        }

        /**
         * @return Atmospheric pressure on the sea level, hPa
         */
        public float getSeaLevel() {
            return main.seaLevel;
        }

        /**
         * @return Atmospheric pressure on the ground level, hPa
         */
        public float getGroundLevel() {
            return main.groundLevel;
        }

        /**
         * @return Humidity, %
         */
        public int getHumidity() {
            return main.humidity;
        }

        /**
         * @return Weather condition id
         */
        public int getConditionId() {
            return weathers.get(0).id;
        }

        /**
         * @return Group of weather parameters (Rain, Snow, Extreme etc.)
         */
        public String getWeather() {
            return weathers.get(0).weather;
        }

        /**
         * @return Weather condition within the group
         */
        public String getDescription() {
            return weathers.get(0).description;
        }

        /**
         * @return Weather icon id
         */
        public String getIconId() {
            return weathers.get(0).iconId;
        }

        /**
         * @return Cloudiness, %
         */
        public int getClouds() {
            return clouds != null ? clouds.all : 0;
        }
        
        /**
         * @return Precipitation volume for last 3 hours, mm
         */
        public int getRain() {
            return rain != null ? rain.three : 0;
        }
        
        /**
         * @return Snow volume for last 3 hours, mm
         */
        public int getSnow() {
            return snow != null ? snow.three : 0;
        }
        
        /**
         * @return Wind speed, mps
         */
        public float getWindSpeed() {
            return wind != null ? wind.speed : 0;
        }
        
        /**
         * @return Wind direction, degrees (meteorological)
         */
        public float getWindDirection() {
            return wind != null ? wind.deg : 0;
        }
        
        /**
         * @return Wind gust, mps
         */
        public float getWindDust() {
            return wind != null ? wind.gust : 0;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Main {

            private float temp;

            @JsonProperty("temp_min")
            private float minTemp;

            @JsonProperty("temp_max")
            private float maxTemp;

            private float pressure;

            @JsonProperty("sea_level")
            private float seaLevel;

            @JsonProperty("grnd_level")
            private float groundLevel;

            private int humidity;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Weather {

            private int id;

            @JsonProperty("main")
            private String weather;

            private String description;

            @JsonProperty("icon")
            private String iconId;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Clouds {
            private int all;
        }
        
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Wind {
            private float speed;
            private float deg;
            private float gust;
        }
        
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Rain {
            @JsonProperty("3h")
            private int three;
        }
        
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Snow {
            @JsonProperty("3h")
            private int three;
        }
    }
}
