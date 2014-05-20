package org.powerbot.script.rt6;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.ref.WeakReference;

import org.powerbot.bot.rt6.client.RSItem;
import org.powerbot.script.Drawable;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Stackable;
import org.powerbot.script.Tile;

public class GroundItem extends Interactive implements Identifiable, Nameable, Stackable, Locatable, Drawable {
	public static final Color TARGET_COLOR = new Color(255, 255, 0, 75);
	private final TileMatrix tile;
	private final WeakReference<RSItem> item;

	public GroundItem(final ClientContext ctx, final Tile tile, final RSItem item) {
		super(ctx);
		this.tile = tile.matrix(ctx);
		boundingModel = this.tile.boundingModel;
		this.item = new WeakReference<RSItem>(item);
		bounds(-64, 64, -64, 0, -64, 64);
	}

	@Override
	public void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
		tile.bounds(x1, x2, y1, y2, z1, z2);
	}

	@Override
	public int id() {
		final RSItem item = this.item.get();
		return item != null ? item.getId() : -1;
	}

	@Override
	public int stackSize() {
		final RSItem item = this.item.get();
		return item != null ? item.getStackSize() : -1;
	}

	@Override
	public String name() {
		return ItemDefinition.getDef(ctx, id()).getName();
	}

	public boolean members() {
		return ItemDefinition.getDef(ctx, id()).isMembers();
	}

	public String[] actions() {
		return ItemDefinition.getDef(ctx, id()).getActions();
	}

	public String[] groundActions() {
		return ItemDefinition.getDef(ctx, id()).getGroundActions();
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
		final RSItem i;
		return (i = item.get()) != null ? System.identityHashCode(i) : 0;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof GroundItem)) {
			return false;
		}
		final GroundItem g = (GroundItem) o;
		if (!tile.equals(g.tile)) {
			return false;
		}
		final RSItem item1 = item.get();
		final RSItem item2 = g.item.get();
		return item1 != null && item2 != null && item1 == item2;
	}

	@Override
	public void draw(final Graphics render) {
		draw(render, 75);
	}

	@Override
	public void draw(final Graphics render, final int alpha) {
		Color c = TARGET_COLOR;
		final int rgb = c.getRGB();
		if (((rgb >> 24) & 0xff) != alpha) {
			c = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, alpha);
		}
		render.setColor(c);
		final BoundingModel m2 = boundingModel.get();
		if (m2 != null) {
			m2.drawWireFrame(render);
		}
	}

	@Override
	public String toString() {
		return GroundItem.class.getSimpleName() + "[id=" + id() + ",stacksize=" + stackSize() + ",name=" + name() + "]";
	}
}
