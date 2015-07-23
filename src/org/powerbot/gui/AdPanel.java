package org.powerbot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
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
	private final BotChrome chrome;

	public AdPanel(final BotChrome chrome) {
		this.chrome = chrome;
	}

	private static Ini.Member parseAds(final Ini ini) {
		final Random r = new Random();
		Ini.Member m = null;

		final NetworkAccount n = NetworkAccount.getInstance();
		final boolean vip = n.isLoggedIn() && n.hasPermission(NetworkAccount.VIP);

		for (final Map.Entry<String, Ini.Member> e : ini.entrySet()) {
			if (!(e.getKey().startsWith("ads-") || e.getKey().equals("ads"))) {
				continue;
			}

			final Ini.Member v = e.getValue();

			if (!v.getBool("enabled", false) || !v.get("image", "").startsWith("http") || !v.get("link", "").startsWith("http") || !v.has("expires")) {
				continue;
			}

			if (!v.getBool("vips", false) && vip) {
				continue;
			}

			final long exp;
			try {
				final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				df.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
				exp = df.parse(v.get("expires")).getTime();
			} catch (final ParseException ignored) {
				continue;
			}

			if (System.currentTimeMillis() > exp) {
				continue;
			}

			m = m == null ? v : r.nextBoolean() ? v : m;
		}

		return m;
	}

	@Override
	public void run() {
		final Ini.Member ini = parseAds(chrome.config);
		if (ini == null) {
			return;
		}

		final String link = ini.get("link");
		if (ini.getBool("popup", false)) {
			GoogleAnalytics.getInstance().pageview("ad/popup", "");
			BotChrome.openURL(link);
		}

		final BufferedImage bi;

		try {
			final URL u = new URL(ini.get("image"));
			final File f = new File(Configuration.TEMP, CryptFile.getHashedName("advert-" + u.getPath() + ".1.png"));
			HttpUtils.download(u, f);
			bi = ImageIO.read(f);
		} catch (final IOException ignored) {
			return;
		}

		final double w = bi.getWidth() / 768d, h = bi.getHeight() / 150d;
		final Image img;
		if (w > 1d || h > 1d) {
			final double z = Math.max(w, h);
			img = bi.getScaledInstance((int) (bi.getWidth() / z), (int) (bi.getHeight() / z), Image.SCALE_SMOOTH);
		} else {
			img = bi;
		}

		if (Thread.interrupted()) {
			return;
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JDialog d = new JDialog(chrome.window.get());
				d.setUndecorated(true);

				try {
					d.setBackground(new Color(0, 0, 0, 0));
				} catch (final UnsupportedOperationException ignored) {
					d.setBackground(Color.BLACK);
				}

				chrome.ad.set(d);
				if (Configuration.OS == Configuration.OperatingSystem.MAC) {
					d.getRootPane().putClientProperty("Window.shadow", Boolean.FALSE);
				}

				final JLabel label = new JLabel();
				label.setBackground(d.getBackground());
				label.setIcon(new ImageIcon(img));
				GoogleAnalytics.getInstance().pageview("ad/display", "");
				label.setCursor(new Cursor(Cursor.HAND_CURSOR));
				label.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(final MouseEvent e) {
						GoogleAnalytics.getInstance().pageview("ad/click", "");
						BotChrome.openURL(link);
						chrome.ad.getAndSet(null).dispose();
					}
				});

				final JLabel text = new JLabel(BotLocale.SPONSORED);
				text.setBackground(d.getBackground());
				text.setForeground(Color.WHITE);
				final Font f = text.getFont();
				text.setFont(f.deriveFont(f.getSize2D() - 2f));

				final JButton close = new JButton(BotLocale.CLOSE);
				close.setFont(text.getFont());
				close.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						GoogleAnalytics.getInstance().pageview("ad/close", "");
						chrome.ad.getAndSet(null).dispose();
					}
				});

				final JPanel panel = new JPanel(new BorderLayout());
				panel.setBackground(d.getBackground());
				panel.add(label, BorderLayout.NORTH);
				final JPanel sub = new JPanel(new BorderLayout());
				sub.setBackground(d.getBackground());
				sub.add(text, BorderLayout.WEST);
				sub.add(close, BorderLayout.EAST);
				panel.add(sub, BorderLayout.SOUTH);

				d.add(panel);
				d.pack();
				d.setLocationRelativeTo(d.getOwner());
				final Point p = d.getLocation();
				d.setLocation(p.x, p.y - d.getPreferredSize().height);
				d.setVisible(true);
			}
		});
	}
}
