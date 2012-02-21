package org.powerbot.game.client.input;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.image.*;
import java.util.Hashtable;

public class Canvas extends java.awt.Canvas {
	private static final long serialVersionUID = -2276037172265300477L;

	private boolean visible;
	private boolean focused;

	@Override
	public final Graphics getGraphics() {
		return super.getGraphics();
	}

	@Override
	public final boolean hasFocus() {
		return focused;
	}

	@Override
	public final boolean isValid() {
		return visible;
	}

	@Override
	public final boolean isVisible() {
		return visible;
	}

	@Override
	public final boolean isDisplayable() {
		return true;
	}

	@Override
	public final Dimension getSize() {
		return super.getSize();
	}

	@Override
	public final void setVisible(final boolean visible) {
		super.setVisible(visible);
		this.visible = visible;
	}

	public final void setFocused(final boolean focused) {
		if (focused && !this.focused) {
			super.processEvent(new FocusEvent(this, FocusEvent.FOCUS_GAINED, false, null));
		} else if (this.focused) {
			super.processEvent(new FocusEvent(this, FocusEvent.FOCUS_LOST, true, null));
		}
		this.focused = focused;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Image createImage(final int width, final int height) {
		final int[] pixels = new int[height * width];
		final DataBufferInt databufferint = new DataBufferInt(pixels, pixels.length);
		final DirectColorModel directcolormodel = new DirectColorModel(32, 0xff0000, 0xff00, 255);
		final WritableRaster writableraster = Raster.createWritableRaster(directcolormodel.createCompatibleSampleModel(width, height), databufferint, null);
		return new BufferedImage(directcolormodel, writableraster, false, new Hashtable());
	}

	@Override
	protected final void processEvent(final AWTEvent e) {
		if (!(e instanceof FocusEvent)) {
			super.processEvent(e);
		}
	}
}