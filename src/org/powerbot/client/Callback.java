package org.powerbot.client;

public interface Callback {
	public void updateRenderInfo(final Render render);

	public void notifyMessage(final int id, final String sender, final String message);

	public void notifyObjectDefinitionLoad(final RSObjectDef def);
}
