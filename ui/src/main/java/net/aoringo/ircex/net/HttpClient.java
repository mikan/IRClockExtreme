/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Provides Web API interactions.
 *
 * @author mikan
 */
public class HttpClient {
    
    public HttpClient() {
        // do nothing
    }

    public String requestGet(String url) throws IOException {
        HttpURLConnection connection = createConnection(url);
        StringBuilder builder = new StringBuilder();
        int rcode = connection.getResponseCode();
        if (rcode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(),
                            StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
        } else {
            throw new RuntimeException("HTTP Error: " + rcode + " "
                    + connection.getResponseMessage());
        }
        return builder.toString();
    }

    public OutputStream requestGetAsStream(String url) throws IOException {
        HttpURLConnection connection = createConnection(url);
        OutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream input = connection.getInputStream()) {
                while ((n = input.read(buffer)) != -1) {
                    if (n > 0) {
                        output.write(buffer, 0, n);
                    }
                }
            }
        } else {
            throw new RuntimeException("HTTP Error: " + responseCode);
        }
        return output;
    }

    private HttpURLConnection createConnection(String url) throws IOException {
        Objects.requireNonNull(url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(false);
        connection.setUseCaches(false);
        connection.connect();
        return connection;
    }
}
