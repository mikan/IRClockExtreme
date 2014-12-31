/*
 * Copyright(C) 2014-2015 mikan
 */
package net.aoringo.ircex;

import java.io.IOException;
import javax.microedition.midlet.MIDlet;

/**
 *
 * @author mikan
 */
public class IRClockExtreme extends MIDlet {
    
    private IRListener listener;
    
    @Override
    public void startApp() {
        listener = new IRListener();
        try {
            listener.start();
        } catch (IOException ex) {
            System.out.println("IOException: " + ex);
            notifyDestroyed();
        }
    }
    
    @Override
    public void destroyApp(boolean unconditional) {
        try {
            listener.stop();
        } catch (IOException ex) {
            System.out.println("IOException: " + ex);
        }
        
    }
}
