package org.powerbot.bot.rt6.client;

public interface Callback {
	void updateRenderInfo(Render render);

	void notifyMessage(int id, String sender, String message);

	void notifyConfig(Object o);

	void updateCamera(RSInteractableLocation offset, RSInteractableLocation center);
}
