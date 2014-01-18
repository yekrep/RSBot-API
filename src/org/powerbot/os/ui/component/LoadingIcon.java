package org.powerbot.os.ui.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JPanel;

/**
 * @author Paris
 */
public class LoadingIcon extends JPanel {
	public final AtomicInteger progress;

	public LoadingIcon() {
		progress = new AtomicInteger(0);
		setBackground(Color.BLACK);
		setForeground(Color.GRAY);

		final Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.interrupted() && progress.get() < 101) {
					try {
						Thread.sleep(40);
					} catch (final InterruptedException ignored) {
					}

					if (!isVisible()) {
						continue;
					}

					repaint();
				}
			}
		});
		t.setDaemon(true);
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}

	@Override
	public void paintComponent(final Graphics g) {
		final Graphics2D g2 = (Graphics2D) g;
		final RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHints(rh);

		g2.setColor(getForeground());
		int x = 0, y = 0, w = getWidth() - 1, h = getHeight() - 1;
		g2.fillOval(x, y, w, h);

		final int p = 1;
		g2.setColor(getBackground());
		x += p;
		y += p;
		w -= p + p;
		h -= p + p;
		g2.fillOval(x, y, w, h);

		g2.setColor(getForeground());
		g2.fillArc(x, y, w, h, 90, (int) (Math.min(100, Math.max(0, progress.get())) * -3.6d));
	}
}
