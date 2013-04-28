package org.powerbot.script.xenon.wrappers;

import java.awt.Point;
import java.util.Arrays;
import java.util.Set;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.BaseInfo;
import org.powerbot.game.client.Cache;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.HashTable;
import org.powerbot.game.client.RSGround;
import org.powerbot.game.client.RSGroundInfo;
import org.powerbot.game.client.RSInfo;
import org.powerbot.game.client.RSItemDefLoader;
import org.powerbot.game.client.RSItemPile;
import org.powerbot.script.internal.Nodes;
import org.powerbot.script.xenon.Calculations;
import org.powerbot.script.xenon.GroundItems;
import org.powerbot.script.xenon.util.Random;

public class GroundItem extends Interactive implements Locatable {
	private final Tile tile;
	private final Item item;
	private int faceIndex = -1;

	public GroundItem(final Tile tile, final Item item) {
		this.tile = tile;
		this.item = item;
	}

	public Model getModel() {
		final Client client = Bot.client();
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
				final int graphicsIndex = Calculations.toolkit.graphicsIndex;
				final int[] ids = {itemPile.getID_1(), itemPile.getID_2(), itemPile.getID_3()};
				final org.powerbot.game.client.Model[] models = new org.powerbot.game.client.Model[ids.length];

				final RSItemDefLoader defLoader;
				final Cache cache;
				final HashTable table;
				if ((defLoader = client.getRSItemDefLoader()) == null ||
						(cache = defLoader.getModelCache()) == null || (table = cache.getTable()) == null)
					return null;

				int i = 0;
				for (final int id : ids) {
					final Object model = Nodes.lookup(table, (long) id | (long) graphicsIndex << 29);
					if (model != null && model instanceof org.powerbot.game.client.Model)
						models[i++] = (org.powerbot.game.client.Model) model;
				}

				return i > 0 ? new RenderableModel(models[Random.nextInt(0, i)], itemPile) : null;
			}
		}
		return null;
	}

	public Item getItem() {
		return this.item;
	}

	@Override
	public Tile getLocation() {
		return tile;
	}

	@Override
	public Point getInteractPoint() {
		final Model model = getModel();
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
		final Model model = getModel();
		if (model != null) model.getNextPoint();
		return tile.getNextPoint();
	}

	@Override
	public Point getCenterPoint() {
		final Model model = getModel();
		if (model != null) model.getCenterPoint();
		return tile.getCenterPoint();
	}

	@Override
	public boolean contains(final Point point) {
		return tile.contains(point);
	}

	@Override
	public boolean isValid() {
		final GroundItem[] items = GroundItems.getLoaded(tile.getX(), tile.getY(), 0);
		return Arrays.asList(items).contains(this);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof GroundItem)) return false;
		final GroundItem g = (GroundItem) o;
		return g.tile.equals(this.tile) && g.item.equals(this.item);
	}
}
