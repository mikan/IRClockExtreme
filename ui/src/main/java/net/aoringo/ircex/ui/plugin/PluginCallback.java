/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui.plugin;

/**
 *
 * @author mikan
 */
public interface PluginCallback {
    
    public void messageChanged(Plugin plugin);
    public void statusChanged(Plugin plugin);
}
