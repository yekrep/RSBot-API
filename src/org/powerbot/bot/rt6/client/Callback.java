package org.powerbot.bot.rt6.client;

public interface Callback {
	public void updateRenderInfo(Render render);

	public void notifyMessage(int id, String sender, String message);

	public void notifyConfig(Object o);

	public void updateCamera(RSInteractableLocation offset, RSInteractableLocation center);
}
