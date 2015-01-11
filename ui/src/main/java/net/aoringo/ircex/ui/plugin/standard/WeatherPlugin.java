/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui.plugin.standard;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import net.aoringo.ircex.ui.plugin.Plugin;

/**
 *
 * @author mikan
 */
public class WeatherPlugin implements Plugin {
    
    private static final String NAME = "Weather";
    private final HBox pane;
    private final Label label;
    
    public WeatherPlugin(HBox pane) {
        this.pane = pane;
        label = new Label("TEST");
        label.setStyle("-fx-background-color: #000000");
        this.pane.getChildren().add(label);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] getIcon() {
        return null;
    }

    @Override
    public String getMessage() {
        return new String();
   }

    @Override
    public Status getStatus() {
        return Status.NORMAL;
    }

    @Override
    public void refresh() {
        
    }
}
