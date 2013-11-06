package org.powerbot.script.wrappers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.ref.WeakReference;

import org.powerbot.client.AbstractModel;
import org.powerbot.client.BaseInfo;
import org.powerbot.client.Cache;
import org.powerbot.client.Client;
import org.powerbot.client.HashTable;
import org.powerbot.client.RSGround;
import org.powerbot.client.RSGroundInfo;
import org.powerbot.client.RSInfo;
import org.powerbot.client.RSItem;
import org.powerbot.client.RSItemDefLoader;
import org.powerbot.client.RSItemPile;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;

public class GroundItem extends Interactive implements Renderable, Identifiable, Nameable, Stackable, Locatable, Drawable {
	public static final Color TARGET_COLOR = new Color(255, 255, 0, 75);
	private final Tile tile;
	private final WeakReference<RSItem> item;
	private int faceIndex = -1;

	public GroundItem(MethodContext ctx, Tile tile, RSItem item) {
		super(ctx);
		this.tile = tile;
		this.item = new WeakReference<RSItem>(item);
	}

	@Override
	public Model getModel() {
		return getModel(-1);
	}

	public Model getModel(final int p) {
		Client client = ctx.getClient();
		if (client == null || ctx.game.toolkit.graphicsIndex != 0) {
			return null;
		}
		final RSInfo info;
		final BaseInfo baseInfo;
		final RSGroundInfo groundInfo;
		final RSGround[][][] grounds;
		if ((info = client.getRSGroundInfo()) == null || (baseInfo = info.getBaseInfo()) == null ||
				(groundInfo = info.getRSGroundInfo()) == null || (grounds = groundInfo.getRSGroundArray()) == null) {
			return null;
		}
		final int x = tile.getX() - baseInfo.getX(), y = tile.getY() - baseInfo.getY();
		final int plane = client.getPlane();
		final RSGround ground = plane > -1 && plane < grounds.length &&
				x > -1 && x < grounds[plane].length &&
				y > -1 && y < grounds[plane][x].length ? grounds[plane][x][y] : null;
		if (ground != null) {
			final RSItemPile itemPile = ground.getRSItemPile();
			if (itemPile != null) {
				final RSItemDefLoader defLoader;
				final Cache cache;
				final HashTable table;
				if ((defLoader = client.getRSItemDefLoader()) == null ||
						(cache = defLoader.getModelCache()) == null || (table = cache.getTable()) == null) {
					return null;
				}

				final int graphicsIndex = ctx.game.toolkit.graphicsIndex;
				Object model;
				if (p != -1 && (model = ctx.game.lookup(table, (long) p | (long) graphicsIndex << 29)) != null &&
						model instanceof AbstractModel) {
					return new PileModel(ctx, (AbstractModel) model, itemPile);
				}

				final int[] ids = {itemPile.getID_1(), itemPile.getID_2(), itemPile.getID_3()};
				final AbstractModel[] models = new AbstractModel[ids.length];

				int i = 0;
				for (final int id : ids) {
					if (id < 1) {
						continue;
					}
					model = ctx.game.lookup(table, (long) id | (long) graphicsIndex << 29);
					if (model != null && model instanceof AbstractModel) {
						models[i++] = (AbstractModel) model;
					}
				}

				return i > 0 ? new PileModel(ctx, models[Random.nextInt(0, i)], itemPile) : null;
			}
		}
		return null;
	}

	@Override
	public int getId() {
		RSItem item = this.item.get();
		return item != null ? item.getId() : -1;
	}

	@Override
	public int getStackSize() {
		RSItem item = this.item.get();
		return item != null ? item.getStackSize() : -1;
	}

	@Override
	public String getName() {
		return ItemDefinition.getDef(ctx, getId()).getName();
	}

	public boolean isMembers() {
		return ItemDefinition.getDef(ctx, getId()).isMembers();
	}

	public String[] getActions() {
		return ItemDefinition.getDef(ctx, getId()).getActions();
	}

	public String[] getGroundActions() {
		return ItemDefinition.getDef(ctx, getId()).getGroundActions();
	}

	@Override
	public Tile getLocation() {
		if (item.get() == null) {
			return Tile.NIL;
		}
		return tile;
	}

	@Override
	public boolean isOnScreen() {
		return tile.getMatrix(ctx).isOnScreen() && ctx.game.isPointOnScreen(getInteractPoint());
	}

	@Override
	public Point getInteractPoint() {
		final Model model = getModel(getId());
		if (model != null) {
			Point point = model.getCentroid(faceIndex);
			if (point != null) {
				return point;
			}
			point = model.getCentroid(faceIndex = model.nextTriangle());
			if (point != null) {
				return point;
			}
		}
		return tile.getMatrix(ctx).getInteractPoint();
	}

	@Override
	public Point getNextPoint() {
		final Model model = getModel(getId());
		if (model != null) {
			return model.getNextPoint();
		}
		return tile.getMatrix(ctx).getNextPoint();
	}

	@Override
	public Point getCenterPoint() {
		final Model model = getModel(getId());
		if (model != null) {
			return model.getCenterPoint();
		}
		return tile.getMatrix(ctx).getCenterPoint();
	}

	@Override
	public boolean contains(final Point point) {
		return tile.getMatrix(ctx).contains(point);
	}

	@Override
	public boolean isValid() {
		return ctx.groundItems.select().contains(this);
	}

	@Override
	public int hashCode() {
		final RSItem i;
		return (i = this.item.get()) != null ? System.identityHashCode(i) : 0;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof GroundItem)) {
			return false;
		}
		final GroundItem g = (GroundItem) o;
		if (!this.tile.equals(g.tile)) {
			return false;
		}
		RSItem item1 = this.item.get(), item2 = g.item.get();
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
		final Model m = getModel();
		if (m != null) {
			m.drawWireFrame(render);
		}
	}
}
