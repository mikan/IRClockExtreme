/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Load "user.home"/proxy.properties.
 *
 * @author mikan
 */
public class ProxyConfiguration extends Authenticator {

    private boolean proxyEnabled;
    private boolean authEnabled;
    private String host = null;
    private String port = null;
    private String id = null;
    private String password = null;
    private static final Logger LOG = Logger.getLogger(ProxyConfiguration.class.getName());
    private static final String PROPERTIES
            = System.getProperty("user.home") + File.separator + "proxy.properties";
    private static final String KEY_HOST = "net.proxy.host";
    private static final String KEY_PORT = "net.proxy.port";
    private static final String KEY_USER_ID = "net.proxy.userid";
    private static final String KEY_USER_PASSWORD = "net.proxy.userpassword";

    public ProxyConfiguration() {
        File file = new File(PROPERTIES);
        if (file.exists()) {
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(file));
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Failed to load properties.", ex);
                return;
            }
            host = props.getProperty(KEY_HOST);
            port = props.getProperty(KEY_PORT);
            id = props.getProperty(KEY_USER_ID);
            password = props.getProperty(KEY_USER_PASSWORD);
            if (host != null && port != null) {
                proxyEnabled = true;
            }
            if (proxyEnabled && id != null && password != null) {
                authEnabled = true;
            }
        }
    }

    public boolean isProxyEnabled() {
        return proxyEnabled;
    }
    
    public boolean isAuthenticationEnabled() {
        return authEnabled;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    @Override
    protected RequestorType getRequestorType() {
        return RequestorType.PROXY;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        if (id == null || password == null) {
            return null;
        }
        return new PasswordAuthentication(id, password.toCharArray());
    }
}
