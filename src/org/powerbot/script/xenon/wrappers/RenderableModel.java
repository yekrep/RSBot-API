package org.powerbot.script.xenon.wrappers;

import org.powerbot.game.client.RSInteractable;

public class RenderableModel extends Model {
	private final RSInteractable interactable;

	public RenderableModel(final org.powerbot.game.client.Model model, final RSInteractable interactable) {
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
