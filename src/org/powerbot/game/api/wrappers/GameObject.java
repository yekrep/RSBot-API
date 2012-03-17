package org.powerbot.game.api.wrappers;

import org.powerbot.game.api.methods.Game;
import org.powerbot.game.bot.Bot;
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

	public Object getArea() {
		return null;//TODO
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
