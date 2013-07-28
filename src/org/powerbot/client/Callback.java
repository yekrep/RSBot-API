package org.powerbot.client;

public interface Callback {
	public void updateMinimapAngle(int angle);

	public void updateRenderInfo(Render render);

	public void notifyMessage(int id, String sender, String message);

	public void notifyObjectDefinitionLoad(RSObjectDef def);
}
