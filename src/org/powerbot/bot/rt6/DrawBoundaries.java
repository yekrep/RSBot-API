package org.powerbot.bot.rt6;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.script.PaintListener;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.CollisionFlag;
import org.powerbot.script.rt6.CollisionMap;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Game;
import org.powerbot.script.rt6.RelativeLocation;

public class DrawBoundaries extends ClientAccessor implements PaintListener {

	public DrawBoundaries(final ClientContext ctx) {
		super(ctx);
	}

	@Override
	public void repaint(final Graphics render) {
		if (!ctx.game.loggedIn()) {
			return;
		}

		final Client client = ctx.client();
		final RelativeLocation r = ctx.players.local().relative();
		final float rx = r.x();
		final float ry = r.z();
		final Component component = ctx.game.mapComponent();
		final int w = component.scrollWidth();
		final int h = component.scrollHeight();
		final int radius = Math.max(w / 2, h / 2) + 10;

		final boolean f = client.getMinimapSettings() == client.reflector.getConstant("V_MINIMAP_SCALE_ON_VALUE");
		final double a = ctx.camera.rotation() * 16384d / (Math.PI * 2d);
		int i = 0x3fff & (int) a;
		if (!f) {
			i = 0x3fff & client.getMinimapOffset() + (int) a;
		}
		int sin = Game.SIN_TABLE[i], cos = Game.COS_TABLE[i];
		if (!f) {
			final int scale = 256 + client.getMinimapScale();
			sin = 256 * sin / scale;
			cos = 256 * cos / scale;
		}

		final CollisionMap map = ctx.movement.collisionMap();
		final int mapWidth = map.width() - 6;
		final int mapHeight = map.height() - 6;
		final Point[][] points = new Point[mapWidth][mapHeight];
		final Point sp = component.screenPoint();
		for (int x = 0; x < mapWidth; ++x) {
			for (int y = 0; y < mapHeight; ++y) {
				Point p = map(x, y, rx, ry, w, h, radius, sin, cos, sp);
				if (p.x == -1 || p.y == -1) {
					p = null;
				}

				points[x][y] = p;
			}
		}
		final CollisionFlag collisionFlag = CollisionFlag.DEAD_BLOCK.mark(CollisionFlag.OBJECT_BLOCK);
		for (int x = 1; x < mapWidth - 1; ++x) {
			for (int y = 1; y < mapHeight - 1; ++y) {
				final Point tl = points[x][y];
				final Point tr = points[x + 1][y];
				final Point br = points[x + 1][y + 1];
				final Point bl = points[x][y + 1];
				if (tl != null && tr != null && br != null && bl != null) {
					render.setColor(map.flagAt(x, y).contains(collisionFlag) ? new Color(255, 0, 0, 50) : new Color(0, 255, 0, 50));
					render.fillPolygon(new int[]{tl.x, tr.x, br.x, bl.x}, new int[]{tl.y, tr.y, br.y, bl.y}, 4);
				}
			}
		}
	}

	Point map(final int tx, final int ty, final float rx, final float ry, final int w, final int h, final int radius, final int sin, final int cos, final Point abs) {
		final float offX = (tx * 4 - rx / 128);
		final float offY = (ty * 4 - ry / 128);
		final int d = (int) Math.round(Math.sqrt(Math.pow(offX, 2) + Math.pow(offY, 2)));
		if (d >= radius) {
			return new Point(-1, -1);
		}

		int rotX = (int) (cos * offX + sin * offY) >> 14;
		int rotY = (int) (cos * offY - sin * offX) >> 14;
		rotX += w / 2;
		rotY *= -1;
		rotY += h / 2;

		if (rotX > 4 && rotX < w - 4 &&
				rotY > 4 && rotY < h - 4) {
			final int sX = rotX + (int) abs.getX();
			final int sY = rotY + (int) abs.getY();
			return new Point(sX, sY);
		}

		return new Point(-1, -1);
	}
}
