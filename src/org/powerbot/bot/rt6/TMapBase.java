package org.powerbot.bot.rt6;

import java.awt.Graphics;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;

import static org.powerbot.bot.DebugHelper.drawLine;

public class TMapBase extends ClientAccessor implements TextPaintListener {

	public TMapBase(final ClientContext ctx) {
		super(ctx);
	}

	public int draw(int idx, final Graphics render) {
		final Tile t = ctx.game.mapOffset();
		drawLine(render, idx++, "Map base: " + (t != null ? t.toString() : ""));
		return idx;
	}
}
