package org.powerbot.script.wrappers;

import java.util.Arrays;
import java.util.EnumSet;

import org.powerbot.script.methods.Movement;
import org.powerbot.script.methods.Players;
import org.powerbot.script.util.Random;

public class TilePath extends Path {
	protected Tile[] tiles;
	protected Tile[] orig;
	private boolean end;

	public TilePath(final Tile[] tiles) {
		orig = tiles;
		this.tiles = Arrays.copyOf(tiles, tiles.length);
	}

	@Override
	public boolean traverse(final EnumSet<TraversalOption> options) {
		final Player local = Players.getLocal();
		final Tile next = getNext();
		if (next == null || local == null) return false;
		final Tile dest = Movement.getDestination();
		if (next.equals(getEnd())) {
			if (Movement.distanceTo(next) <= 1) return false;
			if (end && (local.isInMotion() || (dest != null && dest.equals(next)))) return false;
			end = true;
		} else end = false;
		if (options != null) {
			if (options.contains(TraversalOption.HANDLE_RUN) && !Movement.isRunning() && Movement.getEnergyLevel() > Random.nextInt(45, 60)) {
				Movement.setRunning(true);
			}
			if (options.contains(TraversalOption.SPACE_ACTIONS) && dest != null && local.isInMotion() && Movement.distance(next, dest) < 3d) {
				if (Movement.distanceTo(dest) > Random.nextDouble(4d, 7d)) return true;
			}
		}
		return Movement.stepTowards(next);
	}

	@Override
	public boolean isValid() {
		return tiles.length > 0 && getNext() != null && Movement.distanceTo(getEnd()) > Math.sqrt(2);
	}

	@Override
	public Tile getNext() {
		/* Get current destination */
		Tile dest = Movement.getDestination();
		/* Label main loop for continuing purposes */
		out:
		/* Iterate over all tiles but the first tile (0) starting with the last (length - 1). */
		for (int i = tiles.length - 1; i > 0; --i) {
			/* The tiles not on screen, go to the next. */
			if (!tiles[i].isOnMap()) continue;
			/* If our destination is null, assume mid path and continue there. */
			/* LARGELY SPACED PATH SUPPORT: If the current destination is the tile on the map, return that tile
			 * as the next one will be coming soon (we hope/assume this, as short spaced paths should never experience
			 * this condition as one will be on map before it reaches the current target). */
			if (dest == null || Movement.distance(tiles[i], dest) < 3d) return tiles[i];
			/* Tile is on map and isn't currently "targeted" (dest), let's check it out.
			 * Iterate over all tiles succeeding it. */
			for (int a = i - 1; a >= 0; --a) {
				/* The tile before the tile on map isn't on map.  Break out to the next tile.
				 * Explanation: Path wraps around something and must be followed.
				 * We cannot suddenly click out of a "pathable" region (104x104).
				 * In these cases, we can assume a better tile will become available. */
				if (!tiles[a].isOnMap()) continue out;
				/* If a tile (successor) is currently targeted, return the tile that was the "best"
				 * on the map for getNext as we can safely assume we're following our path. */
				if (Movement.distance(tiles[a], dest) < 3d) return tiles[i];
			}
		}
		/* Well, we've made it this far.  Return the first tile if nothing else is on our map.
		* CLICKING BACK AND FORTH PREVENTION: check for dest not to be null if we're just starting
		 * our path.  If our destination isn't null and we somehow got to our first tile then
		 * we can safely assume lag is being experienced and return null until next call of getNext.
		 * TELEPORTATION SUPPORT: If destination is set but but we're not moving, assume
		 * invalid destination tile from teleportation reset and return first tile. */
		if (!tiles[0].isOnMap()) return null;
		Player p = Players.getLocal();
		if (dest != null && p != null && p.isInMotion()) return null;
		return tiles[0];
	}

	@Override
	public Tile getStart() {
		return tiles[0];
	}

	@Override
	public Tile getEnd() {
		return tiles[tiles.length - 1];
	}

	public TilePath randomize(final int maxX, final int maxY) {
		for (int i = 0; i < tiles.length; ++i) {
			tiles[i] = orig[i].derive(Random.nextInt(-maxX, maxX + 1), Random.nextInt(-maxY, maxY + 1));
		}
		return this;
	}

	public TilePath reverse() {
		Tile[] reversed = new Tile[tiles.length];
		for (int i = 0; i < orig.length; ++i) {
			reversed[i] = orig[tiles.length - 1 - i];
		}
		orig = reversed;
		reversed = new Tile[tiles.length];
		for (int i = 0; i < tiles.length; ++i) {
			reversed[i] = tiles[tiles.length - 1 - i];
		}
		tiles = reversed;
		return this;
	}

	public Tile[] toArray() {
		final Tile[] a = new Tile[tiles.length];
		System.arraycopy(tiles, 0, a, 0, tiles.length);
		return a;
	}
}