package org.powerbot.os.api.wrappers;

import java.awt.Color;
import java.lang.ref.WeakReference;

import org.powerbot.os.client.ItemNode;

public class GroundItem {
	public static final Color TARGET_COLOR = new Color(255, 255, 0, 75);
	private final Tile tile;
	private final WeakReference<ItemNode> node;

	public GroundItem(final Tile tile, final ItemNode node) {
		this.tile = tile;
		this.node = new WeakReference<ItemNode>(node);
	}
}
