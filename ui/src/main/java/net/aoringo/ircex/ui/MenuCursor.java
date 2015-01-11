/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui;

import javafx.application.Platform;

/**
 * An enumeration of cursor of menu.
 *
 * @author mikan
 */
enum MenuCursor {

    ADD(0, () -> {
    }),
    REMOVE(1, () -> {
    }),
    OPTION(2, () -> {
    }),
    QUIT(3, () -> {
        System.exit(0); // Shutdown both Platform and Receiver.
    });

    private final int index;
    private final EnterAction enterAction;

    MenuCursor(int index, EnterAction enter) {
        this.index = index;
        this.enterAction = enter;
    }

    /**
     * Returns up cursor.
     * 
     * @return cursor
     */
    public MenuCursor up() {
        int up = index - 1;
        return indexOf(up < 0 ? values().length - 1 : up);
    }

    /**
     * Returns down cursor.
     * 
     * @return cursor
     */
    public MenuCursor down() {
        int down = index + 1;
        return indexOf(down > values().length - 1 ? 0 : down);
    }
    
    /**
     * Do enter process.
     */
    public void enter() {
        enterAction.enter();
    }

    /**
     * Find menu cursor by specified index.
     * 
     * @param index index, begins 0
     * @return menu cursor
     * @throws IllegalArgumentException if not found
     */
    public static MenuCursor indexOf(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Illegal index: " + index);
        }
        for (MenuCursor c : values()) {
            if (c.index == index) {
                return c;
            }
        }
        throw new IllegalArgumentException("No cursor: " + index);
    }

    private interface EnterAction {

        public void enter();
    }
}
