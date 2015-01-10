/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui;

import com.sun.istack.internal.logging.Logger;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 *
 * @author mikan
 */
public class ClockController implements Initializable {

    private static final Logger LOG = Logger.getLogger(ClockController.class);
    private Thread updaterThread;
    private Thread animatorThread;
    
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updaterThread = new Thread(new ClockUpdater(labelClockDate, labelClockHour,
                labelClockColon, labelClockMinute));
        updaterThread.setDaemon(true);
        updaterThread.start();
        animatorThread = new Thread(new ColorAnimator(wrapper, labelDebug));
        animatorThread.setDaemon(true);
        animatorThread.start();
    }
}
