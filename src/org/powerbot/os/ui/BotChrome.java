package org.powerbot.os.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;

import org.powerbot.os.Configuration;
import org.powerbot.os.client.Bot;
import org.powerbot.os.ui.component.BotPanel;

/**
 * @author Paris
 */
public class BotChrome extends JFrame implements Closeable {
	private static final Logger log = Logger.getLogger(BotChrome.class.getSimpleName());
	public final AtomicReference<Bot> bot;
	public final BotPanel panel;

	public BotChrome() {
		setTitle(Configuration.NAME);
		setBackground(Color.BLACK);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		setFocusTraversalKeysEnabled(false);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				close();
			}
		});

		add(panel = new BotPanel());

		setSize(new Dimension(765, 503));
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setLocationRelativeTo(getParent());
		setResizable(false);
		setVisible(true);

		panel.setProgress(50);
		bot = new AtomicReference<Bot>(new Bot(this));
		new Thread(bot.get()).start();
	}

	@Override
	public void close() {
		log.info("Shutting down");
		setVisible(false);

		if (bot.get() != null) {
			bot.get().close();
		}

		dispose();
	}
}
