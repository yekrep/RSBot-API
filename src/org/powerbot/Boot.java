package org.powerbot;

import org.powerbot.game.bot.Bot;
import org.powerbot.log.SystemConsoleHandler;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Boot {
	public static void main(String[] params) {
		final Logger logger = Logger.getLogger("");
		for (final Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}
		logger.addHandler(new SystemConsoleHandler());

		Bot bot = new Bot();
		if (bot.initializeEnvironment()) {
			bot.startEnvironment();

			JFrame frame = new JFrame();
			frame.add(bot.appletContainer);
			frame.setSize(new Dimension(Chrome.PANEL_WIDTH, Chrome.PANEL_HEIGHT));
			frame.setLocationRelativeTo(frame.getParent());
			frame.setVisible(true);
		}
	}
}
