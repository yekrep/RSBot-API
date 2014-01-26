package org.powerbot.os.api.wrappers;

public class Tile {
	public final int x, y;
	public final int floor;

	public Tile(final int x, final int y) {
		this(x, y, 0);
	}

	public Tile(final int x, final int y, final int floor) {
		this.x = x;
		this.y = y;
		this.floor = floor;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getFloor() {
		return floor;
	}

	public Tile derive(final int x, final int y) {
		return new Tile(this.x + x, this.y + y, floor);
	}

	@Override
	public String toString() {
		return String.format("%s[x=%d/y=%d/floor=%d]", Tile.class.getName(), x, y, floor);
	}
}
