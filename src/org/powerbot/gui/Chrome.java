package org.powerbot.gui;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.powerbot.game.bot.Bot;

public class Chrome extends JFrame {
	private static final long serialVersionUID = 1L;
	public static final int PANEL_WIDTH = 784, PANEL_HEIGHT = 562;

	final ArrayList<Bot> bots = new ArrayList<Bot>();

	public Chrome() {
		setSize(new Dimension(Chrome.PANEL_WIDTH, Chrome.PANEL_HEIGHT));
		setLocationRelativeTo(getParent());
		setVisible(true);
	}

	public void addBot() {
		final Bot bot = new Bot();
		bot.initializeEnvironment();
		bot.startEnvironment();
		add(bot.appletContainer);
	}
}
