package org.powerbot.gui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.powerbot.game.GameDefinition;
import org.powerbot.game.bot.Bot;
import org.powerbot.gui.BotChrome;
import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.Resources;
import org.powerbot.util.io.SecureStore;

public final class BotLoadingPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	public final JLabel status, info;
	private static final Map<ThreadGroup, LogRecord> logRecord = new HashMap<ThreadGroup, LogRecord>();
	private ThreadGroup listeningGroup = null;
	private final BotLoadingPanelLogHandler handler;

	public BotLoadingPanel(final Component parent) {
		setBackground(Color.BLACK);
		setLayout(new GridLayout(0, 1));

		final JPanel panel = new JPanel(new BorderLayout()), panelText = new JPanel(new GridLayout(0, 1)), panelTitle = new JPanel(new GridLayout(1, 0));
		panel.setBackground(getBackground());
		panelText.setBackground(getBackground());
		panelTitle.setBackground(getBackground());

		final JLabel logo = new JLabel(new ImageIcon(Resources.getImage(Resources.Paths.ARROWS)));
		logo.setHorizontalAlignment(SwingConstants.RIGHT);
		panelTitle.add(logo);

		status = new JLabel();
		status.setHorizontalAlignment(SwingConstants.LEFT);
		status.setFont(status.getFont().deriveFont(Font.BOLD, 24));
		status.setForeground(Color.WHITE);
		status.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
		panelTitle.add(status);

		panelText.add(panelTitle);

		info = new JLabel();
		info.setHorizontalAlignment(SwingConstants.CENTER);
		info.setFont(info.getFont().deriveFont(0, 14));
		panelText.add(info);

		panel.add(panelText);

		add(panel);

		handler = new BotLoadingPanelLogHandler(this);
		Logger.getLogger("").addHandler(handler);

		final int delay = 100;

		final Timer t = new Timer(delay, new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				final Timer t = (Timer) arg0.getSource();
				if (!BotChrome.loaded) {
					return;
				}
				new DisplayAd(parent, panel).run();
				t.stop();
			}
		});
		t.setCoalesce(false);
		if (Configuration.DEVMODE) {
			t.stop();
		} else {
			t.start();
		}
	}

	private final class DisplayAd implements Runnable {
		private final Component parent;
		private final JPanel panel;

		public DisplayAd(final Component parent, final JPanel panel) {
			this.parent = parent;
			this.panel = panel;
		}

		public void run() {
			JLabel[] imageLabel = null;
			try {
				if (Resources.getServerData().containsKey("ads")) {
					final String src = Resources.getServerData().get("ads").get("image"), link = Resources.getServerData().get("ads").get("link");
					final String filename = "ad.png";
					final File cache = new File(System.getProperty("java.io.tmpdir"), filename);
					final URL url = new URL(src);
					final boolean secure = true;
					if (secure) {
						final String fileid = "ad-image.txt";
						final InputStream is = SecureStore.getInstance().read(fileid);
						if (is != null) {
							final String cached = StringUtil.newStringUtf8(IOHelper.read(is));
							if (!cached.equals(src)) {
								SecureStore.getInstance().write(filename, null);
							}
						}
						SecureStore.getInstance().download(filename, url);
						SecureStore.getInstance().write(fileid, new ByteArrayInputStream(StringUtil.getBytesUtf8(src)));
					} else {
						HttpClient.download(url, cache);
					}
					BufferedImage image = secure ? ImageIO.read(SecureStore.getInstance().read(filename)) : ImageIO.read(cache);
					final float MAX_WIDTH = 728, MAX_HEIGHT = 120;
					if (image.getWidth() > MAX_WIDTH || image.getHeight() > MAX_HEIGHT) {
						final float factor = Math.min(MAX_WIDTH / image.getWidth(), MAX_HEIGHT / image.getHeight());
						final BufferedImage resized = new BufferedImage((int) (image.getWidth() * factor), (int) (image.getHeight() * factor), BufferedImage.TYPE_INT_ARGB);
						final Graphics2D g = resized.createGraphics();
						g.drawImage(image, 0, 0, resized.getWidth(), resized.getHeight(), null);
						g.dispose();
						image = resized;
					}
					final BufferedImage shadow = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
					imageLabel = new JLabel[]{new JLabel(new ImageIcon(image)), new JLabel(new ImageIcon(shadow))};
					imageLabel[0].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					imageLabel[0].addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(final MouseEvent arg0) {
							BotChrome.openURL(link);
						}
					});
					final int d = 50;
					imageLabel[0].setBorder(BorderFactory.createEmptyBorder(d, 0, 0, 0));
					imageLabel[1].setBorder(BorderFactory.createEmptyBorder(0, 0, d, 0));
				}
			} catch (final IOException ignored) {
			} catch (final GeneralSecurityException ignored) {
			}

			final Component c = panel.getComponent(0);
			panel.removeAll();

			if (imageLabel != null) {
				panel.add(imageLabel[1], BorderLayout.NORTH);
			}

			panel.add(c);

			if (imageLabel != null) {
				panel.add(imageLabel[0], BorderLayout.SOUTH);
			}

			parent.validate();
			parent.repaint();
		}
	}

	public void set(final ThreadGroup threadGroup) {
		this.listeningGroup = threadGroup;
		final LogRecord record;
		if (threadGroup != null && (record = logRecord.get(threadGroup)) != null) {
			handler.publish(record);
		}
	}

	private final class BotLoadingPanelLogHandler extends Handler {
		private final BotLoadingPanel panel;

		public BotLoadingPanelLogHandler(final BotLoadingPanel panel) {
			this.panel = panel;
		}

		@Override
		public void close() throws SecurityException {
		}

		@Override
		public void flush() {
		}

		@Override
		public void publish(final LogRecord record) {
			String src = record.getLoggerName();
			if (src == null || src.isEmpty()) {
				return;
			}
			final int x = src.indexOf('$');
			if (x > 0) {
				src = src.substring(0, x);
			}
			if (!(src.equals(BotChrome.class.getName()) || src.equals(GameDefinition.class.getName()) || src.equals(Bot.class.getName()))) {
				return;
			}
			final ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
			if (listeningGroup == null && currentGroup.getName().startsWith(GameDefinition.class.getName())) {
				return;
			}
			logRecord.put(currentGroup, record);
			if (listeningGroup != null && currentGroup != listeningGroup) {
				return;
			}
			Color c = new Color(200, 200, 200);
			final String title = record.getParameters() != null && record.getParameters().length == 1 ? (String) record.getParameters()[0] : null;
			if (record.getLevel() == Level.SEVERE || record.getLevel() == Level.WARNING) {
				if (title == null) {
					panel.status.setText("Unavailable");
				}
				c = new Color(255, 87, 71);
			} else if (record.getLevel() == Level.INFO) {
				if (title == null) {
					panel.status.setText("Loading...");
				}
			}
			if (title != null) {
				panel.status.setText(title);
			}
			panel.info.setForeground(c);
			panel.info.setText(record.getMessage());
		}
	}
}
