/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.receiver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Receive commands from the commander.
 *
 * @author mikan
 */
public class CommandReceiver implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(CommandReceiver.class.getSimpleName());
    private static final int PORT = 10000;
    private HttpServer server = null;

    public void start() throws IOException {
        if (server != null) {
            throw new IllegalStateException("Server already starts.");
        }
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new CommandHandler());
        server.start();
        LOG.log(Level.INFO, "Server starts at port {0}", PORT);
    }

    @Override
    public void close() throws Exception {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    private static class CommandHandler implements HttpHandler {

        private static final String PARAM_COMMAND = "command";

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (method.equals("POST")) {
                handlePost(exchange);
            } else {
                handleGet(exchange);
            }
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            String command = null;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(exchange.getRequestBody(),
                            StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(PARAM_COMMAND + "=")) {
                        command = line.substring(PARAM_COMMAND.length() + 1);
                    }
                }
            }
            LOG.log(Level.INFO, "Command: {0}", command);
            byte[] message = "OK".getBytes();
            try (OutputStream output = exchange.getResponseBody()) {
                exchange.sendResponseHeaders(200, message.length);
                output.write(message);
            }
        }

        private void handleGet(HttpExchange exchange) throws IOException {
            LOG.info("Responding GET request.");
            byte[] message = createSimpleResponseMassage("Command not specified.");
            try (OutputStream output = exchange.getResponseBody()) {
                exchange.sendResponseHeaders(200, message.length);
                output.write(message);
            }
        }

        private static byte[] createSimpleResponseMassage(String message) {
            StringBuilder builder = new StringBuilder();
            builder.append("<html>\n");
            builder.append("\t<head>\n");
            builder.append("\t\t<title>").append(message).append("</title>\n");
            builder.append("\t</head>\n");
            builder.append("\t<body>\n");
            builder.append("\t\t<h1>").append(message).append("</h1>\n");
            builder.append("\t</body>\n");
            builder.append("</html>\n");
            return builder.toString().getBytes();
        }
    }
}
