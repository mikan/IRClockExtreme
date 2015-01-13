/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Provides Web API interactions.
 *
 * @author mikan
 */
public class HttpClient {

    public HttpClient() {
        CookieHandler.setDefault(new CookieManager());
    }

    /**
     * Send request and receive text response.
     *
     * @param url URL
     * @return response, charset is UTF-8
     * @throws IOException if I/O error occurred
     */
    public String requestGet(String url) throws IOException {
        return requestGet(url, StandardCharsets.UTF_8, null);
    }
    
    /**
     * Send request and receive text response with specified charset.
     *
     * @param url URL
     * @param charset charset
     * @return response
     * @throws IOException if I/O error occurred
     */
    public String requestGet(String url, Charset charset) throws IOException {
        return requestGet(url, charset, null);
    }

    /**
     * Send request and receive text response with specified charset.
     * 
     * <p>
     * Authorization version.
     * </p>
     *
     * @param url URL
     * @param charset charset
     * @param auth Authorization parameter
     * @return response
     * @throws IOException if I/O error occurred
     */
    public String requestGet(String url, Charset charset, String auth) throws IOException {
        Objects.requireNonNull(url);
        HttpURLConnection connection = createGetConnection(new URL(url), auth);
        int rcode = connection.getResponseCode();
        if (rcode == HttpURLConnection.HTTP_OK) {
            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(),
                            charset))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
            return builder.toString();
        } else {
            throw new RuntimeException("HTTP Error: " + rcode + " " 
                    + connection.getResponseMessage());
        }
    }

    /**
     * Send request and receive binary response.
     *
     * @param url URL
     * @return response
     * @throws IOException if I/O error occurred
     */
    public byte[] requestGetAsByteArray(String url) throws IOException {
        Objects.requireNonNull(url);
        HttpURLConnection connection = createGetConnection(new URL(url), null);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int rcode = connection.getResponseCode();
        if (rcode == HttpURLConnection.HTTP_OK) {
            byte[] buffer = new byte[4096];
            int n;
            try (InputStream input = connection.getInputStream()) {
                while ((n = input.read(buffer)) != -1) {
                    if (n > 0) {
                        output.write(buffer, 0, n);
                    }
                }
            }
            return output.toByteArray();
        } else {
            throw new RuntimeException("HTTP Error: " + rcode 
                    + connection.getResponseMessage());
        }
    }

    public String requestPost(String url, String contentType, String params)
            throws IOException {
        Objects.requireNonNull(url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url)
                .openConnection();
        connection.setRequestMethod("POST");
        connection.setInstanceFollowRedirects(false);
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.addRequestProperty("Content-type", contentType);
        connection.setRequestProperty("Content-Length",
                Integer.toString(params.getBytes().length));
        connection.connect();

        // Sending request
        try (DataOutputStream output = new DataOutputStream(
                connection.getOutputStream())) {
            output.writeBytes(params);
            output.flush();
        }

        // Receiving response
        int rcode = connection.getResponseCode();
        if (rcode == HttpURLConnection.HTTP_OK) {
            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(),
                            StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
            return builder.toString();
        } else {
            throw new RuntimeException("HTTP Error: " + rcode);
        }
    }

    private HttpURLConnection createGetConnection(URL url, String auth) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(false);
        connection.setUseCaches(false);
        if (auth != null) {
            connection.setRequestProperty("Authorization", auth);
        }
        connection.connect();
        return connection;
    }
}
