/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui.plugin.camera;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import net.aoringo.ircex.ui.ColorAnimator;
import net.aoringo.ircex.ui.plugin.IconLoader;
import net.aoringo.ircex.ui.plugin.Plugin;
import net.aoringo.ircex.ui.plugin.PluginCallback;

/**
 *
 * @author mikan
 */
public class CameraPlugin implements Plugin {

    private static final Logger LOG = Logger.getLogger(CameraPlugin.class.getName());
    private static final String NAME = "Camera";
    private static final String TO_TAKE = "Press CENTER BUTTON to take a picture.";
    private static final String TO_CLEAR = "Press CENTER BUTTON to clear.";
    private static final String TAKE_CMD = "/usr/bin/raspistill";
    private static final String TAKE_FILE = "/tmp/pic.jpg";
    private final byte[] icon;
    private String message = TO_TAKE;
    private final Pane pane;
    private final ColorAnimator animator;
    private PluginCallback callback;
    private boolean firstCall = true;

    public CameraPlugin(Pane pane, ColorAnimator animator) {
        this.pane = pane;
        this.animator = animator;
        icon = new IconLoader().load("camera.png");
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] getIcon() {
        return icon;
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
        if (firstCall) { // Skip first call when adding.
            firstCall = false;
            return;
        }
        if (!animator.isMoving()) {
            pane.setBackground(Background.EMPTY);
            animator.setMoving(true);
            message = TO_TAKE;
            if (callback != null) {
                callback.messageChanged(this);
            }
        } else {
            if (!new File(TAKE_CMD).exists()) {
                message = "This platform isn't supported Camera Module.";
                if (callback != null) {
                    callback.messageChanged(this);
                }
                return;
            }
            animator.setMoving(false);
            pane.setBackground(Background.EMPTY);
            new Thread(() -> {
                ProcessBuilder builder = new ProcessBuilder(TAKE_CMD,
                        "-n", "-o", TAKE_FILE, "-w", "1366", "-h", "768");
                builder.inheritIO(); // for stdout
                try {
                    Process process = builder.start();
                    process.waitFor(20, TimeUnit.SECONDS);
                    if (new File(TAKE_FILE).exists()) {
                        byte[] data = Files.readAllBytes(Paths.get(TAKE_FILE));
                        LOG.log(Level.INFO, "Picture size: {0}", data.length);
                        Background bg = new Background(new BackgroundImage(
                                new Image(new ByteArrayInputStream(data)),
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundPosition.CENTER,
                                BackgroundSize.DEFAULT)
                        );
                        message = TO_CLEAR;
                        Platform.runLater(() -> {
                            pane.setBackground(bg);
                            callback.messageChanged(this);
                        });
                    } else {
                        setMessage("Ooops! Missing result.");
                    }
                } catch (IOException | InterruptedException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    setMessage("Taking failed: " + ex.getMessage());
                }
            }).start();
        }
    }

    @Override
    public void setCallback(PluginCallback callback) {
        this.callback = callback;
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
