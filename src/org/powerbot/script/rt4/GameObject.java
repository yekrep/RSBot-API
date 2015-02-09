package org.powerbot.script.rt4;

import java.awt.Color;
import java.awt.Point;
import java.io.File;

import org.powerbot.bot.cache.CacheWorker;
import org.powerbot.bot.rt4.HashTable;
import org.powerbot.bot.rt4.client.Cache;
import org.powerbot.bot.rt4.client.Client;
import org.powerbot.bot.rt4.client.ObjectConfig;
import org.powerbot.bot.rt4.client.Varbit;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Nameable;
import org.powerbot.script.Tile;
import org.powerbot.script.Validatable;

public class GameObject extends Interactive implements Nameable, Locatable, Identifiable, Validatable {
	public static final Color TARGET_COLOR = new Color(0, 255, 0, 20);
	private final BasicObject object;
	private final Type type;
	private static final int[] lookup;

	static {
		lookup = new int[32];
		int i = 2;
		for (int j = 0; j < 32; j++) {
			lookup[j] = i - 1;
			i += i;
		}
	}

	public static enum Type {
		INTERACTIVE, BOUNDARY, WALL_DECORATION, FLOOR_DECORATION, UNKNOWN
	}

	GameObject(final ClientContext ctx, final BasicObject object, final Type type) {
		super(ctx);
		this.object = object;
		this.type = type;
		bounds(-32, 32, -64, 0, -32, 32);
	}

	@Override
	public void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
		boundingModel.set(new BoundingModel(ctx, x1, x2, y1, y2, z1, z2) {
			@Override
			public int x() {
				final int r = relative();
				return r >> 16;
			}

			@Override
			public int z() {
				final int r = relative();
				return r & 0xffff;
			}
		});
	}

	@Override
	public int id() {
		final Client client = ctx.client();
		if (client == null) {
			return -1;
		}
		final int id = object != null ? (object.getUid() >> 14) & 0xffff : -1;
		/*final ObjectConfig config = new ObjectConfig(object.object.reflector, HashTable.lookup(client.getObjectConfigCache(), id));
		if (config.obj.get() != null) {
			int index = -1;
			final int varbit = config.getVarbit(), si = config.getVarpbitIndex();
			if (varbit != -1) {
				final Cache cache = client.getVarbitCache();
				final Varbit varBit = new Varbit(object.object.reflector, HashTable.lookup(cache, varbit));
				if (varBit.obj.get() != null) {
					final int mask = lookup[varBit.getEndBit() - varBit.getStartBit()];
					index = ctx.varpbits.varpbit(varBit.getIndex()) >> varBit.getStartBit() & mask;
				}
			} else if (si != -1) {
				index = ctx.varpbits.varpbit(si);
			}
			if (index >= 0) {
				final int[] configs = config.getConfigs();
				if (configs != null && index < configs.length && configs[index] != -1) {
					return configs[index];
				}
			}
		}*/
		return id;
	}

	@Override
	public String name() {
		final CacheWorker w = new CacheWorker(new File(System.getProperty("user.home"), "jagexcache/oldschool/LIVE"));
		final CacheObjectConfig c = CacheObjectConfig.load(w, id());
		if (c != null) {
			return c.name;
		}
		return "";
	}

	public String[] actions() {//TODO: this
		final ObjectConfig config = getConfig();
		final String[] arr = config != null ? config.getActions() : new String[0];
		if (arr == null) {
			return new String[0];
		}
		final String[] arr_ = new String[arr.length];
		int c = 0;
		for (final String str : arr) {
			arr_[c++] = str != null ? str : "";
		}
		return arr_;
	}

	public int orientation() {
		return object != null ? object.getMeta() >> 6 : 0;
	}

	public Type type() {
		/*
		final BasicObject object = this.object.get();
		return object != null ? object.getMeta() & 0x3f : 0;
		 */
		return type;
	}

	public int relative() {
		final int x, z;
		if (object != null) {
			if (object.isComplex()) {
				x = object.getX();
				z = object.getZ();
			} else {
				final int uid = object.getUid();
				x = (uid & 0x7f) << 7;
				z = ((uid >> 7) & 0x7f) << 7;
			}
		} else {
			x = z = 0;
		}
		return (x << 16) | z;
	}

	private ObjectConfig getConfig() {//TODO: change
		final Client client = ctx.client();
		if (client == null) {
			return null;
		}
		final int id = object != null ? (object.getUid() >> 14) & 0xffff : -1, uid = id();
		if (id != uid) {
			final ObjectConfig alt = (ObjectConfig) HashTable.lookup(client.getObjectConfigCache(), uid);
			if (alt != null) {
				return alt;
			}
		}
		return new ObjectConfig(object.object.reflector, HashTable.lookup(client.getObjectConfigCache(), id));
	}

	@Override
	public boolean valid() {
		return !object.object.isNull() && ctx.objects.select().contains(this);
	}

	@Override
	public Tile tile() {
		final Client client = ctx.client();
		final int r = relative();
		final int rx = r >> 16, rz = r & 0xffff;
		if (client != null && rx != 0 && rz != 0) {
			return new Tile(client.getOffsetX() + (rx >> 7), client.getOffsetY() + (rz >> 7), client.getFloor());
		}
		return Tile.NIL;
	}

	@Override
	public Point centerPoint() {
		final BoundingModel model = boundingModel.get();
		return model != null ? model.centerPoint() : new Point(-1, -1);
	}

	@Override
	public Point nextPoint() {
		final BoundingModel model = boundingModel.get();
		return model != null ? model.nextPoint() : new Point(-1, -1);
	}

	@Override
	public boolean contains(final Point point) {
		final BoundingModel model = boundingModel.get();
		return model != null && model.contains(point);
	}

	@Override
	public String toString() {
		return String.format("%s[id=%d,name=%s,type=%s,tile=%s]", GameObject.class.getName(), id(), name(), type.name(), tile().toString());
	}

	@Override
	public int hashCode() {
		return object.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof GameObject && hashCode() == o.hashCode();
	}
}
