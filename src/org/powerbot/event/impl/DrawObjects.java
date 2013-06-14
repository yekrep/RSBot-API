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
		final Tile position = player.getLocation();
		final int textHeight = metrics.getHeight();
		Tile base = ctx.game.getMapBase();
		for (int x = position.getX() - 25; x < position.getX() + 25; x++) {
			for (int y = position.getY() - 25; y < position.getY() + 25; y++) {
				Tile tile = new Tile(x, y, ctx.game.getPlane());
				GameObject[] objs = ctx.objects.select().at(tile).toArray();
				if (objs.length == 0) {
					continue;
				}

				Point locationPoint = tile.getMatrix(ctx).getCenterPoint();
				render.setColor(Color.black);
				render.fillRect(locationPoint.x - 1, locationPoint.y - 1, 2, 2);
				int i = 0;
				for (GameObject object : objs) {
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

					String s = "" + object.getId();
					int ty = locationPoint.y - textHeight / 2 - i++ * 15;
					int tx = locationPoint.x - metrics.stringWidth(s) / 2;
					render.setColor(C[object.getType().ordinal()]);
					render.drawString(s, tx, ty);
				}
			}
		}
	}
}