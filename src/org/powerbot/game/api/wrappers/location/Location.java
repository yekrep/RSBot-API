package org.powerbot.game.api.wrappers.location;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.RSAnimableShorts;
import org.powerbot.game.client.RSAnimableX1;
import org.powerbot.game.client.RSAnimableX2;
import org.powerbot.game.client.RSAnimableY1;
import org.powerbot.game.client.RSAnimableY2;
import org.powerbot.game.client.RSInteractableLocation;
import org.powerbot.game.client.RSInteractableManager;
import org.powerbot.game.client.RSInteractableRSInteractableManager;

/**
 * @author Timer
 */
public class Location implements Entity {
	private final Object object;
	private final int type, plane;

	public Location(final Object obj, final int type, final int plane) {
		this.object = obj;
		this.type = type;
		this.plane = plane;
	}

	public Area getArea() {
		if (object instanceof RSAnimableShorts) {
			final Object shorts = ((RSAnimableShorts) object).getRSAnimableShorts();
			if (shorts instanceof RSAnimableX1 &&
					shorts instanceof RSAnimableY1 &&
					shorts instanceof RSAnimableX2 &&
					shorts instanceof RSAnimableY2) {
				final int bX = Game.getBaseX(), bY = Game.getBaseY();
				final Tile tile1 = new Tile(
						bX + (int) ((RSAnimableX1) shorts).getRSAnimableX1(),
						bY + (int) ((RSAnimableY1) shorts).getRSAnimableY1(),
						plane
				);
				final Tile tile2 = new Tile(
						bX + (int) ((RSAnimableX2) shorts).getRSAnimableX2(),
						bY + (int) ((RSAnimableY2) shorts).getRSAnimableY2(),
						plane
				);
				return new Area(tile1, tile2);
			}
		}
		return null;
	}

	public int getId() {
		return Bot.resolve().client.getRSObjectID(object);
	}

	public int getType() {
		return type;
	}

	public int getPlane() {
		return plane;
	}

	public Tile getLocation() {
		final RSInteractableLocation location = ((RSInteractableManager) ((RSInteractableRSInteractableManager) object).getRSInteractableRSInteractableManager()).getData().getLocation();
		return new Tile(Game.getBaseX() + (int) location.getX() / 512, Game.getBaseY() + (int) location.getY() / 512, plane);
	}

	public LocationDefinition getDefinition() {
		return null;//TODO
	}

	public boolean verify() {
		return false;//TODO
	}

	public Point getCentralPoint() {
		return null;//TODO
	}

	public Point getNextViewportPoint() {
		return null;//TODO
	}

	public boolean contains(Point point) {
		return false;//TODO
	}

	public boolean isOnScreen() {
		return false;//TODO
	}

	public Polygon[] getBounds() {
		return new Polygon[0];//TODO
	}

	public boolean hover() {
		return false;//TODO
	}

	public boolean click(boolean left) {
		return false;//TODO
	}

	public boolean interact(String action) {
		return false;//TODO
	}

	public boolean interact(String action, String option) {
		return false;//TODO
	}

	public void draw(Graphics render) {
		//TODO
	}
}
