package org.powerbot.script.rt6;

import java.awt.Color;
import java.awt.Point;

import org.powerbot.bot.rt6.client.ItemNode;
import org.powerbot.script.Actionable;
import org.powerbot.script.Drawable;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Stackable;
import org.powerbot.script.Tile;

public class GroundItem extends Interactive implements Identifiable, Nameable, Stackable, Locatable, Drawable, Actionable {
	public static final Color TARGET_COLOR = new Color(255, 255, 0, 75);
	private final TileMatrix tile;
	private final ItemNode item;

	public GroundItem(final ClientContext ctx, final Tile tile, final ItemNode item) {
		super(ctx);
		this.tile = tile.matrix(ctx);
		boundingModel = this.tile.boundingModel;
		this.item = item;
		bounds(-64, 64, -64, 0, -64, 64);
	}

	@Override
	public void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
		tile.bounds(x1, x2, y1, y2, z1, z2);
	}

	@Override
	public int id() {
		return item.getId();
	}

	@Override
	public int stackSize() {
		return item.getStackSize();
	}

	@Override
	public String name() {
		final CacheItemConfig c = CacheItemConfig.load(Item.CACHE_WORKER, id());
		return c != null ? c.name : "";
	}

	public boolean members() {
		final CacheItemConfig c = CacheItemConfig.load(Item.CACHE_WORKER, id());
		return c != null && c.members;
	}

	@Override
	public String[] actions() {
		final CacheItemConfig c = CacheItemConfig.load(Item.CACHE_WORKER, id());
		return c != null ? c.groundActions : new String[0];
	}

	public String[] backpackActions() {
		final CacheItemConfig c = CacheItemConfig.load(Item.CACHE_WORKER, id());
		return c != null ? c.actions : new String[0];
	}

	@Override
	public Tile tile() {
		return tile.tile();
	}

	@Override
	public Point nextPoint() {
		return tile.nextPoint();
	}

	public Point centerPoint() {
		return tile.centerPoint();
	}

	@Override
	public boolean contains(final Point point) {
		return tile.contains(point);
	}

	@Override
	public boolean valid() {
		return ctx.groundItems.select().contains(this);
	}

	@Override
	public int hashCode() {
		return item.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof GroundItem && tile.equals(((GroundItem) o).tile) && item.equals(((GroundItem) o).item);
	}

	@Override
	public String toString() {
		return GroundItem.class.getSimpleName() + "[id=" + id() + ",stacksize=" + stackSize() + ",name=" + name() + "]";
	}
}
