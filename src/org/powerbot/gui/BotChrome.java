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
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class BotChrome extends JFrame implements WindowListener {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(BotChrome.class.getName());
	public static final int PANEL_WIDTH = 784, PANEL_HEIGHT = 562, MAX_BOTS = 6;
	public static BotPanel panel;
	private JLabel logLabel;

	public BotChrome() {
		setTitle(Configuration.NAME);
		setIconImage(Resources.getImage(Resources.Paths.ICON));
		addWindowListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		add(new BotToolBar(this), BorderLayout.NORTH);

		panel = new BotPanel();
		add(panel);
		logLabel = new JLabel(" ");
		logLabel.setFont(logLabel.getFont().deriveFont(Font.BOLD));
		logLabel.setSize(getWidth(), logLabel.getHeight());
		add(logLabel, BorderLayout.SOUTH);
		setupLabel();

		pack();
		setMinimumSize(getSize());
		setLocationRelativeTo(getParent());
		setVisible(true);
	}

	public void setupLabel() {
		final Color statusDefaultColor = logLabel.getForeground();
		Logger.getLogger("").addHandler(new Handler() {
			public void close() throws SecurityException {
			}

			public void flush() {
			}

			@Override
			public void publish(final LogRecord record) {
				final StringBuilder txt = new StringBuilder(46);
				txt.append(" ");
				txt.append(record.getMessage());
				final boolean error = record.getLevel() == Level.WARNING || record.getLevel() == Level.SEVERE;
				logLabel.setForeground(error ? Color.RED : statusDefaultColor);
				logLabel.setText(txt.toString());
			}
		});
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
