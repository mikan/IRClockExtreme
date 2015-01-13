/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui.plugin.weather;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import net.aoringo.ircex.ui.plugin.IconLoader;
import net.aoringo.ircex.ui.plugin.Plugin;
import net.aoringo.ircex.ui.plugin.PluginCallback;
import net.aoringo.ircex.ui.plugin.weather.entity.City;
import net.aoringo.ircex.ui.plugin.weather.entity.WeatherForecast;
import net.aoringo.ircex.ui.plugin.weather.entity.WeatherForecast.Forecast;

/**
 *
 * @author mikan
 */
public class WeatherPlugin implements Plugin {

    private static final String NAME = "Weather";
    private static final Logger LOG = Logger.getLogger(WeatherPlugin.class.getName());
    private static final int FORECASTS = 5;
    private static final int ICON_SIZE = 75; // Actual size: 50
    private final ObservableList<Node> children;
    private final City city;
    private final byte[] icon;
    private String message = "Loading...";
    private PluginCallback callback;

    public WeatherPlugin(Pane pane) {
        city = City.YOKOHAMA;
        children = pane.getChildren();
        children.add(createProgress());
        icon = new IconLoader().load("weather.png");
    }

    public WeatherPlugin(Pane pane, City city) {
        this.city = city;
        children = pane.getChildren();
        children.add(createProgress());
        icon = new IconLoader().load("weather.png");
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Status getStatus() {
        return Status.NORMAL;
    }

    @Override
    public void refresh() {
        new Thread(() -> {
            try {
                setMessage("Refreshing...");
                WeatherReader reader = new WeatherReader(city);
                showForecasts(reader.getWeatherForecast());
                setMessage("Press CENTER BUTTON to refresh weather.");
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Refresh failed!", ex);
                Platform.runLater(() -> {
                    showErrorMessage(ex.getMessage());
                });
            }
        }).start();
    }

    @Override
    public byte[] getIcon() {
        return icon;
    }

    @Override
    public void setCallback(PluginCallback callback) {
        this.callback = callback;
    }

    private ProgressIndicator createProgress() {
        ProgressIndicator progress = new ProgressIndicator();
        progress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        return progress;
    }

    private void showForecasts(WeatherForecast weatherForecast) {
        Platform.runLater(() -> {
            children.clear();
            // City label
            Label labelCity = new Label(weatherForecast.getCity().getName());
            labelCity.setStyle("-fx-font-size: 32px");
            children.add(labelCity);
            // Weather icons
            WeatherReader reader = new WeatherReader();
            int count = 0;
            for (Forecast forecast : weatherForecast.getForecasts()) {
                Image image;
                try {
                    image = new Image(new ByteArrayInputStream(
                            reader.getIcon(forecast.getIconId())),
                            ICON_SIZE, ICON_SIZE, true, true);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Icon load failed.", ex);
                    showErrorMessage("Icon load failed!");
                    break;
                }
                VBox pane = new VBox();
                pane.setAlignment(Pos.CENTER);
                pane.setStyle("-fx-padding: 0 20px");
                ObservableList<Node> nodes = pane.getChildren();
                nodes.add(new ImageView(image));
                nodes.add(new Label(forecast.getWeather()));
                nodes.add(new Label(forecast.getJSTDateTime().toLocalTime().toString()));
                children.add(pane);
                if (++count == FORECASTS) {
                    break;
                }
            }
        });
    }

    /**
     * Show error message.
     * <p>
     * If you invoke from non-JavaFX thread, use with
     * <code>Platform.runLater()</code>.
     * </p>
     *
     * @param message error message
     */
    private void showErrorMessage(String message) {
        children.clear();
        VBox pane = new VBox();
        pane.setAlignment(Pos.CENTER);
        Label labelX = new Label("X");
        labelX.setFont(new Font("Monospaced", 60));
        labelX.setStyle("-fx-text-fill: RED");
        Label labelMessage = new Label(message);
        pane.getChildren().add(labelX);
        pane.getChildren().add(labelMessage);
        children.add(pane);
    }

    private void setMessage(String message) {
        this.message = message;
        if (callback != null) {
            Platform.runLater(() -> {
                callback.messageChanged(this);
            });
        }
    }
}
