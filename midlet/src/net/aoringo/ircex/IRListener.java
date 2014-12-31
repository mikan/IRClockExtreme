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
public class IRListener implements PinListener {

    private static final String LED1 = "LED 1";
    private static final int LED1_ID = 1;
    private static final int Button_Port = 0;
    private static final int Button_Pin = 0;
    private GPIOPin led1;
    private GPIOPin button1;

    @Override
    public void valueChanged(PinEvent event) {
        GPIOPin pin = (GPIOPin) event.getDevice();
        // Simple one button = one LED
        try {
            if (pin == button1) {
                System.out.println("setting led1" );
                led1.setValue(!led1.getValue()); // Toggle the value of the led
            }
        } catch (IOException ex) {
            System.out.println("IOException: " + ex);
        }
    }

    public void start() throws IOException {
        // Open the LED pin (Output)
        led1 = (GPIOPin) DeviceManager.open(LED1_ID);
        // Config file for the button - trigger on a rising edge (from low to high)
        GPIOPinConfig config1 = new GPIOPinConfig(
                Button_Port, Button_Pin,
                GPIOPinConfig.DIR_INPUT_ONLY,
                DeviceConfig.DEFAULT,
                GPIOPinConfig.TRIGGER_BOTH_EDGES,
                false);
        // Open the BUTTON pin (Input)
        button1 = (GPIOPin) DeviceManager.open(config1);
        // Add this class as a pin listener to the buttons
        button1.setInputListener(this);
        // Turn the LED on, then off - this tests the LED
        led1.setValue(true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        // Start the LED's off (false)
        led1.setValue(false);
    }

    public void stop() throws IOException {
        if (led1 != null) {
            led1.setValue(false);
            led1.close();
        }
        if (button1 != null) {
            button1.close();
        }
    }
}
