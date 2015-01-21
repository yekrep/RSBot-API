package org.powerbot.bot.rt6;

import java.awt.Graphics;
import java.util.Arrays;

import org.powerbot.script.TextPaintListener;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Player;

import static org.powerbot.bot.DebugHelper.drawLine;

public class TPlayer extends ClientAccessor implements TextPaintListener {
	public TPlayer(final ClientContext ctx) {
		super(ctx);
	}

	public int draw(int idx, final Graphics render) {
		final Player player = ctx.players.local();
		drawLine(render, idx++, String.format("[%s] A: %d, CBL: %d, HP: %d, T: %d, S: %d, INT: %s", player.name(), player.animation(), player.combatLevel(), player.healthPercent(), player.team(), player.speed(), player.interacting()));
		drawLine(render, idx++, String.format("ORIENT: %d, COMBAT: %s, APP (VE): %s", player.orientation(), Boolean.toString(player.inCombat()), Arrays.toString(player.appearance())));
		return idx;
	}
}
