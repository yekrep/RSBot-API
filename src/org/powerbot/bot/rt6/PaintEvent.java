package org.powerbot.bot.rt6;

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
import org.powerbot.script.PaintListener;

/**
 * An event that is dispatched when the game requests the graphic buffer.
 */
public class PaintEvent extends AbstractEvent {
	private static final long serialVersionUID = 4772234942045737667L;
	public static final int PAINT_EVENT = 0x40;
	public Graphics graphics;

	public PaintEvent() {
		super(PAINT_EVENT);
		graphics = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void call(final EventListener eventListener) {
		if (graphics == null) {
			try {
				((PaintListener) eventListener).repaint(null);
			} catch (final Exception ignored) {
			}
			return;
		}
		final Graphics2D graphics2D = (Graphics2D) graphics;

		final Color s_background = graphics2D.getBackground();
		final Shape s_clip = graphics2D.getClip();
		final Color s_color = graphics2D.getColor();
		final Composite s_composite = graphics2D.getComposite();
		final Font s_font = graphics2D.getFont();
		final Paint s_paint = graphics2D.getPaint();
		final RenderingHints s_renderingHints = graphics2D.getRenderingHints();
		final Stroke s_stroke = graphics2D.getStroke();
		final AffineTransform s_transform = graphics2D.getTransform();

		try {
			((PaintListener) eventListener).repaint(graphics);
		} catch (final Throwable e) {
			e.printStackTrace();
		}

		graphics2D.setBackground(s_background);
		graphics2D.setClip(s_clip);
		graphics2D.setColor(s_color);
		graphics2D.setComposite(s_composite);
		graphics2D.setFont(s_font);
		graphics2D.setPaint(s_paint);
		graphics2D.setRenderingHints(s_renderingHints);
		graphics2D.setStroke(s_stroke);
		graphics2D.setTransform(s_transform);
	}
}