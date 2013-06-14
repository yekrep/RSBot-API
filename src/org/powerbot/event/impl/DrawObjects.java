package org.powerbot.event.impl;

import org.powerbot.client.RSAnimable;
import org.powerbot.client.RSObject;
import org.powerbot.event.PaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public class DrawObjects implements PaintListener {
	private static final Color[] C = {Color.GREEN, Color.WHITE, Color.BLACK, Color.BLUE};

	@SuppressWarnings("unchecked")
	@Override
	public void onRepaint(final Graphics render) {
		ClientFactory ctx = BotChrome.getInstance().getBot().getClientFactory();
		if (!ctx.game.isLoggedIn()) {
			return;
		}
		final Player player = ctx.players.getLocal();
		if (player == null) {
			return;
		}
		final FontMetrics metrics = render.getFontMetrics();
		final int textHeight = metrics.getHeight();
		Tile base = ctx.game.getMapBase();
		for (GameObject object : ctx.objects.select().within(25)) {
			Tile t = object.getLocation();
			if (t == null) continue;

			Point p = t.getMatrix(ctx).getCenterPoint();
			if (p.x == -1) continue;

			Point p2 = p;
			p = object.getCenterPoint();
			if (p.x == -1) continue;


			WeakReference<RSObject> internalObj;
			try {
				Field f = object.getClass().getDeclaredField("object");
				f.setAccessible(true);
				internalObj = (WeakReference<RSObject>) f.get(object);
			} catch (IllegalAccessException | NoSuchFieldException e) {
				internalObj = null;
			}

			RSObject rsObject = internalObj != null ? internalObj.get() : null;
			if (rsObject != null && rsObject instanceof RSAnimable) {
				RSAnimable animable = (RSAnimable) rsObject;
				int x1 = animable.getX1(), x2 = animable.getX2(), y1 = animable.getY1(), y2 = animable.getY2();

				for (int _x = x1; _x <= x2; _x++) {
					for (int _y = y1; _y <= y2; _y++) {
						Tile _tile = base.derive(_x, _y);
						_tile.getMatrix(ctx).draw(render);
					}
				}
			}

			render.setColor(Color.gray);
			render.fillRect(p2.x - 1, p2.y - 1, 2, 2);
			render.setColor(Color.black);
			render.fillRect(p.x - 1, p.y - 1, 2, 2);

			render.setColor(new Color(0, 0, 0, 100));
			render.drawLine(p.x, p.y, p2.x, p2.y);

			String s = "" + object.getId();
			int ty = p.y - textHeight / 2;
			int tx = p.x - metrics.stringWidth(s) / 2;
			render.setColor(C[object.getType().ordinal()]);
			render.drawString(s, tx, ty);
		}
	}
}