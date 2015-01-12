/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 *
 * @author mikan
 */
class ColorAnimator implements Runnable {
    
    private static final Logger LOG = Logger.getLogger(ColorAnimator.class.getName());
    private static final int REFRESH_INTERVAL = 200;
    private static final String CSS_BG = "-fx-background-color:";
    private static final String[] KEY_FRAMES = {
        "#e74c3c", "#f1c40f", "#1abc9c", "#3498db", "#9b59b6", "#e74c3c"
    };
    private static final int RES = 100;
    private static final int GAP = RES / (KEY_FRAMES.length - 1); // 20
    private final Pane pane;
    private final Label label;
    private int percent; // 0 to 99
    
    ColorAnimator(Pane pane) {
        this.pane = pane;
        this.label = new Label();
        pane.setStyle(CSS_BG + KEY_FRAMES[0]);
    }
    
    ColorAnimator(Pane pane, Label label) {
        this.pane = pane;
        this.label = label;
        pane.setStyle(CSS_BG + KEY_FRAMES[0]);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Platform.runLater(() -> {
                    Color nextColor = next();
                    pane.setStyle(CSS_BG + nextColor.getColorCode());     
                    label.setText(String.format("%3d ", percent) + nextColor);
                });
                Thread.sleep(REFRESH_INTERVAL);
            } catch (InterruptedException ex) {
                LOG.warning("Updater interrupted!");
                break;
            }
        }
    }
    
    private Color next() {
        percent++;
        if (percent >= RES) {
            percent = 0;
        }
        if (percent % GAP == 0) {
            return new Color(KEY_FRAMES[percent / GAP]);
        }
        int fromPos = percent / GAP;
        int toPos = fromPos + 1;
        if (toPos >= KEY_FRAMES.length) {
            toPos = 1;
        }
        Color from = new Color(KEY_FRAMES[fromPos]);
        Color to = new Color(KEY_FRAMES[toPos]);
        return from.createSteps(to, GAP).get(percent % GAP);
    }
    
    private static class Color {
        
        private final String colorCode;
        private final int r;
        private final int g;
        private final int b;
        
        private Color(String colorCode) {
            this.colorCode = colorCode;
            r = Integer.parseInt(colorCode.substring(1, 3), 16);
            g = Integer.parseInt(colorCode.substring(3, 5), 16);
            b = Integer.parseInt(colorCode.substring(5, 7), 16);
        }
        
        private String getColorCode() {
            return colorCode;
        }
        
        private List<Color> createSteps(Color target, int steps) {
            List<Color> colors = new ArrayList<>();
            for (int i = 0; i < steps; i++) {
                int rs = Math.round((r * (steps - i) + target.r * i) / steps);
                int gs = Math.round((g * (steps - i) + target.g * i) / steps);
                int bs = Math.round((b * (steps - i) + target.b * i) / steps);
                colors.add(new Color(String.format("#%02x%02x%02x", rs, gs, bs)));
            }
            return colors;
        }
        
        @Override
        public String toString() {
            return String.format("Color %s %03d-%03d-%03d", colorCode, r, g, b);
        }
    }
}
