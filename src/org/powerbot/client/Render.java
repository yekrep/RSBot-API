package org.powerbot.client;

public interface Render {
	public float getAbsoluteX();

	public float getAbsoluteY();

	public float getXMultiplier();

	public float getYMultiplier();

	public int getGraphicsIndex();

	public RenderData getRenderData();
}
