package org.powerbot.gui.component;

import java.awt.Color;
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
		final boolean clear = jre.startsWith("1.6") && Configuration.OS != Configuration.OperatingSystem.WINDOWS;

		final JPanel panel = new JPanel() {
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
		panel.setBackground(getBackground());
		add(panel);

		final Point p = parent.getLocation();
		final Insets s = parent.getInsets();
		p.translate(s.left, s.top);
		setLocation(p);
		setSize(parent.getContentPane().getSize());

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
}
