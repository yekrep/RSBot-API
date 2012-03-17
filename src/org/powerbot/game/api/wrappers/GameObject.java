package org.powerbot.game.api.wrappers;

import org.powerbot.game.api.methods.Game;
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
public class GameObject {
	private final Object object;
	private final int type, plane;

	public GameObject(final Object obj, final int type, final int plane) {
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

	public GameObjectDefinition getDefinition() {
		return null;//TODO
	}
}
