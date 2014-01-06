package org.powerbot.os;

import org.powerbot.os.client.Client;
import org.powerbot.os.loader.Crawler;
import org.powerbot.os.loader.GameLoader;
import org.powerbot.os.loader.GameStub;
import org.powerbot.os.loader.OSRSLoader;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Loader extends JFrame implements Runnable {
    private static final Logger log = Logger.getLogger(Loader.class.getName());
    private Applet applet;
    private Client client;

    public static void main(String[] params) throws Exception {
        final Loader loader = new Loader();
        new Thread(loader, "Old School Loader").start();
    }

    @Override
    public void run() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception ignored) {
        }

        setTitle("07 environment");
        setBackground(Color.BLACK);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        setFocusTraversalKeysEnabled(false);

        //GAME

        log.info("Crawling ...");
        final Crawler crawler = new Crawler();
        if (!crawler.crawl()) {
            log.severe("Failed to load game");
            return;
        }

        log.info("Downloading game ...");
        final GameLoader game = new GameLoader(crawler);
        final ClassLoader classLoader = game.call();
        if (classLoader == null) {
            log.severe("Failed to start game");
            return;
        }

        log.info("Launching loader ...");
        final OSRSLoader loader = new OSRSLoader(game, classLoader);
        loader.setCallback(new Runnable() {
            @Override
            public void run() {
                hook(loader);
            }
        });
        final Thread t = new Thread(loader);
        t.setContextClassLoader(classLoader);
        t.start();
    }

    private void hook(final OSRSLoader loader) {
        log.info("Loading game");
        final Dimension d = new Dimension(756, 503);
        applet = loader.getApplet();
        //TODO: client = (Client) loader.getClient();
        final Crawler crawler = loader.getGameLoader().getCrawler();
        final GameStub stub = new GameStub(crawler.parameters, crawler.archive);
        applet.setStub(stub);
        applet.setSize(d);
        applet.setMinimumSize(d);
        applet.init();
        applet.start();

        add(applet);
        log.log(Level.INFO, "", "Starting...");
        setMinimumSize(d);
        setSize(d);
        setLocationRelativeTo(getParent());
        setVisible(true);
    }
}
