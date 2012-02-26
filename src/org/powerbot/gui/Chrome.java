package org.powerbot.gui;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.powerbot.game.bot.Bot;

public class Chrome extends JFrame implements WindowListener {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(Chrome.class.getName());
	public static final int PANEL_WIDTH = 784, PANEL_HEIGHT = 562;

	final ArrayList<Bot> bots = new ArrayList<Bot>();

	public Chrome() {
		setSize(new Dimension(Chrome.PANEL_WIDTH, Chrome.PANEL_HEIGHT));
		setLocationRelativeTo(getParent());
		setVisible(true);
		addWindowListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	public void addBot() {
		final Bot bot = new Bot();
		if (bot.initializeEnvironment()) {
			bot.startEnvironment();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			add(bot.appletContainer);
		}
		else {
			log.severe("Could not start a bot instance");
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		log.info("Shutting down");
		setVisible(false);
		for (final Bot bot : bots) {
			bot.killEnvironment();
		}
		dispose();
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}
