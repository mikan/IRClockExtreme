/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import net.aoringo.ircex.receiver.Command;
import net.aoringo.ircex.receiver.CommandCallback;
import net.aoringo.ircex.receiver.CommandReceiver;
import net.aoringo.ircex.ui.plugin.Plugin;
import net.aoringo.ircex.ui.plugin.PluginManager;
import net.aoringo.ircex.ui.plugin.camera.CameraPlugin;
import net.aoringo.ircex.ui.plugin.todowatch.TodoWatchPlugin;
import net.aoringo.ircex.ui.plugin.traffic.SotetsuPlugin;
import net.aoringo.ircex.ui.plugin.traffic.TokyuPlugin;
import net.aoringo.ircex.ui.plugin.weather.WeatherPlugin;
import net.aoringo.ircex.ui.plugin.weather.entity.City;

/**
 *
 * @author mikan
 */
public class ClockController implements Initializable, CommandCallback {

    private static final Logger LOG = Logger.getLogger(ClockController.class.getName());
    private Thread updaterThread;
    private Thread animatorThread;
    private CommandReceiver receiver;
    private PluginManager pluginManager;
    private WeatherPlugin weatherPlugin;
    private List<Plugin> availablePlugins;

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

    @FXML
    private HBox weatherBox;

    @FXML
    private Label labelPluginMessage;

    @FXML
    private HBox pluginIconBox;

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

    // #################### MANAGE PLUGIN SCREEN ITEMS ####################
    @FXML
    private Pane manage;

    @FXML
    private Label labelManagePluginTitle;

    @FXML
    private Label labelPluginName;

    @FXML
    private Polygon polygonUp;

    @FXML
    private Polygon polygonDown;

    @FXML
    private Label labelPluginHint;

    private int select = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Initialize UI components
        wrapper.requestFocus();
        menu.setVisible(false);
        manage.setVisible(false);
        menuCursor = MenuCursor.ADD;

        // Start update threads
        updaterThread = new Thread(new ClockUpdater(labelClockDate, labelClockHour,
                labelClockColon, labelClockMinute));
        updaterThread.setDaemon(true);
        updaterThread.start();
        ColorAnimator animator = new ColorAnimator(wrapper, labelDebug);
        animatorThread = new Thread(animator);
        animatorThread.setDaemon(true);
        animatorThread.start();

        // Start IR receiver
        receiver = new CommandReceiver(this);
        try {
            receiver.start();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot start the command receiver.", ex);
            throw new RuntimeException("Cannot start the command receiver.");
        }

        // List-up plugins
        weatherPlugin = new WeatherPlugin(weatherBox, City.YOKOHAMA);
        availablePlugins = new ArrayList<>();
        availablePlugins.add(new TokyuPlugin());
        availablePlugins.add(new TodoWatchPlugin());
        availablePlugins.add(new CameraPlugin(wrapper, animator));
        availablePlugins.add(new SotetsuPlugin());

        // Initialize plugins
        pluginManager = new PluginManager(labelPluginMessage, pluginIconBox);
        pluginManager.addPlugin(weatherPlugin);
        pluginManager.addPlugin(availablePlugins.get(0));
        pluginManager.addPlugin(availablePlugins.get(1));
        pluginManager.addPlugin(availablePlugins.get(2));
        pluginManager.select(1); // Focus to TokyuPlugin
    }

    @FXML
    public void handleMouseAction(MouseEvent event) {
        LOG.info("Mouse clicked.");
        Platform.exit();
        try {
            receiver.close();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Receiver close failed.", ex);
        }
    }

    @FXML
    public void handleKeyAction(KeyEvent event) {
        LOG.log(Level.INFO, "Key typed: {0}", event.getCode());
        switch (event.getCode()) {
            case SPACE:
                doCommand(Command.MENU);
                break;
            case ENTER:
                doCommand(Command.ENTER);
                break;
            case LEFT:
                doCommand(Command.LEFT);
                break;
            case RIGHT:
                doCommand(Command.RIGHT);
                break;
            case UP:
                doCommand(Command.UP);
                break;
            case DOWN:
                doCommand(Command.DOWN);
                break;
            default:
                break; // no action
        }
    }

    @Override
    public void command(Command command) {
        Platform.runLater(() -> {
            doCommand(command);
        });
    }

    private void doCommand(Command command) {
        if (command == Command.MENU) {
            if (manage.isVisible()) {
                manage.setVisible(false);
                menu.setVisible(true);
            } else {
                menu.setVisible(!menu.isVisible());
            }
        } else if (command == Command.UP || command == Command.RIGHT) {
            if (menu.isVisible()) {
                menuCursor = menuCursor.up();
                labelCursorAdd.setVisible(menuCursor == MenuCursor.ADD);
                labelCursorRemove.setVisible(menuCursor == MenuCursor.REMOVE);
                labelCursorOption.setVisible(menuCursor == MenuCursor.OPTION);
                labelCursorQuit.setVisible(menuCursor == MenuCursor.QUIT);
            } else if (manage.isVisible()) {
                switch (menuCursor) {
                    case ADD:
                        select++;
                        if (select >= availablePlugins.size()) {
                            select = 0;
                        }
                        labelPluginName.setText(availablePlugins.get(select).getName());
                        break;
                    case REMOVE:
                        List<Plugin> list = pluginManager.getRemovablePlugins();
                        select++;
                        if (select >= list.size()) {
                            select = 0;
                        }
                        labelPluginName.setText(list.get(select).getName());
                        break;
                    case OPTION:
                        select++;
                        if (select >= City.values().length) {
                            select = 0;
                        }
                        labelPluginName.setText(City.values()[select].name());
                        break;
                    default:
                        break; // no action
                }
            } else {
                pluginManager.selectNext();
            }
        } else if (command == Command.DOWN || command == Command.LEFT) {
            if (menu.isVisible()) {
                menuCursor = menuCursor.down();
                labelCursorAdd.setVisible(menuCursor == MenuCursor.ADD);
                labelCursorRemove.setVisible(menuCursor == MenuCursor.REMOVE);
                labelCursorOption.setVisible(menuCursor == MenuCursor.OPTION);
                labelCursorQuit.setVisible(menuCursor == MenuCursor.QUIT);
            } else if (manage.isVisible()) {
                switch (menuCursor) {
                    case ADD:
                        select--;
                        if (select < 0) {
                            select = availablePlugins.size() - 1;
                        }
                        labelPluginName.setText(availablePlugins.get(select).getName());
                        break;
                    case REMOVE:
                        List<Plugin> list = pluginManager.getRemovablePlugins();
                        select--;
                        if (select < 0) {
                            select = list.size() - 1;
                        }
                        labelPluginName.setText(list.get(select).getName());
                        break;
                    case OPTION:
                        select--;
                        if (select < 0) {
                            select = City.values().length - 1;
                        }
                        labelPluginName.setText(City.values()[select].name());
                        break;
                    default:
                        break; // no action
                }
            } else {
                pluginManager.selectPrevious();
            }
        } else if (command == Command.ENTER) {
            if (menu.isVisible()) {
                switch (menuCursor) {
                    case ADD:
                        menu.setVisible(false);
                        labelManagePluginTitle.setText("ADD PLUGIN");
                        select = 0;
                        labelPluginName.setText(availablePlugins.get(select).getName());
                        labelPluginHint.setText("Press CENTER BUTTON to add.");
                        manage.setVisible(true);
                        break;
                    case REMOVE:
                        menu.setVisible(false);
                        labelManagePluginTitle.setText("REMOVE PLUGIN");
                        select = 0;
                        List<Plugin> list = pluginManager.getRemovablePlugins();
                        labelPluginName.setText(list.get(select).getName());
                        labelPluginHint.setText("Press CENTER BUTTON to remove.");
                        manage.setVisible(true);
                        break;
                    case OPTION:
                        menu.setVisible(false);
                        labelManagePluginTitle.setText("WEATHER LOCATION");
                        select = 0;
                        labelPluginName.setText(City.values()[select].name());
                        labelPluginHint.setText("Press CENTER BUTTON to set.");
                        manage.setVisible(true);
                        break;
                    case QUIT:
                        System.exit(0);
                        break;
                    default:
                        break;
                }
            } else if (manage.isVisible()) {
                switch (menuCursor) {
                    case ADD:
                        pluginManager.addPlugin(availablePlugins.get(select));
                        break;
                    case REMOVE:
                        pluginManager.removePlugin(pluginManager.getRemovablePlugins().get(select));
                        break;
                    case OPTION:
                        weatherPlugin.setCity(City.values()[select]);
                        weatherPlugin.refresh();
                        break;
                    default:
                        break; // no action
                }
                manage.setVisible(false);
            } else {
                pluginManager.refreshSelected();
            }
        } else {
            LOG.log(Level.SEVERE, "Sorry, {0} is currently unsupported.", command);
        }
    }
}
