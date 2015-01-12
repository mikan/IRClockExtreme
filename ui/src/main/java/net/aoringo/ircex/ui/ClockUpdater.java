/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 *
 * @author mikan
 */
class ClockUpdater implements Runnable {

    private static final Logger LOG = Logger.getLogger(ClockUpdater.class.getName());
    private static final int REFRESH_INTERVAL = 100;
    private static final DateTimeFormatter FORMAT
            = DateTimeFormatter.ofPattern("yyyy/MM/dd EEE");
    private final Label date;
    private final Label hour;
    private final Label colon;
    private final Label minute;

    ClockUpdater(Label date, Label hour, Label colon, Label minute) {
        this.date = date;
        this.hour = hour;
        this.colon = colon;
        this.minute = minute;
    }

    @Override
    public void run() {
        while (true) {
            Platform.runLater(() -> {
                LocalDateTime dateTime = LocalDateTime.now();
                date.setText(dateTime.toLocalDate().format(FORMAT));
                hour.setText(String.format("%02d", dateTime.getHour()));
                minute.setText(String.format("%02d", dateTime.getMinute()));
                colon.setVisible(dateTime.getNano() < 500000000);
            });
            try {
                Thread.sleep(REFRESH_INTERVAL);
            } catch (InterruptedException ex) {
                LOG.warning("Updater interrupted!");
                break;
            }
        }
    }
}
