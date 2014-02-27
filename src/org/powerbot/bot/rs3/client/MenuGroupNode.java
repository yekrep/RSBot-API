package org.powerbot.bot.rs3.client;

public interface MenuGroupNode extends NodeSub {
	public NodeSubQueue getItems();

	public int getSize();
}
