package org.powerbot.script;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.EventListener;

import org.powerbot.bot.AbstractEvent;

/**
 * PaintEvent
 * An event that is dispatched when the game requests the graphic buffer.
 */
public class PaintEvent extends AbstractEvent {
	public static final int PAINT_EVENT = 0x40;
	private static final long serialVersionUID = 4772234942045737667L;
	public Graphics graphics;

	public PaintEvent() {
		super(PAINT_EVENT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void call(final EventListener e) {
		if (graphics == null) {
			try {
				((PaintListener) e).repaint(null);
			} catch (final Exception ignored) {
			}
			return;
		}
		final Graphics2D g2 = (Graphics2D) graphics;

		final Color b = g2.getBackground();
		final Shape l = g2.getClip();
		final Color c = g2.getColor();
		final Composite m = g2.getComposite();
		final Font f = g2.getFont();
		final Paint p = g2.getPaint();
		final RenderingHints r = g2.getRenderingHints();
		final Stroke s = g2.getStroke();
		final AffineTransform t = g2.getTransform();

		try {
			((PaintListener) e).repaint(graphics);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}

		g2.setBackground(b);
		g2.setClip(l);
		g2.setColor(c);
		g2.setComposite(m);
		g2.setFont(f);
		g2.setPaint(p);
		g2.setRenderingHints(r);
		g2.setStroke(s);
		g2.setTransform(t);
	}
}