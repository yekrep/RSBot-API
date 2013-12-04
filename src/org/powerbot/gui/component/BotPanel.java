package org.powerbot.gui.component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.powerbot.Configuration;
import org.powerbot.gui.BotChrome;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.Ini;
import org.powerbot.util.io.CryptFile;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.Resources;

public class BotPanel extends JPanel {
	private static final long serialVersionUID = -8983015619045562434L;

	public BotPanel(final BotChrome parent) {
		final Dimension d = new Dimension(BotChrome.PANEL_MIN_WIDTH, BotChrome.PANEL_MIN_HEIGHT);
		setSize(d);
		setPreferredSize(d);
		setMinimumSize(d);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		setBackground(Color.black);

		setLayout(new GridBagLayout());
		final JPanel panel = new JPanel();
		panel.setLayout(getLayout());
		panel.setBackground(getBackground());
		final JLabel logo = new JLabel(new ImageIcon(Resources.getImage(Resources.Paths.ARROWS)));
		panel.add(logo, new GridBagConstraints());
		final GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		final JLabel status;
		panel.add(status = new JLabel(), c);
		status.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		final Font f = status.getFont();
		status.setFont(new Font(f.getFamily(), f.getStyle(), f.getSize() + 1));
		add(panel);
		Logger.getLogger("").addHandler(new BotPanelLogHandler(status));

		new Thread(new Runnable() {
			@Override
			public void run() {
				final String link;
				final Image img;
				try {
					final CryptFile ads = new CryptFile("ads.1.txt", getClass());
					final Ini.Member ini = new Ini().read(ads.download(new URL(Configuration.URLs.ADS))).get();

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
						BotChrome.openURL(link);
					}

					final File f = new File(Configuration.TEMP, CryptFile.getHashedName("advert.1.png"));
					HttpClient.download(new URL(ini.get("image")), f);
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
								BotChrome.openURL(link);
							}

							@Override
							public void mouseEntered(final MouseEvent e) {
								parent.overlay.setCursor(label.getCursor());
							}

							@Override
							public void mouseExited(final MouseEvent e) {
								parent.overlay.setCursor(Cursor.getDefaultCursor());
							}
						});
						logo.setBorder(label.getBorder());
						final GridBagConstraints c = new GridBagConstraints();
						c.gridy = 2;
						panel.add(label, c);
						panel.revalidate();
					}
				});
			}
		}).start();
	}
}
