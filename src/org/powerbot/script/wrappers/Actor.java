package org.powerbot.script.wrappers;

import java.awt.Point;

import org.powerbot.bot.World;
import org.powerbot.client.Client;
import org.powerbot.client.CombatStatus;
import org.powerbot.client.CombatStatusData;
import org.powerbot.client.LinkedListNode;
import org.powerbot.client.RSAnimator;
import org.powerbot.client.RSCharacter;
import org.powerbot.client.RSInteractableData;
import org.powerbot.client.RSInteractableLocation;
import org.powerbot.client.RSMessageData;
import org.powerbot.client.RSNPC;
import org.powerbot.client.RSNPCNode;
import org.powerbot.client.RSPlayer;
import org.powerbot.client.Sequence;
import org.powerbot.script.methods.Game;

public abstract class Actor extends Interactive implements Locatable, Drawable {
	private int faceIndex = -1;

	protected abstract RSCharacter getAccessor();

	public Model getModel() {
		final RSCharacter character = getAccessor();
		if (character != null) {
			final org.powerbot.client.Model model = character.getModel();
			if (model != null) return new ActorModel(model, character);
		}
		return null;
	}

	public abstract String getName();

	public abstract int getLevel();

	public int getOrientation() {
		final RSCharacter character = getAccessor();
		return character != null ? (630 - character.getOrientation() * 45 / 2048) % 360 : 0;
	}

	public int getHeight() {
		final RSCharacter character = getAccessor();
		return character != null ? character.getHeight() : 0;
	}

	public int getAnimation() {
		final RSCharacter character = getAccessor();
		if (character == null) return -1;

		final RSAnimator animator = character.getAnimation();
		final Sequence sequence;
		if (animator == null || (sequence = animator.getSequence()) == null) return -1;
		return sequence.getID();
	}

	public int getStance() {
		final RSCharacter character = getAccessor();
		if (character == null) return -1;

		final RSAnimator animator = character.getPassiveAnimation();
		final Sequence sequence;
		if (animator == null || (sequence = animator.getSequence()) == null) return -1;
		return sequence.getID();
	}

	public int[] getAnimationQueue() {
		final RSCharacter character = getAccessor();
		if (character == null) return new int[0];

		final int[] arr = character.getAnimationQueue();
		return arr != null ? arr : new int[0];
	}

	public int getSpeed() {
		final RSCharacter character = getAccessor();
		return character != null ? character.isMoving() : 0;
	}

	public boolean isInMotion() {
		return getSpeed() != 0;
	}

	public String getMessage() {
		final RSCharacter character = getAccessor();
		if (character == null) return null;

		final RSMessageData headMessage = character.getMessageData();
		return headMessage != null ? headMessage.getMessage() : null;
	}

	public Actor getInteracting() {
		final RSCharacter character = getAccessor();
		final int index = character != null ? character.getInteracting() : -1;
		if (index == -1) {
			return null;
		}
		final Client client = World.getWorld().getClient();
		if (client == null) return null;
		if (index < 32768) {
			final Object npcNode = Game.lookup(client.getRSNPCNC(), index);
			if (npcNode == null) {
				return null;
			}
			if (npcNode instanceof RSNPCNode) {
				return new Npc(((RSNPCNode) npcNode).getRSNPC());
			} else if (npcNode instanceof RSNPC) return new Npc((RSNPC) npcNode);
			return null;
		} else {
			final int pos = index - 32768;
			final RSPlayer[] players = client.getRSPlayerArray();
			return pos >= 0 && pos < players.length ? new Player(players[pos]) : null;
		}
	}

	public int getAdrenalineRatio() {
		final CombatStatusData[] data = getBarData();
		if (data == null || data[0] == null) return 0;
		return data[0].getHPRatio();
	}

	public int getHealthRatio() {
		final CombatStatusData[] data = getBarData();
		if (data == null || data[1] == null) return 100;
		return data[1].getHPRatio();
	}

	public int getAdrenalinePercent() {
		final CombatStatusData[] data = getBarData();
		if (data == null || data[0] == null) return 0;
		return toPercent(data[0].getHPRatio());
	}

	public int getHealthPercent() {
		final CombatStatusData[] data = getBarData();
		if (data == null || data[1] == null) return 100;
		return toPercent(data[1].getHPRatio());
	}

	public boolean isInCombat() {
		final Client client = World.getWorld().getClient();
		if (client == null) return false;
		final CombatStatusData[] data = getBarData();
		return data != null && data[1] != null && data[1].getLoopCycleStatus() < client.getLoopCycle();
	}

	@Override
	public Tile getLocation() {
		final RSCharacter character = getAccessor();
		final RSInteractableData data = character != null ? character.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			final Tile base = Game.getMapBase();
			return base != null ? base.derive((int) location.getX() >> 9, (int) location.getY() >> 9, character.getPlane()) : null;
		}
		return null;
	}

	@Override
	public Point getInteractPoint() {
		final Model model = getModel();
		if (model != null) {
			Point point = model.getCentroid(faceIndex);
			if (point != null) return point;
			point = model.getCentroid(faceIndex = model.nextTriangle());
			if (point != null) return point;
		}
		return getScreenPoint();
	}

	@Override
	public Point getNextPoint() {
		final Model model = getModel();
		if (model != null) return model.getNextPoint();
		return getScreenPoint();
	}

	@Override
	public Point getCenterPoint() {
		final Model model = getModel();
		if (model != null) return model.getCenterPoint();
		return getScreenPoint();
	}

	@Override
	public boolean contains(final Point point) {
		final Model model = getModel();
		if (model != null) return model.contains(point);
		return point.distance(getScreenPoint()) < 15d;
	}

	private Point getScreenPoint() {
		final RSCharacter character = getAccessor();
		final RSInteractableData data = character != null ? character.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return Game.groundToScreen((int) location.getX(), (int) location.getY(), character.getPlane(), character.getHeight() / 2);
		}
		return new Point(-1, -1);
	}

	private LinkedListNode[] getBarNodes() {
		final RSCharacter accessor = getAccessor();
		if (accessor == null) return null;
		final org.powerbot.client.LinkedList barList = accessor.getCombatStatusList();
		if (barList == null) return null;
		final LinkedListNode tail = barList.getTail();
		LinkedListNode health, adrenaline, current;
		current = tail.getNext();
		if (current.getNext() != tail) {
			adrenaline = current;
			health = current.getNext();
		} else {
			adrenaline = null;
			health = current;
		}

		return new LinkedListNode[]{adrenaline, health};
	}

	private CombatStatusData[] getBarData() {
		LinkedListNode[] nodes = getBarNodes();
		if (nodes == null) return null;
		CombatStatusData[] data = new CombatStatusData[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] == null || !(nodes[i] instanceof CombatStatus)) {
				data[i] = null;
				continue;
			}
			CombatStatus status = (CombatStatus) nodes[i];
			org.powerbot.client.LinkedList statuses = status.getData();
			if (statuses == null) {
				data[i] = null;
				continue;
			}

			LinkedListNode node = statuses.getTail().getNext();
			if (node == null || !(node instanceof CombatStatusData)) {
				data[i] = null;
				continue;
			}
			data[i] = (CombatStatusData) node;
		}
		return data;
	}

	private int toPercent(final int ratio) {
		return (int) Math.ceil((ratio * 100) / 0xff);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Actor)) return false;
		final Actor c = (Actor) o;
		final RSCharacter i;
		return (i = this.getAccessor()) != null && i == c.getAccessor();
	}
}
