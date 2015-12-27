package org.powerbot.script.rt4;

import java.awt.Color;
import java.awt.Point;

import org.powerbot.bot.rt4.client.ItemNode;
import org.powerbot.script.Actionable;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Tile;
import org.powerbot.script.Validatable;

public class GroundItem extends Interactive implements Nameable, Locatable, Identifiable, Validatable, Actionable {
	public static final Color TARGET_COLOR = new Color(255, 255, 0, 75);
	private final TileMatrix tile;
	private final ItemNode node;

	GroundItem(final ClientContext ctx, final Tile tile, final ItemNode node) {
		super(ctx);//TODO: valid
		this.tile = tile.matrix(ctx);
		boundingModel = this.tile.boundingModel;
		this.node = node;
		bounds(-16, 16, -16, 0, -16, 16);
	}

	@Override
	public void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
		tile.bounds(x1, x2, y1, y2, z1, z2);
	}

	@Override
	public int id() {
		return node.getItemId();
	}

	public int stackSize() {
		return node.getStackSize();
	}

	@Override
	public String name() {
		return ItemConfig.getDef(ctx, id()).getName();
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
		return node.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof GroundItem && tile.equals(((GroundItem) o).tile) && node.equals(((GroundItem) o).node);
	}

	@Override
	public String toString() {
		return String.format("%s[id=%d,stack=%d,tile=%s]", GroundItem.class.getName(), id(), stackSize(), tile.tile().toString());
	}

	@Override
	public String[] actions() {
		return ItemConfig.getDef(ctx, id()).getGroundActions();
	}

	public String[] inventoryActions() {
		return ItemConfig.getDef(ctx, id()).getActions();
	}
}
