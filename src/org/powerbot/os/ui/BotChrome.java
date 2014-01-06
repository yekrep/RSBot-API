package org.powerbot.os.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;

import org.powerbot.os.client.Bot;

/**
 * @author Paris
 */
public class BotChrome extends JFrame implements Closeable {
	private static final Logger log = Logger.getLogger(BotChrome.class.getName());

	public BotChrome() {
		setTitle("RSBot");
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

		setSize(new Dimension(756, 503));
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setLocationRelativeTo(getParent());
		setVisible(true);

		new Thread(new Bot(this)).start();
	}

	@Override
	public void close() {
		log.info("Shutting down");
		dispose();
	}
}
