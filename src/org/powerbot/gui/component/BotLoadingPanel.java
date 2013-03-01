package org.powerbot.gui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.powerbot.core.Bot;
import org.powerbot.core.BotComposite;
import org.powerbot.loader.ClientLoader;
import org.powerbot.gui.BotChrome;
import org.powerbot.util.Configuration;
import org.powerbot.util.LoadUpdates;
import org.powerbot.util.io.Resources;

public final class BotLoadingPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	public final JLabel status, info;
	private static final Map<ThreadGroup, LogRecord> logRecord = new HashMap<ThreadGroup, LogRecord>();
	private ThreadGroup listeningGroup = null;
	public static final int PANEL_WIDTH = 728, PANEL_HEIGHT = 120;
	public final DisplayAd ad;
	private final JPanel panelText;
	private final BotLoadingPanelLogHandler handler;

	public BotLoadingPanel(final Component parent) {
		setBackground(Color.BLACK);
		setLayout(new GridLayout(0, 1));

		final JPanel panel = new JPanel(new BorderLayout()), panelTitle = new JPanel(new GridLayout(1, 0));
		panelText = new JPanel(new GridLayout(0, 1));
		panel.setBackground(getBackground());
		panelText.setBackground(getBackground());
		panelTitle.setBackground(getBackground());
		panelText.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

		final JPanel panelTop = new JPanel(), panelBottom = new JPanel();
		panelTop.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		panelTop.setBackground(panel.getBackground());
		panelBottom.setBorder(new EmptyBorder(PANEL_HEIGHT - panelBottom.getPreferredSize().height, 0, 0, 0));
		panelBottom.setPreferredSize(panelTop.getPreferredSize());
		panelBottom.setBackground(panel.getBackground());

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

		panel.add(panelTop, BorderLayout.NORTH);
		panel.add(panelText);
		panel.add(panelBottom, BorderLayout.SOUTH);
		add(panel);

		ad = new DisplayAd(panelBottom);

		handler = new BotLoadingPanelLogHandler(this);
		Logger.getLogger("").addHandler(handler);
	}

	public synchronized void setAdVisible(final boolean visible) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ad.setVisible(visible);
				panelText.setBorder(BorderFactory.createEmptyBorder(50, 0, visible ? 150 : 50, 0));
			}
		});
	}

	public final class DisplayAd extends JLabel {
		private static final long serialVersionUID = -4699438171304667794L;
		private final JPanel container;

		public DisplayAd(final JPanel container) {
			super();
			this.container = container;
		}

		public void setImage(final Image image, final String link) {
			setIcon(new ImageIcon(image));
			setBorder(null);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			if (link != null && !link.isEmpty()) {
				addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(final MouseEvent e) {
						BotChrome.openURL(link);
					}
				});
			}
			final Dimension d1 = container.getPreferredSize(), d2 = getPreferredSize();
			final int dw = (d1.width - d2.width) / 2, dh = d1.height - d2.height;
			container.setBorder(BorderFactory.createEmptyBorder(0, dw, dh, dw));
			container.add(this);
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
			if (!(src.equals(BotChrome.class.getName()) || src.equals(LoadUpdates.class.getName()) ||
					src.equals(Bot.class.getName()) || src.equals(BotComposite.class.getName()) ||
					src.equals(ClientLoader.class.getName()))) {
				return;
			}
			final ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
			if (listeningGroup == null &&
					(currentGroup.getName().startsWith(Bot.class.getName()) || currentGroup.getName().startsWith(BotComposite.class.getName()) ||
							currentGroup.getName().startsWith(ClientLoader.class.getName()))) {
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

			if (title != null && title.equals("Outdated")) {
				final String msg = Configuration.NAME + " needs to be repaired after a recent game update.\nThis usually takes 1-5 days so please wait patiently.\n\n" +
						"You do not need to do anything, an update will be automatically downloaded for you.\n" +
						"All your scripts will work normally afterwards.\n\n" +
						"Please do not post or send in support messages about this as we are already aware.\n" +
						"Check our website forums and twitter for the latest info.";
				JOptionPane.showMessageDialog(BotChrome.getInstance(), msg, title, JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
}
