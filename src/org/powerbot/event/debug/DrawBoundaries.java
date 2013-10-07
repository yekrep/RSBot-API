package org.powerbot.event.debug;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

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

		Client client = ctx.getClient();
		RelativeLocation r = ctx.players.local().getRelative();
		float rx = r.getX(), ry = r.getY();
		Component component = ctx.widgets.get(1465, 12);
		int w = component.getScrollWidth(), h = component.getScrollHeight();
		int radius = Math.max(w / 2, h / 2) + 10;

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

		CollisionMap map = ctx.movement.getCollisionMap();
		int mapWidth = map.getWidth(), mapHeight = map.getHeight();
		Point[][] points = new Point[mapWidth][mapHeight];
		for (int x = 0; x < mapWidth; ++x) {
			for (int y = 0; y < mapHeight; ++y) {
				Point p = map(x, y, rx, ry, w, h, radius, sin, cos, component.getAbsoluteLocation());
				if (p.x == -1 || p.y == -1) {
					p = null;
				}

				points[x][y] = p;
			}
		}
		CollisionFlag collisionFlag = CollisionFlag.DEAD_BLOCK.mark(CollisionFlag.OBJECT_BLOCK);
		for (int x = 1; x < mapWidth - 1; ++x) {
			for (int y = 1; y < mapHeight - 1; ++y) {
				Point tl = points[x][y];
				Point tr = points[x + 1][y];
				Point br = points[x + 1][y + 1];
				Point bl = points[x][y + 1];
				if (tl != null && tr != null && br != null && bl != null) {
					render.setColor(map.getFlagAt(x, y).contains(collisionFlag) ? new Color(255, 0, 0, 50) : new Color(0, 255, 0, 50));
					render.fillPolygon(new int[]{tl.x, tr.x, br.x, bl.x}, new int[]{tl.y, tr.y, br.y, bl.y}, 4);
				}
			}
		}
	}

	public Point map(int tx, int ty, float rx, float ry, int w, int h, int radius, int sin, int cos, Point abs) {
		float offX = (tx * 4 - rx / 128);
		float offY = (ty * 4 - ry / 128);
		int d = (int) Math.round(Math.sqrt(Math.pow(offX, 2) + Math.pow(offY, 2)));
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
			int sX = rotX + (int) abs.getX();
			int sY = rotY + (int) abs.getY();
			return new Point(sX, sY);
		}

		return new Point(-1, -1);
	}
}
