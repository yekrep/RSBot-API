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

	public PileModel(final MethodContext ctx, final AbstractModel model, final RSItemPile pile) {
		super(ctx, model, pile.getHeight() / 2);
		this.pile = new WeakReference<RSItemPile>(pile);
	}

	@Override
	public int getX() {
		final RSInteractable interactable = this.pile.get();
		final RSInteractableData data = interactable != null ? interactable.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return (int) location.getX();
		}
		return -1;
	}

	@Override
	public int getY() {
		final RSInteractable interactable = this.pile.get();
		final RSInteractableData data = interactable != null ? interactable.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return (int) location.getY();
		}
		return -1;
	}

	@Override
	public byte getPlane() {
		final RSInteractable interactable = this.pile.get();
		return interactable != null ? interactable.getPlane() : -1;
	}


	@Override
	public void update() {
	}
}
