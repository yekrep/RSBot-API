package org.powerbot.game.client.input;

import org.powerbot.game.bot.Bot;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.image.*;
import java.util.Hashtable;

public class Canvas extends java.awt.Canvas {
	private static final long serialVersionUID = -2276037172265300477L;

	private boolean visible, focused, loader_identified;

	private Bot bot;

	@Override
	public final Graphics getGraphics() {
		if (bot == null) {
			if (loader_identified) {
				return super.getGraphics();
			} else {
				bot = Bot.getBot(this);
				loader_identified = true;
			}
		}
		return bot.getBufferGraphics();
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
		if (bot != null) {
			return ((Applet) bot.appletContainer.clientInstance).getSize();
		}
		return super.getSize();
	}

	@Override
	public final void setVisible(boolean visible) {
		super.setVisible(visible);
		this.visible = visible;
	}

	public final void setFocused(boolean focused) {
		if (focused && !this.focused) {
			super.processEvent(new FocusEvent(this, FocusEvent.FOCUS_GAINED, false, null));
		} else if (this.focused) {
			super.processEvent(new FocusEvent(this, FocusEvent.FOCUS_LOST, true, null));
		}
		this.focused = focused;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Image createImage(int width, int height) {
		int[] pixels = new int[height * width];
		DataBufferInt databufferint = new DataBufferInt(pixels, pixels.length);
		DirectColorModel directcolormodel = new DirectColorModel(32, 0xff0000, 0xff00, 255);
		WritableRaster writableraster = Raster.createWritableRaster(directcolormodel.createCompatibleSampleModel(width, height), databufferint, null);
		return new BufferedImage(directcolormodel, writableraster, false, new Hashtable());
	}

	@Override
	protected final void processEvent(AWTEvent e) {
		if (!(e instanceof FocusEvent)) {
			super.processEvent(e);
		}
	}
}