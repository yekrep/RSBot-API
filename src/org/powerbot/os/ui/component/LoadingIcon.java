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
	private final AtomicInteger progress, target;
	private final Thread worker;

	public LoadingIcon() {
		progress = new AtomicInteger(0);
		target = new AtomicInteger(progress.get());
		setBackground(Color.BLACK);
		setForeground(Color.GRAY);

		worker = new Thread(new Runnable() {
			@Override
			public void run() {
				while (progress.incrementAndGet() <= target.get() && !Thread.interrupted()) {
					try {
						Thread.sleep(40);
					} catch (final InterruptedException ignored) {
					}

					repaint();
				}
			}
		});
		worker.setDaemon(true);
		worker.setPriority(Thread.MIN_PRIORITY);
	}

	public void setProgress(final int p) {
		if (target.get() < p) {
			target.set(p);
			worker.start();
		}
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
