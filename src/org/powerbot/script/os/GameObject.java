package org.powerbot.script.os;

import java.awt.Color;
import java.awt.Point;
import java.lang.ref.WeakReference;

import org.powerbot.bot.os.client.BasicObject;
import org.powerbot.bot.os.client.Client;
import org.powerbot.bot.os.client.MRUCache;
import org.powerbot.bot.os.client.ObjectConfig;
import org.powerbot.bot.os.client.Varbit;
import org.powerbot.bot.os.tools.HashTable;

public class GameObject extends Interactive implements Nameable, Locatable, Identifiable {
	private static final Color TARGET_COLOR = new Color(0, 255, 0, 20);
	private final WeakReference<BasicObject> object;
	private static final int[] lookup;

	static {
		lookup = new int[32];
		int i = 2;
		for (int j = 0; j < 32; j++) {
			lookup[j] = i - 1;
			i += i;
		}
	}

	GameObject(final ClientContext ctx, final BasicObject object) {
		super(ctx);
		this.object = new WeakReference<BasicObject>(object);
	}

	@Override
	public int id() {
		final Client client = ctx.client();
		if (client == null) {
			return -1;
		}
		final BasicObject object = this.object.get();
		final int id = object != null ? (object.getUid() >> 14) & 0xffff : -1;
		final ObjectConfig config = (ObjectConfig) HashTable.lookup(client.getObjectConfigCache(), id);
		if (config != null) {
			int index = -1;
			final int varbit = config.getVarBit(), si = config.getSettingsIndex();
			if (varbit != -1) {
				final MRUCache cache = client.getVarbitCache();
				final Varbit varBit = (Varbit) HashTable.lookup(cache, varbit);
				if (varBit != null) {
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
		}
		return id;
	}

	@Override
	public String name() {
		final ObjectConfig config = getConfig();
		final String str = config != null ? config.getName() : "";
		return str != null ? str : "";
	}

	public String[] actions() {
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
		final BasicObject object = this.object.get();
		return object != null ? object.getMeta() >> 6 : 0;
	}

	public int type() {
		final BasicObject object = this.object.get();
		return object != null ? object.getMeta() & 0x3f : 0;
	}

	public int relativePosition() {
		final BasicObject object = this.object.get();
		final int x, z;
		if (object != null) {
			if (object instanceof org.powerbot.bot.os.client.GameObject) {
				final org.powerbot.bot.os.client.GameObject o2 = (org.powerbot.bot.os.client.GameObject) object;
				x = o2.getX();
				z = o2.getZ();
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

	private ObjectConfig getConfig() {
		final Client client = ctx.client();
		if (client == null) {
			return null;
		}
		final BasicObject object = this.object.get();
		final int id = object != null ? (object.getUid() >> 14) & 0xffff : -1, uid = id();
		if (id != uid) {
			final ObjectConfig alt = (ObjectConfig) HashTable.lookup(client.getObjectConfigCache(), uid);
			if (alt != null) {
				return alt;
			}
		}
		return (ObjectConfig) HashTable.lookup(client.getObjectConfigCache(), id);
	}

	@Override
	public Tile tile() {
		final Client client = ctx.client();
		final int r = relativePosition();
		final int rx = r >> 16, rz = r & 0xffff;
		if (client != null && rx != 0 && rz != 0) {
			return new Tile(client.getOffsetX() + (rx >> 7), client.getOffsetY() + (rz >> 7), client.getFloor());
		}
		return new Tile(-1, -1, -1);
	}

	@Override
	public Point centerPoint() {
		return tile().matrix(ctx).centerPoint();
	}

	@Override
	public Point nextPoint() {
		return tile().matrix(ctx).nextPoint();
	}

	@Override
	public boolean contains(final Point point) {
		return tile().matrix(ctx).contains(point);
	}
}
