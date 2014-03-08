package org.powerbot.gui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.powerbot.Configuration;
import org.powerbot.bot.rs3.event.PaintEvent;
import org.powerbot.bot.rs3.event.TextPaintEvent;
import org.powerbot.bot.EventDispatcher;
import org.powerbot.script.Bot;
import org.powerbot.script.Client;

public class BotOverlay extends JDialog {
	private static final Logger log = Logger.getLogger(BotOverlay.class.getName());
	private final BotChrome parent;
	private final Component panel;
	private final Thread repaint;
	private volatile BufferedImage bi = null;
	private final boolean offsetMenu;
	private final PaintEvent paintEvent;
	private final TextPaintEvent textPaintEvent;

	public final boolean supported;

	public BotOverlay(final BotChrome parent) {
		super(parent);
		this.parent = parent;

		final Color a = new Color(0, 0, 0, 0);
		setUndecorated(true);
		getRootPane().setOpaque(false);
		getContentPane().setBackground(a);

		boolean supported = true;
		try {
			setBackground(a);
		} catch (final UnsupportedOperationException ignored) {
			log.severe("Transparency is not supported on your system (for paint)");
			supported = false;
		}
		this.supported = supported;

		setFocusableWindowState(false);
		setVisible(false);

		final String jre = System.getProperty("java.version");
		final boolean mac = Configuration.OS == Configuration.OperatingSystem.MAC;
		final boolean clear = Configuration.OS == Configuration.OperatingSystem.LINUX || (jre != null && jre.startsWith("1.6") && mac);
		final String s = System.getProperty("apple.laf.useScreenMenuBar");
		offsetMenu = !(mac && s != null && s.equalsIgnoreCase("true"));

		if (mac) {
			getRootPane().putClientProperty("apple.awt.draggableWindowBackground", Boolean.FALSE);
		}

		panel = new Component() {
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

		paintEvent = new PaintEvent();
		textPaintEvent = new TextPaintEvent();
		final AtomicInteger c = new AtomicInteger(0);

		repaint = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					try {
						Thread.sleep(40);
					} catch (final InterruptedException ignored) {
						break;
					}

					if (!parent.isVisible() || ((parent.getExtendedState() & Frame.ICONIFIED) == Frame.ICONIFIED)
							|| getWidth() == 0 || getHeight() == 0) {
						continue;
					}

					if (c.getAndIncrement() % 5 == 0) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								adjustSize();
							}
						});
					}

					final Bot b = parent.bot.get();
					final EventDispatcher m;
					if (b != null && (m = b.dispatcher) != null) {
						bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
						final Graphics2D g2 = (Graphics2D) bi.getGraphics();
						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

						paintEvent.graphics = g2;
						textPaintEvent.graphics = g2;
						textPaintEvent.id = 0;
						try {
							m.consume(paintEvent);
							m.consume(textPaintEvent);
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}

					try {
						repaint();
					} catch (final Exception ignored) {
						break;
					}
				}
			}
		});

		if (supported) {
			repaint.start();
		}
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

		final Bot bot;
		final Client client;
		final Canvas canvas;
		if ((bot = parent.bot.get()) != null && (client = bot.ctx().client()) != null
				&& (canvas = ((org.powerbot.bot.rs3.client.Client) client).getCanvas()) != null) {
			final Point l = canvas.getLocation();
			p.translate(l.x, l.y);
			d2 = canvas.getSize();
		}

		if (!p.equals(getLocation()) || !d2.equals(getSize())) {
			setLocation(p);
			setSize(d2);
			setPreferredSize(d2);
			panel.setSize(d2);
			panel.setPreferredSize(d2);
			pack();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		repaint.interrupt();
	}
}
