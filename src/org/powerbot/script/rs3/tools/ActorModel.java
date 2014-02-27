package org.powerbot.script.rs3.tools;

import java.lang.ref.WeakReference;

import org.powerbot.bot.rs3.client.AbstractModel;
import org.powerbot.bot.rs3.client.RSCharacter;
import org.powerbot.bot.rs3.client.RSInteractableData;
import org.powerbot.bot.rs3.client.RSInteractableLocation;

class ActorModel extends Model {
	private final WeakReference<RSCharacter> character;
	private final int[] x_base, z_base;

	public ActorModel(final MethodContext ctx, final AbstractModel model, final RSCharacter character) {
		super(ctx, model);
		this.character = new WeakReference<RSCharacter>(character);
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
		if (location != null) {
			return (int) location.getX();
		}
		return -1;
	}

	@Override
	public int getY() {
		final RSCharacter character = this.character.get();
		final RSInteractableData data = character != null ? character.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return (int) location.getY();
		}
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
		if (character == null) {
			return;
		}

		final int theta = character.getOrientation() & 0x3fff;
		final int sin = Game.SIN_TABLE[theta];
		final int cos = Game.COS_TABLE[theta];
		for (int i = 0; i < numVertices; ++i) {
			xPoints[i] = x_base[i] * cos + z_base[i] * sin >> 14;
			zPoints[i] = z_base[i] * cos - x_base[i] * sin >> 14;
		}
	}
}
