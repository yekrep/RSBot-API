package org.powerbot.gui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.powerbot.Configuration;
import org.powerbot.bot.Bot;
import org.powerbot.event.EventMulticaster;
import org.powerbot.event.PaintEvent;
import org.powerbot.event.TextPaintEvent;
import org.powerbot.gui.BotChrome;

/**
 * @author Paris
 */
public class BotOverlay extends JDialog {
	private final BotChrome parent;
	private final JPanel panel;
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
		final boolean mac = Configuration.OS == Configuration.OperatingSystem.MAC, clear = jre.startsWith("1.6") && !mac;
		final String s = System.getProperty("apple.laf.useScreenMenuBar");
		offsetMenu = !(mac && s != null && s.equalsIgnoreCase("true"));

		if (mac) {
			getRootPane().putClientProperty("apple.awt.draggableWindowBackground", Boolean.FALSE);
		}

		panel = new JPanel() {
			@Override
			public void paintComponent(final Graphics g) {
				if (g != null) {
					final Bot b = parent.getBot();
					final EventMulticaster m;
					if (b != null && (m = b.getEventMulticaster()) != null) {
						final PaintEvent paintEvent = b.paintEvent;
						final TextPaintEvent textPaintEvent = b.textPaintEvent;
						paintEvent.graphics = g;
						textPaintEvent.graphics = g;
						textPaintEvent.id = 0;

						if (clear) {
							g.clearRect(0, 0, getWidth(), getHeight());
						}
						try {
							m.fire(paintEvent);
							m.fire(textPaintEvent);
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}
				}
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
