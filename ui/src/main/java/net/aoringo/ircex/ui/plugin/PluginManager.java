/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui.plugin;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import net.aoringo.ircex.ui.plugin.Plugin.Status;

/**
 * Manage plugins.
 *
 * @author mikan
 */
public class PluginManager {
    
    private final List<Plugin> plugins;
    private final Label messageLabel;
    private final Pane iconPane;
    private int selected = -1;
    
    public PluginManager(Label messageLabel, Pane iconPane) {
        plugins = new ArrayList<>();
        this.messageLabel = messageLabel;
        this.iconPane = iconPane;
    }
    
    public void addPlugin(Plugin plugin) {
        Objects.requireNonNull(plugin);
        plugin.setCallback(new PluginCallbackImpl());
        plugins.add(plugin);
        ImageView imageView = new ImageView(new Image(
                new ByteArrayInputStream(plugin.getIcon())));
        imageView.setStyle("-fx-padding: 15px");
        iconPane.getChildren().add(imageView);
        messageLabel.setText(plugin.getMessage());
        plugin.refresh();
        selected = plugins.size() - 1;
    }
    
    public void refreshAll() {
        plugins.forEach(p -> p.refresh());
    }
    
    private ProgressIndicator createProgress() {
        ProgressIndicator progress = new ProgressIndicator();
        progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        return progress;
    }
    
    private class PluginCallbackImpl implements PluginCallback {

        @Override
        public void messageChanged(Plugin plugin) {
            messageLabel.setText(plugin.getMessage());
        }

        @Override
        public void statusChanged(Plugin plugin) {
            if (plugin.getStatus() == Status.LOADING || 
                    plugin.getStatus() == Status.REFRESHING) {
                
            }
        }
        
    }
}
