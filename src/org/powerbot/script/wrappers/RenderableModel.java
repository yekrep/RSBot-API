package org.powerbot.script.wrappers;

import java.lang.ref.WeakReference;

import org.powerbot.client.AbstractModel;
import org.powerbot.client.RSInteractable;
import org.powerbot.client.RSInteractableData;
import org.powerbot.client.RSInteractableLocation;
import org.powerbot.script.methods.MethodContext;

public class RenderableModel extends Model {
	private final WeakReference<RSInteractable> interactable;

	public RenderableModel(MethodContext ctx, AbstractModel model, RSInteractable interactable) {
		super(ctx, model);
		this.interactable = new WeakReference<>(interactable);
	}

	@Override
	public int getX() {
		RSInteractable interactable = this.interactable.get();
		RSInteractableData data = interactable != null ? interactable.getData() : null;
		RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return (int) location.getX();
		}
		return -1;
	}

	@Override
	public int getY() {
		RSInteractable interactable = this.interactable.get();
		RSInteractableData data = interactable != null ? interactable.getData() : null;
		RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return (int) location.getY();
		}
		return -1;
	}

	@Override
	public byte getPlane() {
		RSInteractable interactable = this.interactable.get();
		return interactable != null ? interactable.getPlane() : -1;
	}


	@Override
	public void update() {
	}
}
