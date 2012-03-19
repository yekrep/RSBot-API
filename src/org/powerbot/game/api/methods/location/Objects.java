package org.powerbot.game.api.methods.location;

import java.util.LinkedHashSet;
import java.util.Set;

import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.location.GameObject;
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
import org.powerbot.game.client.RSInfoGroundData;

/**
 * @author Timer
 */
public class Objects {
	public static final int TYPE_INTERACTABLE = 0x1;
	public static final int TYPE_FLOOR_DECORATION = 0x2;
	public static final int TYPE_BOUNDARY = 0x4;
	public static final int TYPE_WALL_DECORATION = 0x8;
	public static final int TYPE_UNKNOWN = 0x10;

	public static final Filter<GameObject> ALL_FILTER = new Filter<GameObject>() {
		public boolean accept(final GameObject obj) {
			return true;
		}
	};

	public static GameObject[] getLoaded() {
		return getLoaded(ALL_FILTER);
	}

	public static GameObject[] getLoaded(final Filter<GameObject> filter) {
		final Set<GameObject> objects = new LinkedHashSet<GameObject>();
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				for (final GameObject o : getAtLocal(x, y, -1)) {
					if (o != null && filter.accept(o)) {
						objects.add(o);
					}
				}
			}
		}
		return objects.toArray(new GameObject[objects.size()]);
	}

	private static Object[][][] getRSGroundArray(final Client client) {
		Object obj;
		if ((obj = client.getRSGroundInfo()) == null) {
			return null;
		}
		if ((obj = ((RSInfoGroundData) obj).getRSInfoGroundData()) == null) {
			return null;
		}
		return (Object[][][]) ((RSGroundInfoRSGroundArray) obj).getRSGroundInfoRSGroundArray();
	}

	private static Set<GameObject> getAtLocal(int x, int y, final int mask) {
		final Bot bot = Bot.resolve();
		final Client client = bot.client;
		final Set<GameObject> objects = new LinkedHashSet<GameObject>();
		final Object[][][] groundArray = getRSGroundArray(client);
		if (groundArray == null) {
			return objects;
		}

		try {
			final int plane = client.getPlane();
			final Object rsGround = groundArray[plane][x][y];

			if (rsGround != null) {
				Object obj;

				if ((mask & TYPE_INTERACTABLE) != 0) {
					for (RSAnimableNode node = (RSAnimableNode) ((RSGroundRSAnimableList) rsGround).getRSGroundRSAnimableList(); node != null; node = node.getNext()) {
						obj = node.getRSAnimable();
						if (obj != null) {
							if (client.getRSObjectID(obj) != -1) {
								objects.add(new GameObject(obj, TYPE_INTERACTABLE, plane));
							}
						}
					}
				}


				if ((mask & TYPE_FLOOR_DECORATION) != 0) {
					obj = ((RSGroundFloorDecoration) rsGround).getRSGroundFloorDecoration();
					if (obj != null) {
						if (client.getRSObjectID(obj) != -1) {
							objects.add(new GameObject(obj, TYPE_FLOOR_DECORATION, plane));
						}
					}
				}

				if ((mask & TYPE_BOUNDARY) != 0) {
					obj = ((RSGroundBoundary1) rsGround).getRSGroundBoundary1();
					if (obj != null) {
						if (client.getRSObjectID(obj) != -1) {
							objects.add(new GameObject(obj, TYPE_BOUNDARY, plane));
						}
					}

					obj = ((RSGroundBoundary2) rsGround).getRSGroundBoundary2();
					if (obj != null) {
						if (client.getRSObjectID(obj) != -1) {
							objects.add(new GameObject(obj, TYPE_BOUNDARY, plane));
						}
					}
				}

				if ((mask & TYPE_WALL_DECORATION) != 0) {
					obj = ((RSGroundWallDecoration1) rsGround).getRSGroundWallDecoration1();
					if (obj != null) {
						if (client.getRSObjectID(obj) != -1) {
							objects.add(new GameObject(obj, TYPE_WALL_DECORATION, plane));
						}
					}

					obj = ((RSGroundWallDecoration2) rsGround).getRSGroundWallDecoration2();
					if (obj != null) {
						if (client.getRSObjectID(obj) != -1) {
							objects.add(new GameObject(obj, TYPE_WALL_DECORATION, plane));
						}
					}
				}
			}
		} catch (final Exception ignored) {
		}
		return objects;
	}
}
