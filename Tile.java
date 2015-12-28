package org.powerbot.script;

import java.awt.Color;

public class Tile implements Locatable, Nillable<Tile>, Comparable<Tile> {
	public static final Tile NIL = new Tile(-1, -1, -1);
	public static final Color TARGET_COLOR = new Color(255, 0, 0, 75);
	protected final Vector3 p;

	public Tile(final int x, final int y) {
		this(x, y, 0);
	}

	public Tile(final int x, final int y, final int z) {
		p = new Vector3(x, y, z);
	}

	public static Tile[] fromArray(final int[][] list) {
		final Tile[] t = new Tile[list.length];
		for (int i = 0; i < t.length; i++) {
			final int l = list[i].length;
			t[i] = new Tile(l > 0 ? list[i][0] : 0, l > 1 ? list[i][1] : 0, list[i].length > 2 ? list[i][2] : 0);
		}
		return t;
	}

	public int x() {
		return p.x;
	}

	public int y() {
		return p.y;
	}

	public int floor() {
		return p.z;
	}

	public double distanceTo(final Locatable l) {
		final Tile o;
		return l == null || (o = l.tile()) == null || p.z != o.p.z || o.p.z == NIL.p.z ? Double.POSITIVE_INFINITY : p.distanceTo(o.p);
	}

	public Tile derive(final int x, final int y) {
		return new Tile(p.x + x, p.y + y, p.z);
	}

	public Tile derive(final int x, final int y, final int z) {
		return new Tile(p.x + x, p.y + y, z);
	}

	public org.powerbot.script.rt4.TileMatrix matrix(final org.powerbot.script.rt4.ClientContext ctx) {
		return new org.powerbot.script.rt4.TileMatrix(ctx, this);
	}

	public org.powerbot.script.rt6.TileMatrix matrix(final org.powerbot.script.rt6.ClientContext ctx) {
		return new org.powerbot.script.rt6.TileMatrix(ctx, this);
	}

	@Override
	public Tile tile() {
		return this;
	}

	@Override
	public Tile nil() {
		return NIL;
	}

	@Override
	public int compareTo(final Tile o) {
		return p.compareTo(o.p);
	}

	@Override
	public String toString() {
		return p.toString();
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof Tile && p.equals(((Tile) o).p);
	}

	@Override
	public int hashCode() {
		return p.hashCode();
	}
}
