package org.powerbot.script.os;

import java.awt.Color;
import java.awt.Point;
import java.lang.ref.WeakReference;

import org.powerbot.bot.os.client.ItemNode;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Tile;
import org.powerbot.script.Validatable;

public class GroundItem extends Interactive implements Nameable, Locatable, Identifiable, Validatable {
	public static final Color TARGET_COLOR = new Color(255, 255, 0, 75);
	private final Tile tile;
	private final WeakReference<ItemNode> node;

	GroundItem(final ClientContext ctx, final Tile tile, final ItemNode node) {
		super(ctx);
		this.tile = tile;
		this.node = new WeakReference<ItemNode>(node);
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
		return new TileMatrix(ctx, tile).centerPoint();
	}

	@Override
	public Point nextPoint() {
		return new TileMatrix(ctx, tile).nextPoint();
	}

	@Override
	public boolean contains(final Point point) {
		return new TileMatrix(ctx, tile).contains(point);
	}

	@Override
	public Tile tile() {
		return tile;
	}
}
