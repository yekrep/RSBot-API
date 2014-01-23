package org.powerbot.os.api.wrappers;

import org.powerbot.os.client.ItemNode;

public class GroundItem {
	private final Tile tile;
	private final ItemNode node;

	public GroundItem(final Tile tile, final ItemNode node) {
		this.tile = tile;
		this.node = node;
	}
}
