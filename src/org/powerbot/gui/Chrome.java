package org.powerbot.gui;

import org.powerbot.game.bot.Bot;
import org.powerbot.gui.component.BotPanel;
import org.powerbot.gui.component.BotToolBar;
import org.powerbot.util.Configuration;
import org.powerbot.util.io.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Logger;

public class Chrome extends JFrame implements WindowListener {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(Chrome.class.getName());
	public static final int PANEL_WIDTH = 784, PANEL_HEIGHT = 562;
	public static BotPanel panel;

	public Chrome() {
		setSize(new Dimension(Chrome.PANEL_WIDTH, Chrome.PANEL_HEIGHT));
		setLocationRelativeTo(getParent());
		setTitle(Configuration.NAME);
		setIconImage(Resources.getImage(Resources.Paths.ICON));
		addWindowListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		add(new BotToolBar(), BorderLayout.NORTH);
		Chrome.panel = new BotPanel();
		add(Chrome.panel);
		setVisible(true);
	}

	public void addBot() {
		Bot bot = new Bot();
		if (bot.initializeEnvironment()) {
			bot.startEnvironment();
			panel.setBot(bot);
		} else {
			log.severe("Could not start a bot instance");
		}
	}

	public void windowActivated(WindowEvent arg0) {
	}

	public void windowClosed(WindowEvent arg0) {
	}

	public void windowClosing(WindowEvent arg0) {
		log.info("Shutting down");
		setVisible(false);
		int bots = Bot.bots.size();
		while (bots-- > 0) {
			Bot.bots.peekLast().killEnvironment();
		}
		dispose();
		System.exit(0);
	}

	public void windowDeactivated(WindowEvent arg0) {
	}

	public void windowDeiconified(WindowEvent arg0) {
	}

	public void windowIconified(WindowEvent arg0) {
	}

	public void windowOpened(WindowEvent arg0) {
	}
}
