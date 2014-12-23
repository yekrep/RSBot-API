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
	private final Ini.Member ini;
	private final JLabel logo;
	private final JPanel panel;

	public AdPanel(final Ini.Member ini, final JLabel logo, final JPanel panel) {
		this.ini = ini;
		this.logo = logo;
		this.panel = panel;
	}

	@Override
	public void run() {
		final String link;
		final Image img;
		try {
			if (!ini.getBool("enabled", false) || !ini.get("image", "").startsWith("http") || !ini.get("link", "").startsWith("http") || !ini.has("expires")) {
				return;
			}

			final long exp;
			try {
				final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				df.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
				exp = df.parse(ini.get("expires")).getTime();
			} catch (final ParseException ignored) {
				return;
			}

			if (System.currentTimeMillis() > exp) {
				return;
			}

			final NetworkAccount n = NetworkAccount.getInstance();
			if (!ini.getBool("vips", false) && n.isLoggedIn() && n.hasPermission(NetworkAccount.VIP)) {
				return;
			}

			link = ini.get("link");
			if (ini.getBool("popup", false)) {
				GoogleAnalytics.getInstance().pageview("ad/popup", "");
				BotChrome.openURL(link);
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
				img = bi.getScaledInstance((int) (bi.getWidth() / z), (int) (bi.getHeight() / z), Image.SCALE_SMOOTH);
			} else {
				img = bi;
			}
		} catch (final IOException ignored) {
			return;
		}

		if (Thread.interrupted()) {
			return;
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JLabel label = new JLabel(new ImageIcon(img));
				label.setBorder(BorderFactory.createEmptyBorder(175, 0, 0, 0));
				label.setCursor(new Cursor(Cursor.HAND_CURSOR));
				label.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(final MouseEvent e) {
						GoogleAnalytics.getInstance().pageview("ad/click", "");
						BotChrome.openURL(link);
					}
				});
				logo.setBorder(label.getBorder());
				final GridBagConstraints c = new GridBagConstraints();
				c.gridy = 2;
				panel.add(label, c);
				panel.revalidate();
				GoogleAnalytics.getInstance().pageview("ad/display", "");
			}
		});
	}
}
