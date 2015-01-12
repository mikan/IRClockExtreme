/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui.plugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Load icon.
 *
 * @author mikan
 */
public class IconLoader {
    
    /**
     * Load specified icon.
     * 
     * @param fileName icon file name (e.g. train.png)
     * @return icon data
     * @throws RuntimeException If load failed
     */
    public byte[] load(String fileName) {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("img/" + fileName)) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int l;
            while ((l = input.read(buffer)) != -1) {
                output.write(buffer, 0, l);
            }
            return output.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Icon lead error.", ex);
        }
    }
}
