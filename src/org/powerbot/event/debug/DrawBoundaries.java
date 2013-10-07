package org.powerbot.event.debug;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import org.powerbot.client.Client;
import org.powerbot.client.Constants;
import org.powerbot.event.PaintListener;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.CollisionFlag;
import org.powerbot.script.wrappers.CollisionMap;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.RelativeLocation;

public class DrawBoundaries implements PaintListener {
	@Override
	public void repaint(Graphics render) {
		MethodContext ctx = BotChrome.getInstance().getBot().getMethodContext();
		if (!ctx.game.isLoggedIn()) {
			return;
		}

		CollisionMap map = ctx.movement.getCollisionMap();
		int w = map.getWidth(), h = map.getHeight();
		Point[][] points = new Point[w][h];
		for (int x = 0; x < w; ++x) {
			for (int y = 0; y < h; ++y) {
				Point p = toMap(ctx, x, y);
				if (p.x == -1 || p.y == -1) {
					p = null;
				}

				points[x][y] = p;
			}
		}
		CollisionFlag f = CollisionFlag.DEAD_BLOCK.mark(CollisionFlag.OBJECT_BLOCK);
		for (int x = 1; x < w - 1; ++x) {
			for (int y = 1; y < h - 1; ++y) {
				Point tl = points[x][y];
				Point tr = points[x + 1][y];
				Point br = points[x + 1][y + 1];
				Point bl = points[x][y + 1];
				if (tl != null && tr != null && br != null && bl != null) {
					render.setColor(map.getFlagAt(x, y).contains(f) ? new Color(255, 0, 0, 50) : new Color(0, 255, 0, 50));
					render.fillPolygon(new int[]{tl.x, tr.x, br.x, bl.x}, new int[]{tl.y, tr.y, br.y, bl.y}, 4);
				}
			}
		}
	}

	public Point toMap(MethodContext ctx, int tx, int ty) {
		Point bad = new Point(-1, -1);
		Client client = ctx.getClient();
		RelativeLocation r = ctx.players.local().getRelative();
		float offX = (tx * 4 - r.getX() / 128);
		float offY = (ty * 4 - r.getY() / 128);
		int d = (int) Math.round(Math.sqrt(Math.pow(offX, 2) + Math.pow(offY, 2)));

		Component component = ctx.widgets.get(1465, 12);
		int w = component.getScrollWidth(), h = component.getScrollHeight();
		int radius = Math.max(w / 2, h / 2) + 10;
		if (d >= radius) {
			return bad;
		}

		Constants constants = ctx.getBot().getConstants();
		int v = constants != null ? constants.MINIMAP_SETTINGS_ON : -1;
		boolean f = client.getMinimapSettings() == v;

		double a = (ctx.camera.getYaw() * (Math.PI / 180d)) * 2607.5945876176133d;
		int i = 0x3fff & (int) a;
		if (!f) {
			i = 0x3fff & client.getMinimapOffset() + (int) a;
		}
		int sin = Game.SIN_TABLE[i], cos = Game.COS_TABLE[i];
		if (!f) {
			int scale = 256 + client.getMinimapScale();
			sin = 256 * sin / scale;
			cos = 256 * cos / scale;
		}

		int rotX = (int) (cos * offX + sin * offY) >> 14;
		int rotY = (int) (cos * offY - sin * offX) >> 14;
		rotX += w / 2;
		rotY *= -1;
		rotY += h / 2;

		if (rotX > 4 && rotX < component.getScrollWidth() - 4 &&
				rotY > 4 && rotY < component.getScrollHeight() - 4) {
			Point basePoint = component.getAbsoluteLocation();
			int sX = rotX + (int) basePoint.getX();
			int sY = rotY + (int) basePoint.getY();
			return new Point(sX, sY);
		}

		return bad;
	}
}
