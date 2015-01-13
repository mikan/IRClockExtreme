/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui.plugin.todowatch;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import net.aoringo.ircex.ui.plugin.IconLoader;
import net.aoringo.ircex.ui.plugin.Plugin;
import net.aoringo.ircex.ui.plugin.PluginCallback;
import net.aoringo.ircex.ui.plugin.todowatch.entity.Todo;

/**
 * Provides Aruga's TodoWatch information.
 *
 * @see https://github.com/akeboshi/TodoWatch
 * @author mikan
 */
public class TodoWatchPlugin implements Plugin {

    private static final String NAME = "TodoWatch";
    private static final Logger LOG = Logger.getLogger(TodoWatchPlugin.class.getName());
    private final byte[] icon;
    private String message;
    private Status status;
    private PluginCallback callback;

    public TodoWatchPlugin() {
        icon = new IconLoader().load("todo.png");
        message = "Loading...";
        status = Status.LOADING;
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
        new Thread(() -> {
            List<Todo> todos;
            try {
                todos = new TodoWatchClient("unko", "unko").getList();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return;
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < todos.size(); i++) {
                builder.append("[").append(i + 1).append("] ");
                builder.append(todos.get(i)).append(" ");
            }
            if (builder.toString().isEmpty()) {
                builder.append("(Nothing to do!)");
            }
            setMessage(builder.toString());
            setStatus(Status.NORMAL);
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
        this.message = message;
        if (callback != null) {
            Platform.runLater(() -> {
                callback.messageChanged(this);
            });
        }
    }
}
