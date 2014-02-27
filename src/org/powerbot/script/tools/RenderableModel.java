package org.powerbot.script.tools;

import java.lang.ref.WeakReference;

import org.powerbot.bot.client.AbstractModel;
import org.powerbot.bot.client.RSInteractable;
import org.powerbot.bot.client.RSInteractableData;
import org.powerbot.bot.client.RSInteractableLocation;

public class RenderableModel extends Model {
	private final WeakReference<RSInteractable> interactable;

	public RenderableModel(final MethodContext ctx, final AbstractModel model, final RSInteractable interactable) {
		super(ctx, model);
		this.interactable = new WeakReference<RSInteractable>(interactable);
	}

	@Override
	public int getX() {
		final RSInteractable interactable = this.interactable.get();
		final RSInteractableData data = interactable != null ? interactable.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return (int) location.getX();
		}
		return -1;
	}

	@Override
	public int getY() {
		final RSInteractable interactable = this.interactable.get();
		final RSInteractableData data = interactable != null ? interactable.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return (int) location.getY();
		}
		return -1;
	}

	@Override
	public byte getPlane() {
		final RSInteractable interactable = this.interactable.get();
		return interactable != null ? interactable.getPlane() : -1;
	}


	@Override
	public void update() {
	}
}
