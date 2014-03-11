package org.powerbot.script.rs3;

import java.lang.ref.WeakReference;

import org.powerbot.bot.rs3.client.AbstractModel;
import org.powerbot.bot.rs3.client.RSInteractable;
import org.powerbot.bot.rs3.client.RSInteractableData;
import org.powerbot.bot.rs3.client.RSInteractableLocation;
import org.powerbot.bot.rs3.client.RSItemPile;

public class PileModel extends Model {
	private final WeakReference<RSItemPile> pile;

	public PileModel(final ClientContext ctx, final AbstractModel model, final RSItemPile pile) {
		super(ctx, model, pile.getHeight() / 2);
		this.pile = new WeakReference<RSItemPile>(pile);
	}

	@Override
	public int x() {
		final RSInteractable interactable = this.pile.get();
		final RSInteractableData data = interactable != null ? interactable.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return (int) location.getX();
		}
		return -1;
	}

	@Override
	public int z() {
		final RSInteractable interactable = this.pile.get();
		final RSInteractableData data = interactable != null ? interactable.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return (int) location.getY();
		}
		return -1;
	}

	@Override
	public byte floor() {
		final RSInteractable interactable = this.pile.get();
		return interactable != null ? interactable.getPlane() : -1;
	}


	@Override
	public void update() {
	}
}
