package org.powerbot.script.xenon.wrappers;

import java.util.Arrays;
import java.util.EnumSet;

import org.powerbot.script.xenon.Movement;
import org.powerbot.script.xenon.Players;
import org.powerbot.script.xenon.util.Random;

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
				if (Movement.distanceTo(dest) > Random.nextDouble(4d, 6d)) return true;
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
		final Tile dest = Movement.getDestination();
		for (int i = tiles.length - 1; i >= 0; --i) {
			if (!tiles[i].isOnMap()) continue;
			if (dest == null || Movement.distance(dest, tiles[i - 1]) < 3) return tiles[i];
		}
		if (tiles.length > 0 && tiles[0].isOnMap()) return tiles[0];
		return null;
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