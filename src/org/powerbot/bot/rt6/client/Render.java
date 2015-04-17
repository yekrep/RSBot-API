package org.powerbot.bot.rt6.client;

public interface Render {
	float getAbsoluteX();

	float getAbsoluteY();

	float getXMultiplier();

	float getYMultiplier();

	int getGraphicsIndex();

	RenderData getRenderData();
}
