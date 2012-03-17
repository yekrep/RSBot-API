package org.powerbot.game.api.wrappers;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.powerbot.game.api.Multipliers;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.util.internal.Nodes;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.Model;
import org.powerbot.game.client.ModelCapture;
import org.powerbot.game.client.RSAnimatorSequence;
import org.powerbot.game.client.RSCharacterAnimation;
import org.powerbot.game.client.RSCharacterHPRatio;
import org.powerbot.game.client.RSCharacterHeight;
import org.powerbot.game.client.RSCharacterInteracting;
import org.powerbot.game.client.RSCharacterIsMoving;
import org.powerbot.game.client.RSCharacterOrientation;
import org.powerbot.game.client.RSCharacterPassiveAnimation;
import org.powerbot.game.client.RSInteractableInts;
import org.powerbot.game.client.RSInteractableLocation;
import org.powerbot.game.client.RSInteractableManager;
import org.powerbot.game.client.RSInteractableRSInteractableManager;
import org.powerbot.game.client.RSNPCHolder;
import org.powerbot.game.client.RSNPCNode;
import org.powerbot.game.client.RSNPCNodeHolder;
import org.powerbot.game.client.SequenceID;
import org.powerbot.game.client.SequenceInts;

/**
 * @author Timer
 */
public abstract class Character implements Entity {
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
		final RSInteractableLocation location = ((RSInteractableManager) ((RSInteractableRSInteractableManager) get()).getRSInteractableRSInteractableManager()).getData().getLocation();
		return new Tile(Game.getBaseX() + ((int) location.getX() >> 9), Game.getBaseY() + ((int) location.getY() >> 9), Game.getFloor());//TODO plane
	}

	public int getPlane() {
		return Game.getFloor();//TODO plane
	}

	public Character getInteracting() {
		final int index = ((RSCharacterInteracting) ((RSInteractableInts) get()).getRSInteractableInts()).getRSCharacterInteracting() * multipliers.CHARACTER_INTERACTING;
		if (index == -1) {
			return null;
		}
		if (index < 0x8000) {
			return new Npc(((RSNPCHolder) ((RSNPCNodeHolder) ((RSNPCNode) Nodes.lookup(client.getRSNPCNC(), index)).getData()).getRSNPCNodeHolder()).getRSNPC());
		} else {
			return new Player(client.getRSPlayerArray()[index - 0x8000]);
		}
	}

	public int getAnimation() {
		final Object animation = ((RSCharacterAnimation) get()).getRSCharacterAnimation();
		if (animation != null) {
			final Object sequence = ((RSAnimatorSequence) animation).getRSAnimatorSequence();
			if (sequence != null) {
				return ((SequenceID) ((SequenceInts) sequence).getSequenceInts()).getSequenceID() * multipliers.SEQUENCE_ID;
			}
		}
		return -1;
	}

	public int getPassiveAnimation() {
		final Object animation = ((RSCharacterPassiveAnimation) get()).getRSCharacterPassiveAnimation();
		if (animation != null) {
			final Object sequence = ((RSAnimatorSequence) animation).getRSAnimatorSequence();
			if (sequence != null) {
				return ((SequenceID) ((SequenceInts) sequence).getSequenceInts()).getSequenceID() * multipliers.SEQUENCE_ID;
			}
		}
		return -1;
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
		return null;//TODO
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

	public GameModel getModel() {
		final Object ref = get();
		if (ref != null) {
			final Model model = ModelCapture.modelCache.get(ref);
			return new CharacterModel(model, this);
		}
		return null;
	}

	protected abstract Object get();

	public boolean verify() {
		return get() != null;
	}

	public Point getCentralPoint() {//TODO
		final RSInteractableLocation location = ((RSInteractableManager) ((RSInteractableRSInteractableManager) get()).getRSInteractableRSInteractableManager()).getData().getLocation();
		return Calculations.groundToScreen((int) location.getX(), (int) location.getY(), Game.getFloor(), -getHeight() / 2);
	}

	public Point getNextViewportPoint() {
		return getCentralPoint();//TODO
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

	public boolean click(final boolean left) {
		return false;//TODO
	}

	public boolean interact(final String action) {
		return false;//TODO
	}

	public boolean interact(final String action, final String option) {
		return false;//TODO
	}

	public void draw(final Graphics render) {
		//TODO
	}
}
