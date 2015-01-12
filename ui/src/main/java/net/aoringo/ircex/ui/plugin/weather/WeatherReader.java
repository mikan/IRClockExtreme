/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui.plugin.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import net.aoringo.ircex.net.HttpClient;

/**
 * Reads weather forecast from OpenWeatherMap API.
 * <p>
 * Requires jackson-databind library.
 * </p>
 *
 * @author mikan
 */
public class WeatherReader {

    private static final String API_URL = "http://api.openweathermap.org/data/2.5/forecast?id=";
    private static final String ICON_URL = "http://openweathermap.org/img/w/";
    private static final String ICON_SUFFIX = ".png";
    private final String url;
    private final HttpClient client;

    public WeatherReader() {
        url = API_URL + City.YOKOHAMA.getCode();
        client = new HttpClient();
    }

    public WeatherReader(City city) {
        url = API_URL + city.getCode();
        client = new HttpClient();
    }

    public WeatherForecast getWeatherForecast() throws IOException {
        String json = client.requestGet(url);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, WeatherForecast.class);
    }

    /**
     * City definition.
     *
     * @see http://openweathermap.org/city/
     */
    public enum City {

        TOKYO("1850147"), YOKOHAMA("1848354");

        private final String code;

        City(String code) {
            this.code = code;
        }

        private String getCode() {
            return code;
        }
    }
}
