package org.powerbot.script.wrappers;

import java.lang.ref.WeakReference;

import org.powerbot.client.AbstractModel;
import org.powerbot.client.RSCharacter;
import org.powerbot.client.RSInteractableData;
import org.powerbot.client.RSInteractableLocation;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.MethodContext;

class ActorModel extends Model {
	private final WeakReference<RSCharacter> character;
	private final int[] x_base, z_base;

	public ActorModel(MethodContext ctx, AbstractModel model, RSCharacter character) {
		super(ctx, model);
		this.character = new WeakReference<RSCharacter>(character);
		x_base = xPoints;
		z_base = zPoints;
		xPoints = new int[numVertices];
		zPoints = new int[numVertices];
	}

	@Override
	public int getX() {
		RSCharacter character = this.character.get();
		RSInteractableData data = character != null ? character.getData() : null;
		RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return (int) location.getX();
		}
		return -1;
	}

	@Override
	public int getY() {
		RSCharacter character = this.character.get();
		RSInteractableData data = character != null ? character.getData() : null;
		RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return (int) location.getY();
		}
		return -1;
	}

	@Override
	public byte getPlane() {
		RSCharacter character = this.character.get();
		return character != null ? character.getPlane() : -1;
	}

	@Override
	public void update() {
		RSCharacter character = this.character.get();
		if (character == null) {
			return;
		}

		int theta = character.getOrientation() & 0x3fff;
		int sin = Game.SIN_TABLE[theta];
		int cos = Game.COS_TABLE[theta];
		for (int i = 0; i < numVertices; ++i) {
			xPoints[i] = x_base[i] * cos + z_base[i] * sin >> 14;
			zPoints[i] = z_base[i] * cos - x_base[i] * sin >> 14;
		}
	}
}
