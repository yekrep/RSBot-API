package org.powerbot.script.wrappers;

import java.lang.ref.WeakReference;

import org.powerbot.client.AbstractModel;
import org.powerbot.client.RSInteractable;
import org.powerbot.client.RSInteractableData;
import org.powerbot.client.RSInteractableLocation;
import org.powerbot.client.RSItemPile;
import org.powerbot.script.methods.MethodContext;

public class PileModel extends Model {
	private final WeakReference<RSItemPile> pile;

	public PileModel(MethodContext ctx, AbstractModel model, RSItemPile pile) {
		super(ctx, model, pile.getHeight() / 2);
		this.pile = new WeakReference<>(pile);
	}

	@Override
	public int getX() {
		RSInteractable interactable = this.pile.get();
		RSInteractableData data = interactable != null ? interactable.getData() : null;
		RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return (int) location.getX();
		}
		return -1;
	}

	@Override
	public int getY() {
		RSInteractable interactable = this.pile.get();
		RSInteractableData data = interactable != null ? interactable.getData() : null;
		RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return (int) location.getY();
		}
		return -1;
	}

	@Override
	public byte getPlane() {
		RSInteractable interactable = this.pile.get();
		return interactable != null ? interactable.getPlane() : -1;
	}


	@Override
	public void update() {
	}
}
