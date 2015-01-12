/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui.plugin;

/**
 * The plugin interface of the clock screen.
 * 
 * @author mikan
 */
public interface Plugin {
    
    public String getName();
    public byte[] getIcon();
    public String getMessage();
    public Status getStatus();
    public void refresh();
    
    /**
     * Plugin status.
     */
    public enum Status {
        LOADING, NORMAL, REFRESHING, ATTENTION, ALERT;
    }
}
