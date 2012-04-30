package org.powerbot.game.api.wrappers;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.interactive.Players;

/**
 * A polygonal area of tiles.
 * @author Timer and Odell
 */
public class Area {
	protected final Polygon polygon;
	protected int plane = -1;
	private Tile[] taCache = null;

	/**
	 * Constructs a rectangular area.
	 */
	public Area(final Tile t1, final Tile t2) {
		this(new Tile[]{
				new Tile(Math.min(t1.getX(), t2.getX()), Math.min(t1.getY(), t2.getY()), t1.getPlane()),
				new Tile(Math.max(t1.getX(), t2.getX()), Math.min(t1.getY(), t2.getY()), t1.getPlane()),
				new Tile(Math.max(t1.getX(), t2.getX()), Math.max(t1.getY(), t2.getY()), t2.getPlane()),
				new Tile(Math.min(t1.getX(), t2.getX()), Math.max(t1.getY(), t2.getY()), t2.getPlane())
		});
	}

	/**
	 * Constructs a polygonal area.
	 */
	public Area(final Tile... bounds) {
		polygon = new Polygon();
		for (final Tile tile : bounds) {
			if (plane != -1 && tile.getPlane() != plane) {
				throw new RuntimeException("area does not support 3d");
			}
			plane = tile.getPlane();
			addTile(tile);
		}
	}

	public void translate(final int x, final int y) {
		polygon.translate(x, y);
		taCache = null;
	}

	/**
	 * @return a bounding rectangle of this area.
	 */
	public Rectangle getBounds() {
		return polygon.getBounds();
	}

	/**
	 * @return the plane of this area.
	 */
	public int getPlane() {
		return plane;
	}
	
	/**
	 * Adds a Tile to this Area.
	 * @param t The Tile to add.
	 */
	public void addTile(final Tile t) {
		addTile(t.getX(), t.getY());
	}
	
	/**
	 * Adds a Tile to this Area.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 */
	public void addTile(final int x, final int y) {
		polygon.addPoint(x, y);
		taCache = null;
	}
	
	/**
	 * Determines whether the given x,y pair is contained in the area.
	 * @param x The x coordinate.
	 * @param y The y coordinate.
	 * @return whether the area contains this pair.
	 */
	public boolean contains(final int x, final int y) {
		return polygon.contains(x, y);
	}
	
	/**
	 * Determines whether at least one of the given tiles is contained in this area.
	 * @param tiles The tiles to verify.
	 * @return <tt>true</tt> if at least one of the tiles is contained, otherwise <tt>false</tt>.
	 */
	public boolean contains(final Tile... tiles) {
		for (final Tile tile : tiles) {
			if (tile != null && plane == tile.getPlane() && contains(tile.getX(), tile.getY())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines whether all the given tiles are contained in this area.
	 * @param tiles The tiles to verify.
	 * @return <tt>true</tt> if all tiles are contained, otherwise <tt>false</tt>.
	 */
	public boolean containsAll(final Tile... tiles) {
		for (final Tile tile : tiles) {
			if (tile == null || !contains(tile)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @return the averaged center tile of this area
	 */
	public Tile getCentralTile() {
		return polygon.npoints > 0 ? new Tile((int) Math.round(avg(polygon.xpoints)), (int) Math.round(avg(polygon.ypoints)), plane) : null;
	}
	
	/**
	 * Finds the nearest tile in this area to the local player.
	 * @return the nearest tile contained in this area closest to the local player.
	 */
	public Tile getNearest() {
		return getNearest(Players.getLocal().getLocation());
	}
	
	/**
	 * Finds the nearest tile in this area to the base tile.
	 * @param base The base tile to measure the closest tile to.
	 * @return the nearest tile contained in this area closest to the base tile.
	 */
	public Tile getNearest(Tile base) {
		Tile[] tiles = getTileArray();
		Tile tile = null;
		long dist = Long.MAX_VALUE, temp;
		for (Tile t : tiles) {
			temp = (long) Calculations.distanceTo(t);
			if (t == null || temp < dist) {
				dist = temp;
				tile = t;
			}
		}
		return tile;
	}
	
	/**
	 * @return the tiles backing this Area.
	 */
	public Tile[] getBoundingTiles() {
		Tile[] bounding = new Tile[polygon.npoints];
		for (int i = 0; i < polygon.npoints; i++) {
			bounding[i] = new Tile(polygon.xpoints[i], polygon.ypoints[i], plane);
		}
		return bounding;
	}
	
	/**
	 * @return an array of all the contained tiles in this area.
	 */
	public Tile[] getTileArray() {
		if (taCache == null) {
			Rectangle bounds = getBounds();
			ArrayList<Tile> tiles = new ArrayList<Tile>(bounds.width * bounds.height);
			int xMax = bounds.x + bounds.width, yMax = bounds.y + bounds.height;
			for (int x = bounds.x; x < xMax; x++) {
				for (int y = bounds.y; y < yMax; y++) {
					if (contains(x, y))
						tiles.add(new Tile(x, y, plane));
				}
			}
			taCache = tiles.toArray(new Tile[tiles.size()]);
		}
		return taCache;
	}
	
	private double avg(final int... nums) {
		long total = 0;
		for (int i : nums) total += (long) i;
		return (double) total / (double) nums.length;
	}
	
}