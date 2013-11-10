package org.powerbot.bot;

import org.powerbot.client.Callback;
import org.powerbot.client.RSInteractableLocation;
import org.powerbot.client.RSObjectDef;
import org.powerbot.client.Render;
import org.powerbot.event.MessageEvent;
import org.powerbot.script.methods.Camera;
import org.powerbot.util.math.Vector3f;

public class AbstractCallback implements Callback {
	private final Bot bot;

	public AbstractCallback(final Bot bot) {
		this.bot = bot;
	}

	@Override
	public void updateRenderInfo(final Render render) {
		bot.getMethodContext().game.updateToolkit(render);
	}

	@Override
	public void notifyMessage(final int id, final String sender, final String message) {
		bot.getEventMulticaster().dispatch(new MessageEvent(id, sender, message));
	}

	@Override
	public void notifyObjectDefinitionLoad(final RSObjectDef def) {
		bot.getMethodContext().objects.setType(def.getID(), def.getClippingType());
	}

	@Override
	public void updateCamera(final RSInteractableLocation offset, final RSInteractableLocation center) {
		final Camera camera = bot.getMethodContext().camera;
		camera.offset = new Vector3f(offset.getX(), offset.getY(), offset.getZ());
		camera.center = new Vector3f(center.getX(), center.getY(), center.getZ());
	}
}
