/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui.plugin.todowatch.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Catgory entity.
 *
 * @author mikan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

    private String id;
    private String body;

    /**
     * @return ID
     */
    public String getId() {
        return id;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }
}
