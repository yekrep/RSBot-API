package org.powerbot.script.xenon.wrappers;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import org.powerbot.client.RSCharacter;
import org.powerbot.client.RSInteractableData;
import org.powerbot.client.RSInteractableLocation;
import org.powerbot.script.xenon.Game;

class ActorModel extends Model {
	private final Reference<RSCharacter> character;
	private final int[] x_base, z_base;

	public ActorModel(final org.powerbot.client.Model model, final RSCharacter character) {
		super(model);
		this.character = new WeakReference<>(character);
		x_base = xPoints;
		z_base = zPoints;
		xPoints = new int[numVertices];
		zPoints = new int[numVertices];
	}

	@Override
	public int getX() {
		final RSCharacter character = this.character.get();
		final RSInteractableData data = character != null ? character.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) return (int) location.getX();
		return -1;
	}

	@Override
	public int getY() {
		final RSCharacter character = this.character.get();
		final RSInteractableData data = character != null ? character.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) return (int) location.getY();
		return -1;
	}

	@Override
	public byte getPlane() {
		final RSCharacter character = this.character.get();
		return character != null ? character.getPlane() : -1;
	}

	@Override
	public void update() {
		final RSCharacter character = this.character.get();
		if (character == null) return;

		final int theta = character.getOrientation() & 0x3fff;
		final int sin = Game.SIN_TABLE[theta];
		final int cos = Game.COS_TABLE[theta];
		for (int i = 0; i < numVertices; ++i) {
			xPoints[i] = x_base[i] * cos + z_base[i] * sin >> 15;
			zPoints[i] = z_base[i] * cos - x_base[i] * sin >> 15;
		}
	}
}
