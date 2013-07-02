package org.powerbot.script.internal.wrappers;

public final class ClippingValue {

	public static final ClippingValue NORTHWEST = new ClippingValue(0x1, false);
	public static final ClippingValue NORTH = new ClippingValue(0x2, false);
	public static final ClippingValue NORTHEAST = new ClippingValue(0x4, false);
	public static final ClippingValue EAST = new ClippingValue(0x8, false);
	public static final ClippingValue SOUTHEAST = new ClippingValue(0x10, false);
	public static final ClippingValue SOUTH = new ClippingValue(0x20, false);
	public static final ClippingValue SOUTHWEST = new ClippingValue(0x40, false);
	public static final ClippingValue WEST = new ClippingValue(0x80, false);
	public static final ClippingValue OBJECT_TILE = new ClippingValue(0x100, false);
	public static final ClippingValue DECORATION_BLOCK = new ClippingValue(0x40000, false);
	public static final ClippingValue OBJECT_BLOCK = new ClippingValue(0x200000, false);
	public static final ClippingValue ALLOW_RANGE_NORTHWEST = new ClippingValue(0x400000, false);
	public static final ClippingValue ALLOW_RANGE_NORTH = new ClippingValue(0x800000, false);
	public static final ClippingValue ALLOW_RANGE_NORTHEAST = new ClippingValue(0x1000000, false);
	public static final ClippingValue ALLOW_RANGE_EAST = new ClippingValue(0x2000000, false);
	public static final ClippingValue ALLOW_RANGE_SOUTHEAST = new ClippingValue(0x4000000, false);
	public static final ClippingValue ALLOW_RANGE_SOUTH = new ClippingValue(0x8000000, false);
	public static final ClippingValue ALLOW_RANGE_SOUTHWEST = new ClippingValue(0x10000000, false);
	public static final ClippingValue ALLOW_RANGE_WEST = new ClippingValue(0x20000000, false);
	public static final ClippingValue OBJECT_ALLOW_RANGE = new ClippingValue(0x40000000, false);
	public static final ClippingValue PADDING = new ClippingValue(0xffffffff, false);

	public static ClippingValue createNewMarkable() {
		return new ClippingValue(0, true);
	}

	private ClippingValue(final int type, final boolean markable) {
		this.type = type;
		this.markable = markable;
	}

	private int type;
	private boolean markable;

	public boolean marked(ClippingValue clippingValue) {
		return (type & clippingValue.type) == clippingValue.type;
	}

	public ClippingValue mark(final ClippingValue clippingValue) {
		if (markable) {
			type |= clippingValue.type;
			return this;
		} else {
			return new ClippingValue(type | clippingValue.type, true);
		}
	}

	public ClippingValue erase(final ClippingValue clippingValue) {
		if (markable) {
			type &= ~clippingValue.type;
			return this;
		} else {
			return new ClippingValue(type & ~clippingValue.type, true);
		}
	}

	public ClippingValue mark(final int flag) {
		if (markable) {
			type |= flag;
			return this;
		} else {
			return new ClippingValue(type | flag, true);
		}
	}

	public ClippingValue erase(final int flag) {
		if (markable) {
			type &= ~flag;
			return this;
		} else {
			return new ClippingValue(type & ~flag, true);
		}
	}

	public int getType() {
		return type;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ClippingValue)) {
			return false;
		}
		ClippingValue that = (ClippingValue) o;
		return type == that.type;
	}

	@Override
	public int hashCode() {
		return type;
	}
}
