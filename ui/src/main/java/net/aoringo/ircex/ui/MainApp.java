/*
 * Copyright(C) 2014-2015 mikan All rights reserved.
 */
package net.aoringo.ircex.ui;

import java.net.Authenticator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.aoringo.ircex.net.ProxyConfiguration;

/**
 *
 * @author mikan
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Check proxy configuration
        ProxyConfiguration proxy = new ProxyConfiguration();
        if (proxy.isProxyEnabled()) {
            System.setProperty("http.proxyHost", proxy.getHost());
            System.setProperty("http.proxyPort", proxy.getPort());
            System.setProperty("https.proxyHost", proxy.getHost());
            System.setProperty("https.proxyPort", proxy.getPort());
        }
        if (proxy.isAuthenticationEnabled()) {
            Authenticator.setDefault(proxy);
        }

        // Start JavaFX platform
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Clock.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setTitle("IR Clock Extreme");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

}
