/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui;

import com.sun.istack.internal.logging.Logger;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import net.aoringo.ircex.receiver.Command;
import net.aoringo.ircex.receiver.CommandCallback;
import net.aoringo.ircex.receiver.CommandReceiver;

/**
 *
 * @author mikan
 */
public class ClockController implements Initializable, CommandCallback {

    private static final Logger LOG = Logger.getLogger(ClockController.class);
    private Thread updaterThread;
    private Thread animatorThread;
    private CommandReceiver receiver;
    
    @FXML
    private Pane wrapper;

    @FXML
    private Label labelClockDate;

    @FXML
    private Label labelClockHour;

    @FXML
    private Label labelClockColon;

    @FXML
    private Label labelClockMinute;
    
    @FXML
    private Label labelDebug;
    
    @FXML
    private Pane menu;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        menu.setVisible(false);
        updaterThread = new Thread(new ClockUpdater(labelClockDate, labelClockHour,
                labelClockColon, labelClockMinute));
        updaterThread.setDaemon(true);
        updaterThread.start();
        animatorThread = new Thread(new ColorAnimator(wrapper, labelDebug));
        animatorThread.setDaemon(true);
        animatorThread.start();
        receiver = new CommandReceiver(this);
        try {
            receiver.start();
        } catch (IOException ex) {
            LOG.severe("Cannot start the command receiver.", ex);
            throw new RuntimeException("Cannot start the command receiver.");
        }
    }
    
    @FXML
    public void handleMouseAction(MouseEvent event) {
        LOG.info("Mouse clicked.");
        Platform.exit();
        try {
            receiver.close();
        } catch (Exception ex) {
            LOG.severe("Receiver close failed.", ex);
        }
    }

    @Override
    public void command(Command command) {
        if (command == Command.MENU) {
            Platform.runLater(() -> {
                menu.setVisible(!menu.isVisible());
            });
        } else {
            LOG.info("Sorry, " + command + "is currently unsupported.");
        }
    }
}
