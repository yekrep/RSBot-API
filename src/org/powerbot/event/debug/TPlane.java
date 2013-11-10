package org.powerbot.event.debug;

import java.awt.Graphics;

import org.powerbot.event.TextPaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.MethodContext;

import static org.powerbot.event.debug.DebugHelper.drawLine;

/**
 * @author Timer
 */
public class TPlane implements TextPaintListener {
	public int draw(int idx, final Graphics render) {
		final MethodContext ctx = BotChrome.getInstance().getBot().getMethodContext();
		drawLine(render, idx++, "Floor: " + ctx.game.getPlane());
		return idx;
	}
}
