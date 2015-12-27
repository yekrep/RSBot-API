package org.powerbot.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.plaf.RootPaneUI;

import org.powerbot.Configuration;
import org.powerbot.bot.AbstractBot;
import org.powerbot.bot.EventDispatcher;
import org.powerbot.bot.InputSimulator;
import org.powerbot.script.PaintEvent;
import org.powerbot.script.TextPaintEvent;

class BotOverlay extends JDialog {
	private static final Logger log = Logger.getLogger("Overlay");
	private final BotChrome chrome;
	private final Thread repaint;
	private volatile BufferedImage bi = null;
	private final PaintEvent paintEvent;
	private final TextPaintEvent textPaintEvent;
	public final boolean supported;

	public BotOverlay(final BotChrome chrome) {
		super(chrome.window.get());
		this.chrome = chrome;

		final Color a = new Color(0, 0, 0, 0);
		setUndecorated(true);
		setBackground(a);

		final boolean mac = Configuration.OS == Configuration.OperatingSystem.MAC;
		boolean supported = mac || !Configuration.JRE6;

		if (mac) {
			getRootPane().putClientProperty("Window.shadow", Boolean.FALSE);
		}

		switch (Configuration.OS) {
		case LINUX:
			final String path = System.getenv("PATH");
			if (path != null && !path.isEmpty()) {
				for (final String s : path.split(Pattern.quote(File.pathSeparator))) {
					final File f = new File(s, "glxinfo");
					if (f.isFile()) {
						Process p = null;
						BufferedReader stdin = null;
						try {
							p = Runtime.getRuntime().exec(new String[]{f.getAbsolutePath()});
							stdin = new BufferedReader(new InputStreamReader(p.getInputStream()));
							String line;

							while ((line = stdin.readLine()) != null) {
								if (line.toLowerCase().contains("direct rendering: no")) {
									supported = false;
									break;
								}
							}
						} catch (final IOException ignored) {
						} finally {
							if (p != null) {
								p.destroy();
							}
							if (stdin != null) {
								try {
									stdin.close();
								} catch (final IOException ignored) {
								}
							}
						}

						break;
					}
				}
			}
			break;
		}

		if (supported && !isOpaque()) {
			try {
				setBackground(a);
			} catch (final UnsupportedOperationException ignored) {
				log.severe("Transparency is not supported on your system (for paint)");
				supported = false;
			}
		}

		setFocusableWindowState(false);
		setVisible(false);

		final boolean clear = Configuration.OS == Configuration.OperatingSystem.LINUX || (Configuration.JRE6 && mac);

		getRootPane().setUI(new RootPaneUI() {
			@Override
			public void paint(final Graphics g, final JComponent c) {

				if (clear) {
					final int x = 0, y = 0, w = getWidth(), h = getHeight();
					switch (Configuration.OS) {
					case MAC:
						g.clearRect(x, y, w, h);
						break;
					case LINUX:
						g.setColor(a);
						g.fillRect(x, y, w, h);
						break;
					}
				}
				if (bi != null) {
					g.drawImage(bi, 0, 0, null);
				}
			}
		});

		adjustSize();

		paintEvent = new PaintEvent();
		textPaintEvent = new TextPaintEvent();
		final AtomicInteger c = new AtomicInteger(0);
		createBufferStrategy(2);
		final BufferStrategy bs = getBufferStrategy();

		repaint = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					try {
						Thread.sleep(40);
					} catch (final InterruptedException ignored) {
						break;
					}

					if (c.getAndIncrement() % 5 == 0) {
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								adjustSize();
							}
						});
					}

					final AbstractBot b = chrome.bot.get();
					final EventDispatcher m;
					if (b != null && (m = b.dispatcher) != null) {
						bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
						final Graphics2D g2 = (Graphics2D) bi.getGraphics();
						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

						paintEvent.graphics = g2;
						textPaintEvent.graphics = g2;
						textPaintEvent.index = 0;
						try {
							m.consume(paintEvent);
							m.consume(textPaintEvent);
						} catch (final Exception e) {
							e.printStackTrace();
						}

						g2.dispose();
					}

					if (Configuration.OS == Configuration.OperatingSystem.LINUX) {
						do {
							Graphics g = null;
							try {
								g = bs.getDrawGraphics();
								update(g);
							} finally {
								if (g != null) {
									g.dispose();
								}
							}
							bs.show();
						} while (bs.contentsLost());
						Toolkit.getDefaultToolkit().sync();
					} else {
						try {
							repaint();
						} catch (final Exception e) {
							e.printStackTrace();
							break;
						}
					}
				}
			}
		});

		if (supported) {
			repaint.start();
			setVisible(true);

			chrome.window.get().addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(final ComponentEvent e) {
					adjustSize();
				}

				@Override
				public void componentMoved(final ComponentEvent e) {
					adjustSize();
				}
			});

			chrome.window.get().addWindowListener(new WindowAdapter() {
				@Override
				public void windowDeiconified(final WindowEvent e) {
					if (isVisible()) {
						setVisible(false);
						setVisible(true);
					}
				}
			});
		}

		this.supported = supported;
	}

	public void adjustSize() {
		final Point p = chrome.window.get().getLocation();
		final Insets s = chrome.window.get().getInsets();
		p.translate(s.left, s.top);
		final Dimension d = chrome.window.get().getSize();
		Dimension d2 = new Dimension(d.width - s.left - s.right, d.height - s.top - s.bottom);

		final AbstractBot bot;
		final Component c;
		if ((bot = chrome.bot.get()) != null && (c = ((InputSimulator) bot.ctx.input).getComponent()) != null) {
			final Point l = c.getLocation();
			p.translate(l.x, l.y);
			d2 = c.getSize();
		}

		if (!p.equals(getLocation()) || !d2.equals(getSize())) {
			setLocation(p);
			setSize(d2);
			setPreferredSize(d2);
			pack();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		repaint.interrupt();
	}
}
