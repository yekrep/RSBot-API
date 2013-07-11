package org.powerbot;

import java.applet.Applet;
import java.awt.Dimension;
import java.lang.reflect.Constructor;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.powerbot.bot.loader.Crawler;
import org.powerbot.bot.nloader.AbstractBridge;
import org.powerbot.bot.nloader.Application;
import org.powerbot.bot.nloader.GameLoader;
import org.powerbot.bot.nloader.GameStub;

public class Test implements Runnable {
	public static void main(String[] params) {
		new Thread(new Test()).start();
	}

	@Override
	public void run() {
		Crawler crawler = new Crawler();
		if (crawler.crawl()) {
			GameLoader loader = new GameLoader(crawler);
			ClassLoader cLoader = loader.call();
			if (cLoader != null) {
				Applet applet;
				try {
					Class<?> clazz = cLoader.loadClass("Rs2Applet");
					Constructor<?> constructor = clazz.getConstructor((Class[]) null);
					applet = (Applet) constructor.newInstance((Object[]) null);
					((Application) applet).setBridge(new AbstractBridge());
					applet.setPreferredSize(new Dimension(800, 700));
				} catch (Exception ignored) {
					ignored.printStackTrace();
					applet = null;
				}

				if (applet != null) {
					JFrame frame = new JFrame();
					frame.setMinimumSize(new Dimension(800, 700));
					JPanel panel = new JPanel();
					panel.setSize(new Dimension(800, 700));
					panel.setMinimumSize(panel.getSize());
					panel.setPreferredSize(panel.getSize());
					frame.add(panel);
					frame.setVisible(true);

					System.out.println("We have it");
					GameStub stub = new GameStub(crawler.parameters, crawler.archive);
					applet.setStub(stub);
					applet.init();
					applet.start();

					panel.add(applet);
					panel.revalidate();
				}
			} else {
				System.out.println("cloader is null");
			}
		} else {
			System.out.println("crawler failed");
		}
	}
}
