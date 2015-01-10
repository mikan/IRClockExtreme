/*
 * Copyright(C) 2014-2015 mikan
 */
package net.aoringo.ircex;

import java.io.IOException;
import jdk.dio.DeviceConfig;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;
import jdk.dio.gpio.GPIOPinConfig;
import jdk.dio.gpio.PinEvent;
import jdk.dio.gpio.PinListener;

/**
 *
 * @author mikan
 */
public class IRListener implements PinListener, AutoCloseable {

    private static final int LED1_ID = 24;
    private static final int Button_Pin = 25;
    private GPIOPin led1;
    private GPIOPin button1;

    public void start() throws IOException {
        led1 = DeviceManager.open(LED1_ID);
        GPIOPinConfig config1 = new GPIOPinConfig(
                DeviceConfig.DEFAULT,
                Button_Pin,
                GPIOPinConfig.DIR_INPUT_ONLY,
                DeviceConfig.DEFAULT,
                GPIOPinConfig.TRIGGER_RISING_EDGE,
                false);
        button1 = DeviceManager.open(config1);
        button1.setInputListener(this);
        led1.setValue(true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        led1.setValue(false);
    }

    @Override
    public void valueChanged(PinEvent event) {
        System.out.println("valueChanged() " + event.getDevice());
        try {
            led1.setValue(!led1.getValue()); // Toggle the value of the led
        } catch (IOException ex) {
            System.out.println("IOException: " + ex);
        }
    }

    @Override
    public void close() {
        try {
            if (led1 != null) {
                led1.setValue(false);
                led1.close();
            }
            if (button1 != null) {
                button1.close();
            }
        } catch (IOException ex) {
            System.err.println("Exception closing resources: " + ex);
        }
    }
}
