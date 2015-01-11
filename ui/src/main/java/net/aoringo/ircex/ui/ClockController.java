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
    
    // #################### BASE SCREEN ITEMS ####################
    
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
    
    // #################### MENU SCREEN ITEMS ####################

    @FXML
    private Pane menu;
    
    @FXML
    private Label labelCursorAdd;
    
    @FXML
    private Label labelCursorRemove;
    
    @FXML
    private Label labelCursorOption;
    
    @FXML
    private Label labelCursorQuit;
    
    private MenuCursor menuCursor;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        menu.setVisible(false);
        menuCursor = MenuCursor.ADD;
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
        } else if (command == Command.UP) {
            Platform.runLater(() -> {
                menuCursor = menuCursor.up();
                labelCursorAdd.setVisible(menuCursor == MenuCursor.ADD);
                labelCursorRemove.setVisible(menuCursor == MenuCursor.REMOVE);
                labelCursorOption.setVisible(menuCursor == MenuCursor.OPTION);
                labelCursorQuit.setVisible(menuCursor == MenuCursor.QUIT);
            });
        } else if (command == Command.DOWN) {
            Platform.runLater(() -> {
                menuCursor = menuCursor.down();
                labelCursorAdd.setVisible(menuCursor == MenuCursor.ADD);
                labelCursorRemove.setVisible(menuCursor == MenuCursor.REMOVE);
                labelCursorOption.setVisible(menuCursor == MenuCursor.OPTION);
                labelCursorQuit.setVisible(menuCursor == MenuCursor.QUIT);
            });
        } else if (command == Command.ENTER) {
            if (menu.isVisible()) {
                switch (menuCursor) {
                    case QUIT:
                        Platform.exit();
                        break;
                    default:
                        break;
                }
            }
        } else {
            LOG.severe("Sorry, " + command + " is currently unsupported.");
        }
    }
}
