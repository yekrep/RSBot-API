package org.powerbot.game.api.methods.location;

import java.util.LinkedHashSet;
import java.util.Set;

import org.powerbot.game.api.RegionTile;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.location.Location;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSAnimableNode;
import org.powerbot.game.client.RSGroundBoundary1;
import org.powerbot.game.client.RSGroundBoundary2;
import org.powerbot.game.client.RSGroundFloorDecoration;
import org.powerbot.game.client.RSGroundInfoRSGroundArray;
import org.powerbot.game.client.RSGroundRSAnimableList;
import org.powerbot.game.client.RSGroundWallDecoration1;
import org.powerbot.game.client.RSGroundWallDecoration2;
import org.powerbot.game.client.RSInfoRSGroundInfo;

/**
 * A utility for the gathering of locations on the game plane's mesh.
 *
 * @author Timer
 */
public class Locations {
	public static final int TYPE_INTERACTIVE = 0x1;
	public static final int TYPE_FLOOR_DECORATION = 0x2;
	public static final int TYPE_BOUNDARY = 0x4;
	public static final int TYPE_WALL_DECORATION = 0x8;
	public static final int TYPE_UNKNOWN = 0x10;

	public static final Filter<Location> ALL_FILTER = new Filter<Location>() {
		public boolean accept(final Location obj) {
			return true;
		}
	};

	/**
	 * @return An array of all of the loaded Locations within the currently loaded region.
	 */
	public static Location[] getLoaded() {
		return getLoaded(ALL_FILTER);
	}

	/**
	 * @param tile The <code>Tile</code> desired to have its Locations listed.
	 * @return An array of all of the loaded Locations positioned on the given tile.
	 */
	public static Location[] getLoaded(final Tile tile) {
		final Set<Location> locations = getAtLocal(tile.getX() - Game.getBaseX(), tile.getY() - Game.getBaseY(), -1);
		return locations.toArray(new Location[locations.size()]);
	}

	public static Location[] getLoaded(final int id) {
		return getLoaded(new Filter<Location>() {
			public boolean accept(final Location location) {
				return location.getId() == id;
			}
		});
	}

	/**
	 * @param filter The filtering <code>Filter</code> to accept all the Locations through.
	 * @return An array of all of the loaded Locations within the currently loaded region that are accepted by the provided filter.
	 */
	public static Location[] getLoaded(final Filter<Location> filter) {
		final Set<Location> objects = new LinkedHashSet<Location>();
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				for (final Location l : getAtLocal(x, y, -1)) {
					if (l != null && filter.accept(l)) {
						objects.add(l);
					}
				}
			}
		}
		return objects.toArray(new Location[objects.size()]);
	}

	public static Location getNearest(final int id) {
		return getNearest(new Filter<Location>() {
			public boolean accept(final Location location) {
				return location.getId() == id;
			}
		});
	}

	public static Location getNearest(final Filter<Location> filter) {
		Location location = null;
		double distance = Double.MAX_VALUE;
		final RegionTile position = Players.getLocal().getRegionPosition();
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				for (final Location l : getAtLocal(x, y, -1)) {
					if (l != null && filter.accept(l)) {
						final double dist = Calculations.distance(position.getX(), position.getY(), x, y);
						if (dist < distance) {
							distance = dist;
							location = l;
						}
					}
				}
			}
		}
		return location;
	}

	private static Object[][][] getRSGroundArray(final Client client) {
		Object obj;
		if ((obj = client.getRSGroundInfo()) == null) {
			return null;
		}
		if ((obj = ((RSInfoRSGroundInfo) obj).getRSInfoRSGroundInfo()) == null) {
			return null;
		}
		return (Object[][][]) ((RSGroundInfoRSGroundArray) obj).getRSGroundInfoRSGroundArray();
	}

	private static Set<Location> getAtLocal(int x, int y, final int mask) {
		final Bot bot = Bot.resolve();
		final Client client = bot.getClient();
		final Set<Location> objects = new LinkedHashSet<Location>();
		final Object[][][] groundArray = getRSGroundArray(client);
		if (groundArray == null) {
			return objects;
		}

		try {
			final int plane = client.getPlane() * bot.multipliers.GLOBAL_PLANE;
			final Object rsGround = groundArray[plane][x][y];

			if (rsGround != null) {
				Object obj;

				if ((mask & TYPE_INTERACTIVE) != 0) {
					for (RSAnimableNode node = (RSAnimableNode) ((RSGroundRSAnimableList) rsGround).getRSGroundRSAnimableList(); node != null; node = node.getNext()) {
						obj = node.getRSAnimable();
						if (obj != null) {
							if (client.getRSObjectID(obj) != -1) {
								objects.add(new Location(obj, Location.Type.INTERACTIVE, plane));
							}
						}
					}
				}


				if ((mask & TYPE_FLOOR_DECORATION) != 0) {
					obj = ((RSGroundFloorDecoration) rsGround).getRSGroundFloorDecoration();
					if (obj != null) {
						if (client.getRSObjectID(obj) != -1) {
							objects.add(new Location(obj, Location.Type.FLOOR_DECORATION, plane));
						}
					}
				}

				if ((mask & TYPE_BOUNDARY) != 0) {
					obj = ((RSGroundBoundary1) rsGround).getRSGroundBoundary1();
					if (obj != null) {
						if (client.getRSObjectID(obj) != -1) {
							objects.add(new Location(obj, Location.Type.BOUNDARY, plane));
						}
					}

					obj = ((RSGroundBoundary2) rsGround).getRSGroundBoundary2();
					if (obj != null) {
						if (client.getRSObjectID(obj) != -1) {
							objects.add(new Location(obj, Location.Type.BOUNDARY, plane));
						}
					}
				}

				if ((mask & TYPE_WALL_DECORATION) != 0) {
					obj = ((RSGroundWallDecoration1) rsGround).getRSGroundWallDecoration1();
					if (obj != null) {
						if (client.getRSObjectID(obj) != -1) {
							objects.add(new Location(obj, Location.Type.WALL_DECORATION, plane));
						}
					}

					obj = ((RSGroundWallDecoration2) rsGround).getRSGroundWallDecoration2();
					if (obj != null) {
						if (client.getRSObjectID(obj) != -1) {
							objects.add(new Location(obj, Location.Type.WALL_DECORATION, plane));
						}
					}
				}
			}
		} catch (final Exception ignored) {
		}
		return objects;
	}
}
