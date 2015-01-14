/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui.plugin.traffic;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import net.aoringo.ircex.net.HttpClient;
import net.aoringo.ircex.ui.plugin.IconLoader;
import net.aoringo.ircex.ui.plugin.Plugin;
import net.aoringo.ircex.ui.plugin.PluginCallback;

/**
 * Provides TOKYU traffic information to the IRClockExtreme.
 *
 * @author mikan
 */
public class TokyuPlugin implements Plugin {

    private static final String NAME = "TOKYU";
    private static final Logger LOG = Logger.getLogger(TokyuPlugin.class.getName());
    private static final String URL = "http://www.tokyu.co.jp/i/unten_i.cgi";
    private Status status = Status.NORMAL;
    private String message = "Loading...";
    private final byte[] icon;
    private PluginCallback callback;

    public TokyuPlugin() {
        icon = new IconLoader().load("train.png");
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
        return status;
    }

    @Override
    public void refresh() {
        message = "Refreshing...";
        if (callback != null) {
            callback.messageChanged(this);
        }
        new Thread(() -> {
            setStatus(Status.LOADING);
            try {
                String html = new HttpClient().requestGet(
                        URL, Charset.forName("Shift_JIS"));
                int cp1 = html.indexOf("\">20");
                int cp2 = html.indexOf("　現在");
                String time = html.substring(cp1 + 2, cp2) + "現在";
                int cp3 = html.indexOf("</div>", cp2);
                String info = html.substring(cp2 + 7, cp3);
                setStatus(Status.NORMAL);
                setMessage(info + " (" + time + ")");
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "IOException", ex);
                setMessage("ERROR: " + ex.getMessage());
                setStatus(Status.ERROR);
            }
        }).start();
    }

    @Override
    public void setCallback(PluginCallback callback) {
        this.callback = callback;
    }

    private void setStatus(Status status) {
        this.status = status;
        if (callback != null) {
            Platform.runLater(() -> {
                callback.statusChanged(this);
            });
        }
    }

    private void setMessage(String message) {
        LOG.info(message);
        this.message = message;
        if (callback != null) {
            Platform.runLater(() -> {
                callback.messageChanged(this);
            });
        }
    }
}
