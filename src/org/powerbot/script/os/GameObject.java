package org.powerbot.script.os;

import java.awt.Color;
import java.awt.Point;
import java.lang.ref.WeakReference;

import org.powerbot.bot.os.client.BasicObject;
import org.powerbot.bot.os.client.Client;
import org.powerbot.bot.os.client.MRUCache;
import org.powerbot.bot.os.client.ObjConfig;
import org.powerbot.bot.os.client.VarBit;
import org.powerbot.script.os.tools.HashTable;
import org.powerbot.script.rs3.Nameable;

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
	public int getId() {
		final Client client = ctx.client();
		if (client == null) {
			return -1;
		}
		final BasicObject object = this.object.get();
		final int id = object != null ? (object.getUid() >> 14) & 0xffff : -1;
		final ObjConfig config = (ObjConfig) HashTable.lookup(client.getObjConfigMRUCache(), id);
		if (config != null) {
			int index = -1;
			final int varbit = config.getVarBit(), si = config.getSettingsIndex();
			if (varbit != -1) {
				final MRUCache cache = client.getVarBitMRUCache();
				final VarBit varBit = (VarBit) HashTable.lookup(cache, varbit);
				if (varBit != null) {
					final int mask = lookup[varBit.getEndBit() - varBit.getStartBit()];
					index = ctx.varpbits.getVarpbit(varBit.getIndex()) >> varBit.getStartBit() & mask;
				}
			} else if (si != -1) {
				index = ctx.varpbits.getVarpbit(si);
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
	public String getName() {
		final ObjConfig config = getConfig();
		final String str = config != null ? config.getName() : "";
		return str != null ? str : "";
	}

	public String[] getActions() {
		final ObjConfig config = getConfig();
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

	public int getOrientation() {
		final BasicObject object = this.object.get();
		return object != null ? object.getMeta() >> 6 : 0;
	}

	public int getType() {
		final BasicObject object = this.object.get();
		return object != null ? object.getMeta() & 0x3f : 0;
	}

	public int getRelativePosition() {
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

	private ObjConfig getConfig() {
		final Client client = ctx.client();
		if (client == null) {
			return null;
		}
		final BasicObject object = this.object.get();
		final int id = object != null ? (object.getUid() >> 14) & 0xffff : -1, uid = getId();
		if (id != uid) {
			final ObjConfig alt = (ObjConfig) HashTable.lookup(client.getObjConfigMRUCache(), uid);
			if (alt != null) {
				return alt;
			}
		}
		return (ObjConfig) HashTable.lookup(client.getObjConfigMRUCache(), id);
	}

	@Override
	public Tile getLocation() {
		final Client client = ctx.client();
		final int r = getRelativePosition();
		final int rx = r >> 16, rz = r & 0xffff;
		if (client != null && rx != 0 && rz != 0) {
			return new Tile(client.getOffsetX() + (rx >> 7), client.getOffsetY() + (rz >> 7), client.getFloor());
		}
		return new Tile(-1, -1, -1);
	}

	@Override
	public Point getCenterPoint() {
		return getLocation().getMatrix(ctx).getCenterPoint();
	}

	@Override
	public Point getNextPoint() {
		return getLocation().getMatrix(ctx).getNextPoint();
	}

	@Override
	public boolean contains(final Point point) {
		return getLocation().getMatrix(ctx).contains(point);
	}
}
