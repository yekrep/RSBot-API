package org.powerbot.os;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.powerbot.os.ui.BotChrome;

/**
 * @author Paris
 */
public class Boot implements Runnable {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Boot());
	}

	@Override
	public void run() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception ignored) {
		}

		new BotChrome();
	}
}
