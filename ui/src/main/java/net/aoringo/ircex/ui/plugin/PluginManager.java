/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui.plugin;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
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
        updateSelect();
    }

    public void refreshSelected() {
        plugins.get(selected).refresh();
    }

    public void refreshAll() {
        plugins.forEach(p -> p.refresh());
    }
    
    public void select(int index) {
        if (index < 0 || index >= plugins.size()) {
            throw new IllegalArgumentException("index is out of range: " + index);
        }
        selected = index;
        updateSelect();
    }

    public void selectNext() {
        selected++;
        if (selected >= plugins.size()) {
            selected = 0;
        }
        updateSelect();
    }

    public void selectPrevious() {
        selected--;
        if (selected < 0) {
            selected = plugins.size() - 1;
        }
        updateSelect();
    }

    private void updateSelect() {
        messageLabel.setText(plugins.get(selected).getMessage());
        ObservableList<Node> nodes = iconPane.getChildren();
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if (i == selected) {
                BoxBlur blur = new BoxBlur();
                blur.setWidth(0);
                blur.setHeight(0);
                blur.setIterations(0);
                node.setEffect(blur);
            } else {
                BoxBlur blur = new BoxBlur();
                blur.setWidth(7);
                blur.setHeight(7);
                blur.setIterations(3);
                node.setEffect(blur);
            }
        }
    }

    private class PluginCallbackImpl implements PluginCallback {

        @Override
        public void messageChanged(Plugin plugin) {
            if (plugins.indexOf(plugin) == selected) {
                messageLabel.setText(plugin.getMessage());
                selected = plugins.indexOf(plugin);
                updateSelect();
            }
        }

        @Override
        public void statusChanged(Plugin plugin) {
            if (plugin.getStatus() == Status.LOADING
                    || plugin.getStatus() == Status.REFRESHING) {

            } else if (plugin.getStatus() == Status.ATTENTION) {
                Node node = iconPane.getChildren().get(plugins.indexOf(plugin));
                node.setEffect(new Bloom());
            }
        }

    }
}
