/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui.plugin.todowatch.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;

/**
 * The Todo entity.
 *
 * @author mikan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Todo implements Comparable<Todo> {
    
    private String id;
    private String title;
    private String description;
    private int level;
    private Category category;
    private int status;
    private Date deadline;
    private Date created;

    /**
     * @return the ID
     */
    public String getId() {
        return id;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the categoryId
     */
    public Category getCategory() {
        return category;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the deadline
     */
    public Date getDeadline() {
        return deadline;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }
    
    @Override
    public String toString() {
        return title;
    }

    @Override
    public int compareTo(Todo other) {
        if (deadline == null) {
            return 0;
        } else {
            return deadline.compareTo(other.deadline);
        }
    }
}
