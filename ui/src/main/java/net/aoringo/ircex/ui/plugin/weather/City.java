/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui.plugin.weather;

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

    public String getCode() {
        return code;
    }
}
