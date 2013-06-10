package org.powerbot.script.wrappers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.ref.WeakReference;

import org.powerbot.bot.ClientFactory;
import org.powerbot.client.BaseInfo;
import org.powerbot.client.Cache;
import org.powerbot.client.Client;
import org.powerbot.client.HashTable;
import org.powerbot.client.RSGround;
import org.powerbot.client.RSGroundInfo;
import org.powerbot.client.RSInfo;
import org.powerbot.client.RSItem;
import org.powerbot.client.RSItemDef;
import org.powerbot.client.RSItemDefLoader;
import org.powerbot.client.RSItemPile;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.World;
import org.powerbot.script.util.Filters;
import org.powerbot.script.util.Random;

public class GroundItem extends Interactive implements Locatable, Drawable {
	public static final Color TARGET_COLOR = new Color(255, 255, 0, 75);
	private final Tile tile;
	private final WeakReference<RSItem> item;
	private int faceIndex = -1;

	public GroundItem(Tile tile, RSItem item) {
		this.tile = tile;
		this.item = new WeakReference<>(item);
	}

	public Model getModel() {
		return getModel(-1);
	}

	public Model getModel(final int p) {
		final Client client = ClientFactory.getFactory().getClient();
		if (client == null) return null;
		final RSInfo info;
		final BaseInfo baseInfo;
		final RSGroundInfo groundInfo;
		final RSGround[][][] grounds;
		if ((info = client.getRSGroundInfo()) == null || (baseInfo = info.getBaseInfo()) == null ||
				(groundInfo = info.getRSGroundInfo()) == null || (grounds = groundInfo.getRSGroundArray()) == null)
			return null;
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
						(cache = defLoader.getModelCache()) == null || (table = cache.getTable()) == null)
					return null;

				final int graphicsIndex = ClientFactory.getFactory().getToolkit().graphicsIndex;
				Object model;
				if (p != -1 && (model = Game.lookup(table, (long) p | (long) graphicsIndex << 29)) != null &&
						model instanceof org.powerbot.client.Model) {
					return new RenderableModel((org.powerbot.client.Model) model, itemPile);
				}

				final int[] ids = {itemPile.getID_1(), itemPile.getID_2(), itemPile.getID_3()};
				final org.powerbot.client.Model[] models = new org.powerbot.client.Model[ids.length];

				int i = 0;
				for (final int id : ids) {
					if (id < 1) continue;
					model = Game.lookup(table, (long) id | (long) graphicsIndex << 29);
					if (model != null && model instanceof org.powerbot.client.Model)
						models[i++] = (org.powerbot.client.Model) model;
				}

				return i > 0 ? new RenderableModel(models[Random.nextInt(0, i)], itemPile) : null;
			}
		}
		return null;
	}

	public int getId() {
		RSItem item = this.item.get();
		return item != null ? item.getId() : -1;
	}

	public int getStackSize() {
		RSItem item = this.item.get();
		return item != null ? item.getStackSize() : -1;
	}

	public ItemDefinition getDefinition() {
		final Client client = ClientFactory.getFactory().getClient();
		if (client == null) return null;
		int id = getId();
		if (id == -1) return null;
		final RSItemDefLoader loader;
		final Cache cache;
		final HashTable table;
		if ((loader = client.getRSItemDefLoader()) == null ||
				(cache = loader.getCache()) == null || (table = cache.getTable()) == null) return null;
		final Object o = Game.lookup(table, id);
		return o != null && o instanceof RSItemDef ? new ItemDefinition((RSItemDef) o) : null;
	}

	@Override
	public Tile getLocation() {
		return tile;
	}

	@Override
	public Point getInteractPoint() {
		final Model model = getModel(getId());
		if (model != null) {
			Point point = model.getCentroid(faceIndex);
			if (point != null) return point;
			point = model.getCentroid(faceIndex = model.nextTriangle());
			if (point != null) return point;
		}
		return tile.getInteractPoint();
	}

	@Override
	public Point getNextPoint() {
		final Model model = getModel(getId());
		if (model != null) model.getNextPoint();
		return tile.getNextPoint();
	}

	@Override
	public Point getCenterPoint() {
		final Model model = getModel(getId());
		if (model != null) model.getCenterPoint();
		return tile.getCenterPoint();
	}

	@Override
	public boolean contains(final Point point) {
		return tile.contains(point);
	}

	@Override
	public boolean isValid() {
		return Filters.accept(World.getStacks(), Filters.accept(this));
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof GroundItem)) return false;
		final GroundItem g = (GroundItem) o;
		return g.tile.equals(this.tile) && g.item.equals(this.item);
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
		if (m != null) m.drawWireFrame(render);
	}
}
