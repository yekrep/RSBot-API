package org.powerbot.script.wrappers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.ref.WeakReference;

import org.powerbot.bot.client.AbstractModel;
import org.powerbot.bot.client.BaseInfo;
import org.powerbot.bot.client.Cache;
import org.powerbot.bot.client.Client;
import org.powerbot.bot.client.HashTable;
import org.powerbot.bot.client.RSGround;
import org.powerbot.bot.client.RSGroundInfo;
import org.powerbot.bot.client.RSInfo;
import org.powerbot.bot.client.RSItem;
import org.powerbot.bot.client.RSItemDefLoader;
import org.powerbot.bot.client.RSItemPile;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;

public class GroundItem extends Interactive implements Renderable, Identifiable, Nameable, Stackable, Locatable, Drawable {
	public static final Color TARGET_COLOR = new Color(255, 255, 0, 75);
	private final Tile tile;
	private final WeakReference<RSItem> item;
	private int faceIndex = -1;

	public GroundItem(final MethodContext ctx, final Tile tile, final RSItem item) {
		super(ctx);
		this.tile = tile;
		this.item = new WeakReference<RSItem>(item);
	}

	@Override
	public void setBounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
		boundingModel.set(new BoundingModel(ctx, x1, x2, y1, y2, z1, z2) {
			@Override
			public int getX() {
				final Tile base = ctx.game.getMapBase();
				return ((tile.x - base.x) * 512) + 256;
			}

			@Override
			public int getZ() {
				final Tile base = ctx.game.getMapBase();
				return ((tile.y - base.y) * 512) + 256;
			}
		});
	}

	@Override
	public Model getModel() {
		return getModel(-1);
	}

	public Model getModel(final int p) {
		final Client client = ctx.getClient();
		if (client == null || ctx.game.toolkit.gameMode != 0) {
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
		final RSItem item = this.item.get();
		return item != null ? item.getId() : -1;
	}

	@Override
	public int getStackSize() {
		final RSItem item = this.item.get();
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
	public boolean isInViewport() {
		return tile.getMatrix(ctx).isInViewport() && ctx.game.isPointInViewport(getInteractPoint());
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
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.getNextPoint();
		}
		return tile.getMatrix(ctx).getInteractPoint();
	}

	@Override
	public Point getNextPoint() {
		final Model model = getModel(getId());
		if (model != null) {
			return model.getNextPoint();
		}
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.getNextPoint();
		}
		return tile.getMatrix(ctx).getNextPoint();
	}

	@Override
	public Point getCenterPoint() {
		final Model model = getModel(getId());
		if (model != null) {
			return model.getCenterPoint();
		}
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.getCenterPoint();
		}
		return tile.getMatrix(ctx).getCenterPoint();
	}

	@Override
	public boolean contains(final Point point) {
		final Model model = getModel(getId());
		if (model != null) {
			return model.contains(point);
		}
		final BoundingModel model2 = boundingModel.get();
		if (model2 != null) {
			return model2.contains(point);
		}
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
		final RSItem item1 = this.item.get();
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
		final Model m = getModel();
		if (m != null) {
			m.drawWireFrame(render);
		} else {
			final BoundingModel m2 = boundingModel.get();
			if (m2 != null) {
				m2.drawWireFrame(render);
			}
		}
	}
}
