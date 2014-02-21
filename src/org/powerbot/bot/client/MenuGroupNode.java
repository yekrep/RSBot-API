package org.powerbot.bot.client;

public interface MenuGroupNode extends NodeSub {
	public NodeSubQueue getItems();

	public int getSize();
}
