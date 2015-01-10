/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sends command to the UI.
 *
 * @author mikan
 */
public class Commander {

    private static final String DEFAULT_HOST = "localhost:10000";
    private static final Logger LOG = Logger.getLogger(Commander.class.getSimpleName());

    /**
     * Main method.
     *
     * @param args args[0]: command (required), args[1]: host:port (optional)
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            LOG.severe("Commander: You must specify a command.");
            return;
        }
        String command = args[0];
        String host = args.length == 1 ? DEFAULT_HOST : args[1];
        LOG.log(Level.INFO, "Commander: Sending command: {0}", command);
        try {
            new Commander().executePost(command, host);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Sending failed: {0}", ex.getMessage());
        }
    }

    public void executePost(String command) throws IOException {
        executePost(command, DEFAULT_HOST);
    }

    public void executePost(String command, String host) throws IOException {
        Objects.requireNonNull(command);
        Objects.requireNonNull(host);
        String hostWithPort = host;
        if (!host.contains(":")) {
            hostWithPort += ":10000";
        }
        HttpURLConnection connection = null;
        try {
            // Request
            URL url = new URL("http://" + hostWithPort + "/");
            LOG.log(Level.INFO, "URL: {0}", url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            String parameter = "command=" + command;
            try (PrintWriter writer = new PrintWriter(connection.getOutputStream())) {
                writer.print(parameter);
            }
            LOG.log(Level.INFO, "Request sent.");
            // Response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(),
                                StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
                LOG.info("Sending succeeded.");
            } else {
                LOG.log(Level.SEVERE, "Sending failed: HTTP {0}", responseCode);
            }
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Malformed URL.", ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
