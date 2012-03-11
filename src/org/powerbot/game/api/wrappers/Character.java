package org.powerbot.game.api.wrappers;

import org.powerbot.game.api.Multipliers;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSAnimatorSequence;
import org.powerbot.game.client.RSCharacterAnimation;
import org.powerbot.game.client.RSCharacterHPRatio;
import org.powerbot.game.client.RSCharacterHeight;
import org.powerbot.game.client.RSCharacterInteracting;
import org.powerbot.game.client.RSCharacterIsMoving;
import org.powerbot.game.client.RSCharacterMessageData;
import org.powerbot.game.client.RSCharacterOrientation;
import org.powerbot.game.client.RSCharacterPassiveAnimation;
import org.powerbot.game.client.RSInteractableInts;
import org.powerbot.game.client.RSInteractableLocation;
import org.powerbot.game.client.RSInteractableManager;
import org.powerbot.game.client.RSInteractableRSInteractableManager;
import org.powerbot.game.client.RSMessageDataMessage;
import org.powerbot.game.client.SequenceID;
import org.powerbot.game.client.SequenceInts;

/**
 * @author Timer
 */
public abstract class Character {
	private final Client client;
	private final Multipliers multipliers;

	public Character() {
		final Bot bot = Bot.resolve();
		this.client = bot.client;
		this.multipliers = bot.multipliers;
	}

	public abstract int getLevel();

	public abstract String getName();

	public Tile getLocation() {
		final RSInteractableLocation location = ((RSInteractableManager) ((RSInteractableRSInteractableManager) get()).getRSInteractableManager()).getData().getLocation();
		return new Tile(Game.getBaseX() + (int) location.getX() >> 9, Game.getBaseY() + (int) location.getY() >> 9);
	}

	public Character getInteracting() {
		final int index = ((RSCharacterInteracting) ((RSInteractableInts) get()).getRSInteractableInts()).getRSCharacterInteracting() * multipliers.CHARACTER_INTERACTING;
		if (index < 0x8000) {
			//TODO NPC
		} else {
			return new Player(client.getRSPlayerArray()[index - 0x8000]);
		}
		return null;
	}

	public int getAnimation() {
		return ((SequenceID) ((SequenceInts) ((RSAnimatorSequence) ((RSCharacterAnimation) get()).getRSCharacterAnimation()).getRSAnimatorSequence()).getSequenceInts()).getSequenceID() * multipliers.SEQUENCE_ID;
	}

	public int getPassiveAnimation() {
		return ((SequenceID) ((SequenceInts) ((RSAnimatorSequence) ((RSCharacterPassiveAnimation) get()).getRSCharacterPassiveAnimation()).getRSAnimatorSequence()).getSequenceInts()).getSequenceID() * multipliers.SEQUENCE_ID;
	}

	public int getHeight() {
		return ((RSCharacterHeight) ((RSInteractableInts) get()).getRSInteractableInts()).getRSCharacterHeight() * multipliers.CHARACTER_HEIGHT;
	}

	public int getOrientation() {
		return ((RSCharacterOrientation) ((RSInteractableInts) get()).getRSInteractableInts()).getRSCharacterOrientation() * multipliers.CHARACTER_ORIENTATION;
	}

	public boolean isInCombat() {
		return false;//TODO combat
	}

	public String getMessage() {
		return (String) ((RSMessageDataMessage) ((RSCharacterMessageData) get()).getRSCharacterMessageData()).getRSMessageDataMessage();
	}

	public int getHpPercent() {
		final int ratio = ((RSCharacterHPRatio) ((RSInteractableInts) get()).getRSInteractableInts()).getRSCharacterHPRatio() * multipliers.CHARACTER_HPRATIO;
		return (int) Math.ceil(isInCombat() ? (ratio * 100) / 0xff : 100);
	}

	public int getSpeed() {
		return ((RSCharacterIsMoving) ((RSInteractableInts) get()).getRSInteractableInts()).getRSCharacterIsMoving() * multipliers.CHARACTER_ISMOVING;
	}

	public boolean isMoving() {
		return getSpeed() != 0;
	}

	protected abstract Object get();
}
