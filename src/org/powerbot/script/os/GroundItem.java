package org.powerbot.script.os;

import java.awt.Color;
import java.awt.Point;
import java.lang.ref.WeakReference;

import org.powerbot.bot.os.client.ItemNode;

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
	public int getId() {
		final ItemNode node = this.node.get();
		return node != null ? node.getItemId() : -1;
	}

	public int getStackSize() {
		final ItemNode node = this.node.get();
		return node != null ? node.getStackSize() : -1;
	}

	@Override
	public String getName() {
		return "";//TODO
	}

	@Override
	public Point getCenterPoint() {
		return tile.getMatrix(ctx).getCenterPoint();
	}

	@Override
	public Point getNextPoint() {
		return tile.getMatrix(ctx).getNextPoint();
	}

	@Override
	public boolean contains(final Point point) {
		return tile.getMatrix(ctx).contains(point);
	}

	@Override
	public Tile getLocation() {
		return tile;
	}
}
