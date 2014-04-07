package org.powerbot.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.powerbot.Configuration;
import org.powerbot.misc.CryptFile;
import org.powerbot.util.HttpUtils;

class GameButtons implements Runnable {
	private final JLabel logo;
	private final JButton rs3, os;
	private final String url;
	private final File cache;

	public GameButtons(final JLabel logo, final JButton rs3, final JButton os) {
		this.logo = logo;
		this.rs3 = rs3;
		this.os = os;

		url = String.format("http://www.%s/img/rs3/sprite_main.png", Configuration.URLs.GAME);
		cache = new File(Configuration.TEMP, CryptFile.getHashedName(url));
	}

	@Override
	public void run() {
		final BufferedImage bi;

		try {
			HttpUtils.download(new URL(url), cache);
			if (!cache.isFile()) {
				return;
			}
			bi = ImageIO.read(cache);
		} catch (final IOException ignored) {
			return;
		}

		final int w = 185, h = 63, x0 = 0, y0 = 754, x1 = 0, y1 = y0 - h;
		final BufferedImage[] ico = {bi.getSubimage(x0, y0, w, h), bi.getSubimage(x1, y1, w, h)};
		final JButton[] b = {rs3, os};

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				logo.setVisible(false);

				for (int i = 0; i < b.length; i++) {
					b[i].setText("");
					b[i].setIcon(new ImageIcon(ico[i]));
					b[i].setBackground(Color.BLACK);
					b[i].setContentAreaFilled(false);
					b[i].setOpaque(true);
					b[i].setBorderPainted(false);
				}
			}
		});
	}
}
