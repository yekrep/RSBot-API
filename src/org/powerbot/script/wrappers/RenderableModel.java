package org.powerbot.script.wrappers;

import org.powerbot.client.RSInteractable;

public class RenderableModel extends Model {
	private final RSInteractable interactable;

	public RenderableModel(final org.powerbot.client.Model model, final RSInteractable interactable) {
		super(model);
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
