package org.powerbot.script.rt6;

import java.awt.Point;
import java.io.File;

import org.powerbot.bot.cache.CacheWorker;
import org.powerbot.script.Actionable;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Stackable;
import org.powerbot.util.StringUtils;

public class Item extends Interactive implements Displayable, Identifiable, Nameable, Stackable, Actionable {
	static final CacheWorker CACHE_WORKER = new CacheWorker(new File(
			System.getProperty("user.home"), "jagexcache/runescape/LIVE"
	));
	private final int id;
	private int stack;
	private final Component component;

	public Item(final ClientContext ctx, final Component component) {
		this(ctx, component.itemId(), component.itemStackSize(), component);
	}

	public Item(final ClientContext ctx, final int id, final int stack, final Component component) {
		super(ctx);
		this.id = id;
		this.stack = stack;
		this.component = component;
	}

	@Override
	public void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public int stackSize() {
		if (component == null) {
			return stack;
		}
		final int stack = component.itemStackSize();
		if (component.visible() && component.itemId() == id) {
			return this.stack = stack;
		}
		return this.stack;
	}

	@Override
	public String name() {
		final String name;
		if (component != null && component.itemId() == id) {
			name = component.itemName();
		} else {
			final CacheItemConfig c = CacheItemConfig.load(CACHE_WORKER, id);
			name = c != null ? c.name : "";
		}
		return StringUtils.stripHtml(name);
	}

	public boolean members() {
		final CacheItemConfig c = CacheItemConfig.load(CACHE_WORKER, id);
		return c != null && c.members;
	}

	@Override
	public String[] actions() {
		final CacheItemConfig c = CacheItemConfig.load(CACHE_WORKER, id);
		return c != null ? c.actions : new String[0];
	}

	public String[] groundActions() {
		final CacheItemConfig c = CacheItemConfig.load(CACHE_WORKER, id);
		return c != null ? c.groundActions : new String[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component component() {
		return component;
	}

	@Override
	public Point nextPoint() {
		if (component == null) {
			return new Point(-1, -1);
		}
		return component.nextPoint();
	}

	public Point centerPoint() {
		if (component == null) {
			return new Point(-1, -1);
		}
		return component.centerPoint();
	}

	@Override
	public boolean contains(final Point point) {
		return component != null && component.contains(point);
	}

	@Override
	public boolean valid() {
		return id != -1 && component != null && component.valid() &&
				(!component.visible() || component.itemId() == id);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + id + "/" + stack + "]@" + component;
	}

	@Override
	public int hashCode() {
		if (component == null) {
			return -1;
		}
		return id * 31 + component.index();
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Item)) {
			return false;
		}
		final Item i = (Item) o;
		return id == i.id &&
				((component != null && component.equals(i.component))
						|| (component == null && i.component == null));
	}
}
