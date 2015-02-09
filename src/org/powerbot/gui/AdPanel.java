package org.powerbot.gui;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.powerbot.Configuration;
import org.powerbot.misc.CryptFile;
import org.powerbot.misc.GoogleAnalytics;
import org.powerbot.misc.NetworkAccount;
import org.powerbot.util.HttpUtils;
import org.powerbot.util.Ini;

class AdPanel implements Runnable {
	private final Ini.Member[] inis;
	private final JLabel logo;
	private final JPanel[] panel;

	public AdPanel(final Ini.Member[] inis, final JLabel logo, final JPanel[] panel) {
		this.inis = inis;
		this.logo = logo;
		this.panel = panel;
	}

	@Override
	public void run() {
		final String[] link = new String[inis.length];
		final Image[] img = new Image[link.length];
		for (int i = 0; i < inis.length; i++) {
			final Ini.Member ini = inis[i];
			try {
				if (!ini.getBool("enabled", false) || !ini.get("image", "").startsWith("http") || !ini.get("link", "").startsWith("http") || !ini.has("expires")) {
					continue;
				}

				final long exp;
				try {
					final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					df.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
					exp = df.parse(ini.get("expires")).getTime();
				} catch (final ParseException ignored) {
					continue;
				}

				if (System.currentTimeMillis() > exp) {
					continue;
				}

				final NetworkAccount n = NetworkAccount.getInstance();
				if (!ini.getBool("vips", false) && n.isLoggedIn() && n.hasPermission(NetworkAccount.VIP)) {
					continue;
				}

				link[i] = ini.get("link");
				if (ini.getBool("popup", false)) {
					GoogleAnalytics.getInstance().pageview("ad/popup", "");
					BotChrome.openURL(link[i]);
				}

				final File f = new File(Configuration.TEMP, CryptFile.getHashedName("advert.1.png"));
				HttpUtils.download(new URL(ini.get("image")), f);
				if (!(f.isFile() && f.canRead())) {
					return;
				}

				final BufferedImage bi = ImageIO.read(f);
				final double w = bi.getWidth() / 768d, h = bi.getHeight() / 150d;
				if (w > 1d || h > 1d) {
					final double z = Math.max(w, h);
					img[i] = bi.getScaledInstance((int) (bi.getWidth() / z), (int) (bi.getHeight() / z), Image.SCALE_SMOOTH);
				} else {
					img[i] = bi;
				}
			} catch (final IOException ignored) {
				return;
			}
		}

		if (Thread.interrupted()) {
			return;
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JLabel[] label = {new JLabel(), new JLabel()};

				for (int i = 0; i < img.length; i++) {
					if (img[i] != null) {
						label[i].setIcon(new ImageIcon(img[i]));
						GoogleAnalytics.getInstance().pageview("ad" + (i == 0 ? "" : "-" + Integer.toString(i + 1)) + "/display", "");
					}
				}

				for (int i = 0; i < label.length; i++) {
					label[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
					final int j = i;
					if (i < link.length && link[i] != null && !link[i].isEmpty()) {
						label[i].addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(final MouseEvent e) {
								GoogleAnalytics.getInstance().pageview("ad" + (j == 0 ? "" : "-" + Integer.toString(j + 1)) + "/ click", "");
								BotChrome.openURL(link[j]);
							}
						});
					}
				}

				label[0].setBorder(BorderFactory.createEmptyBorder(75, 0, 0, 0));
				label[1].setBorder(BorderFactory.createEmptyBorder(0, 0, 75, 0));
				logo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

				final GridBagConstraints c = new GridBagConstraints();
				c.gridy = 2;
				panel[1].add(label[0], c);
				panel[0].add(label[1], c);
			}
		});
	}
}
