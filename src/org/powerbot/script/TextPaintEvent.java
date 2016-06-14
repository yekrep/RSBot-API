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
 * TextPaintEvent
 * An event that is dispatched when the game requests the graphic buffer.
 */
public class TextPaintEvent extends AbstractEvent {
	public static final int TEXT_PAINT_EVENT = 0x80;
	private static final long serialVersionUID = 7174559879186449999L;
	public Graphics graphics;
	public int index = 0;

	public TextPaintEvent() {
		super(TEXT_PAINT_EVENT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void call(final EventListener eventListener) {
		if (graphics == null) {
			try {
				((PaintListener) eventListener).repaint(null);
			} catch (final NullPointerException ignored) {
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

		index = ((TextPaintListener) eventListener).draw(index, graphics);

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