package org.powerbot.script.wrappers;

import org.powerbot.client.AbstractModel;
import org.powerbot.client.RSInteractable;
import org.powerbot.script.methods.MethodContext;

public class RenderableModel extends Model {
	private final RSInteractable interactable;

	public RenderableModel(MethodContext ctx, final AbstractModel model, final RSInteractable interactable) {
		super(ctx, model);
		this.interactable = interactable;
	}

	@Override
	public int getX() {
		return (int) interactable.getData().getLocation().getX();
	}

	@Override
	public int getY() {
		return (int) interactable.getData().getLocation().getY();
	}

	@Override
	public byte getPlane() {
		return interactable.getPlane();
	}

	@Override
	public void update() {
	}
}
