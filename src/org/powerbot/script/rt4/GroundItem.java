package org.powerbot.script.rt4;

import java.awt.Color;
import java.awt.Point;
import java.lang.ref.WeakReference;

import org.powerbot.bot.rt4.client.ItemNode;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Tile;
import org.powerbot.script.Validatable;

public class GroundItem extends Interactive implements Nameable, Locatable, Identifiable, Validatable {
	public static final Color TARGET_COLOR = new Color(255, 255, 0, 75);
	private final TileMatrix tile;
	private final WeakReference<ItemNode> node;
	private final int hash;

	GroundItem(final ClientContext ctx, final Tile tile, final ItemNode node) {
		super(ctx);
		this.tile = tile.matrix(ctx);
		boundingModel = this.tile.boundingModel;
		this.node = new WeakReference<ItemNode>(node);
		bounds(-16, 16, -16, 0, -16, 16);
		hash = System.identityHashCode(node);
	}

	@Override
	public void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
		tile.bounds(x1, x2, y1, y2, z1, z2);
	}

	@Override
	public int id() {
		final ItemNode node = this.node.get();
		return node != null ? node.getItemId() : -1;
	}

	public int stackSize() {
		final ItemNode node = this.node.get();
		return node != null ? node.getStackSize() : -1;
	}

	@Override
	public String name() {
		return "";//TODO
	}

	@Override
	public Point centerPoint() {
		return tile.centerPoint();
	}

	@Override
	public Point nextPoint() {
		return tile.nextPoint();
	}

	@Override
	public boolean contains(final Point point) {
		return tile.contains(point);
	}

	@Override
	public Tile tile() {
		return tile.tile();
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof GroundItem && hashCode() == o.hashCode();
	}

	@Override
	public String toString() {
		return String.format("%s[id=%d,stack=%d,tile=%s]", GroundItem.class.getName(), id(), stackSize(), tile.tile().toString());
	}
}
