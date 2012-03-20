package org.powerbot.game.api.wrappers.location;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.graphics.CapturedModel;
import org.powerbot.game.api.wrappers.graphics.model.LocationModel;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.Model;
import org.powerbot.game.client.ModelCapture;
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
	private final Type type;
	private final int plane;

	public static enum Type {
		INTERACTIVE, FLOOR_DECORATION, BOUNDARY, WALL_DECORATION
	}

	public Location(final Object obj, final Type type, final int plane) {
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

	public Type getType() {
		return type;
	}

	public int getPlane() {
		return plane;
	}

	public Object getInstance() {
		return object;
	}

	public Tile getLocation() {
		final RSInteractableLocation location = ((RSInteractableManager) ((RSInteractableRSInteractableManager) object).getRSInteractableRSInteractableManager()).getData().getLocation();
		return new Tile(Game.getBaseX() + (int) location.getX() / 512, Game.getBaseY() + (int) location.getY() / 512, plane);
	}

	public LocationDefinition getDefinition() {
		return null;//TODO
	}

	public CapturedModel getModel() {
		if (object != null) {
			Model model = Bot.resolve().client.getRSObjectModel(object);
			if (model == null) {
				model = ModelCapture.modelCache.get(object);
			}
			if (model != null) {
				return new LocationModel(model, this);
			}
		}
		return null;
	}

	public boolean verify() {
		return false;//TODO
	}

	public Point getCentralPoint() {
		final CapturedModel model = getModel();
		return model != null ? model.getCentralPoint() : getLocation().getCentralPoint();
	}

	public Point getNextViewportPoint() {
		return null;//TODO
	}

	public boolean contains(Point point) {
		return false;//TODO
	}

	public boolean isOnScreen() {
		final CapturedModel model = getModel();
		return model != null ? model.isOnScreen() : getLocation().isOnScreen();//TODO
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
