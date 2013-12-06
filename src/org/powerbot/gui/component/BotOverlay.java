package org.powerbot.gui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.powerbot.Configuration;
import org.powerbot.bot.Bot;
import org.powerbot.event.EventDispatcher;
import org.powerbot.gui.BotChrome;

/**
 * @author Paris
 */
public class BotOverlay extends JDialog {
	private final BotChrome parent;
	private final JPanel panel;
	private volatile BufferedImage bi = null;
	private final boolean offsetMenu;

	public BotOverlay(final BotChrome parent) {
		super(parent);
		this.parent = parent;

		final Color a = new Color(0, 0, 0, 0);
		setUndecorated(true);
		getRootPane().setOpaque(false);
		getContentPane().setBackground(a);
		setBackground(a);
		setFocusableWindowState(false);

		final String jre = System.getProperty("java.version");
		final boolean mac = Configuration.OS == Configuration.OperatingSystem.MAC, clear = jre != null && jre.startsWith("1.6") && mac;
		final String s = System.getProperty("apple.laf.useScreenMenuBar");
		offsetMenu = !(mac && s != null && s.equalsIgnoreCase("true"));

		if (mac) {
			getRootPane().putClientProperty("apple.awt.draggableWindowBackground", Boolean.FALSE);
		}

		panel = new JPanel() {
			@Override
			public void paint(final Graphics g) {
				if (bi == null) {
					return;
				}
				if (clear) {
					g.clearRect(0, 0, getWidth(), getHeight());
				}
				g.drawImage(bi, 0, 0, null);
			}
		};
		setLayout(new BorderLayout());
		panel.setBackground(getBackground());
		add(panel, BorderLayout.CENTER);

		adjustSize();

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					final Bot b = parent.getBot();
					final EventDispatcher m;
					if (b != null && (m = b.getEventDispatcher()) != null) {
						bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
						final Graphics2D g2 = (Graphics2D) bi.getGraphics();
						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

						try {
							m.paint(g2);
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}

					try {
						repaint();
						Thread.sleep(40);
					} catch (final Exception ignored) {
						break;
					}
				}
			}
		}).start();
	}

	public void adjustSize() {
		final Point p = parent.getLocation();
		final Insets s = parent.getInsets();
		p.translate(s.left, s.top);
		final Dimension d = parent.getSize();
		Dimension d2 = new Dimension(d.width - s.left - s.right, d.height - s.top - s.bottom);
		if (offsetMenu) {
			final int h = parent.getJMenuBar().getHeight();
			p.translate(0, h);
			d2 = new Dimension(d2.width, d2.height - h);
		}
		setLocation(p);
		setSize(d2);
		panel.setPreferredSize(getSize());
		pack();
	}
}
