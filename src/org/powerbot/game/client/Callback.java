package org.powerbot.game.client;

public interface Callback {
	public void updateRenderInfo(Render render);

	public void notifyMessage(int id, String sender, String message);
}
