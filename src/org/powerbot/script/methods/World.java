package org.powerbot.script.methods;

import org.powerbot.bot.ClientFactory;
import org.powerbot.client.*;
import org.powerbot.script.internal.wrappers.Deque;
import org.powerbot.script.wrappers.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class World {
	/**
	 * Returns the game's local player (your player).
	 * Must be logged in to retrieve.
	 * <p/>
	 * Be sure to check for nulls!
	 *
	 * @return the local {@link org.powerbot.script.wrappers.Player}
	 */
	public static Player getPlayer() {
		final Client client = ClientFactory.getFactory().getClient();
		if (client == null) return null;

		final RSPlayer p = client.getMyRSPlayer();
		return p != null ? new Player(p) : null;
	}

	/**
	 * Returns all the {@link org.powerbot.script.wrappers.Player}s in the region.
	 *
	 * @return an array of all the loaded {@link org.powerbot.script.wrappers.Player}s
	 */
	public static Player[] getPlayers() {
		final Client client = ClientFactory.getFactory().getClient();
		if (client == null) return new Player[0];

		final int[] indices = client.getRSPlayerIndexArray();
		final RSPlayer[] players = client.getRSPlayerArray();
		if (indices == null || players == null) return new Player[0];

		final Player[] loadedPlayers = new Player[indices.length];
		int d = 0;
		for (final int index : indices) {
			final RSPlayer player = players[index];
			if (player != null) loadedPlayers[d++] = new Player(player);
		}

		return Arrays.copyOf(loadedPlayers, d);
	}

	/**
	 * Returns all the {@link org.powerbot.script.wrappers.Npc}s in the region.
	 *
	 * @return an array of the loaded {@link org.powerbot.script.wrappers.Npc}s
	 */
	public static Npc[] getNpcs() {
		final Client client = ClientFactory.getFactory().getClient();
		if (client == null) return new Npc[0];

		final int[] indices = client.getRSNPCIndexArray();
		final HashTable npcTable = client.getRSNPCNC();
		if (indices == null || npcTable == null) return new Npc[0];

		final Npc[] npcs = new Npc[indices.length];
		int d = 0;
		for (final int index : indices) {
			Object npc = Game.lookup(npcTable, index);
			if (npc == null) continue;
			if (npc instanceof RSNPCNode) npc = ((RSNPCNode) npc).getRSNPC();
			if (npc instanceof RSNPC) npcs[d++] = new Npc((RSNPC) npc);
		}

		return Arrays.copyOf(npcs, d);
	}

	public static GameObject[] getObjects() {
		final Set<GameObject> objects = new LinkedHashSet<>();
		final Client client = ClientFactory.getFactory().getClient();
		if (client == null) return new GameObject[0];

		final RSInfo info;
		final RSGroundInfo groundInfo;
		final RSGround[][][] grounds;
		if ((info = client.getRSGroundInfo()) == null || (groundInfo = info.getRSGroundInfo()) == null ||
				(grounds = groundInfo.getRSGroundArray()) == null)
			return new GameObject[0];

		final GameObject.Type[] types = {
				GameObject.Type.BOUNDARY, GameObject.Type.BOUNDARY,
				GameObject.Type.FLOOR_DECORATION,
				GameObject.Type.WALL_DECORATION, GameObject.Type.WALL_DECORATION
		};

		final int plane = client.getPlane();

		final RSGround[][] objArr = plane > -1 && plane < grounds.length ? grounds[plane] : null;
		if (objArr == null) return new GameObject[0];
		for (int x = 0; x <= objArr.length - 1; x++) {
			for (int y = 0; y <= objArr[x].length - 1; y++) {
				final RSGround ground = objArr[x][y];
				if (ground == null) continue;

				for (RSAnimableNode node = ground.getRSAnimableList(); node != null; node = node.getNext()) {
					final RSObject obj = node.getRSAnimable();
					if (obj != null && obj.getId() != -1) objects.add(new GameObject(obj, GameObject.Type.INTERACTIVE));
				}


				final RSObject[] objs = {
						ground.getBoundary1(), ground.getBoundary2(),
						ground.getFloorDecoration(),
						ground.getWallDecoration1(), ground.getWallDecoration2()
				};

				for (int i = 0; i < objs.length; i++) {
					if (objs[i] != null && objs[i].getId() != -1) objects.add(new GameObject(objs[i], types[i]));
				}
			}
		}
		return objects.toArray(new GameObject[objects.size()]);
	}

	public static GroundItem[] getStacks() {
		Set<GroundItem> items = new HashSet<>();

		Client client = ClientFactory.getFactory().getClient();
		if (client == null) return new GroundItem[0];

		HashTable table = client.getRSItemHashTable();
		if (table == null) return new GroundItem[0];

		int plane = client.getPlane();
		long id;
		NodeListCache cache;
		NodeDeque deque;

		Tile base = Game.getMapBase();
		if (base == null) return new GroundItem[0];
		int bx = base.getX(), by = base.getY();
		for (int x = bx; x < bx + 104; x++) {
			for (int y = by; y < by + 104; y++) {
				id = x | y << 14 | plane << 28;
				cache = (NodeListCache) Game.lookup(table, id);
				if (cache == null || (deque = cache.getNodeList()) == null) continue;
				final Deque<RSItem> itemStack = new Deque<>(deque);
				for (RSItem item = itemStack.getHead(); item != null; item = itemStack.getNext()) {
					items.add(new GroundItem(new Tile(x, y, plane), item));
				}
			}
		}
		return items.toArray(new GroundItem[items.size()]);
	}

	/**
	 * Returns the loaded {@link org.powerbot.script.wrappers.HintArrow}s.
	 *
	 * @return an array of loaded {@link org.powerbot.script.wrappers.HintArrow}s
	 */
	public static HintArrow[] getHintArrows() {
		final Client client = ClientFactory.getFactory().getClient();
		if (client == null) return new HintArrow[0];

		final RSHintArrow[] arr = client.getRSHintArrows();
		final HintArrow[] arrows = new HintArrow[arr != null ? arr.length : 0];
		int d = 0;
		for (final RSHintArrow arrow : arr != null ? arr : new RSHintArrow[0]) {
			if (arrow != null) arrows[d++] = new HintArrow(arrow);
		}
		return Arrays.copyOf(arrows, d);
	}

	/**
	 * Returns the {@link org.powerbot.script.wrappers.Projectile}s in the region.
	 *
	 * @return an array of loaded {@link org.powerbot.script.wrappers.Projectile}s
	 */
	public static Projectile[] getProjectiles() {
		final Client client = ClientFactory.getFactory().getClient();
		if (client == null) return new Projectile[0];

		final NodeDeque deque = client.getProjectileDeque();
		if (deque == null) return new Projectile[0];

		final Set<Projectile> projectiles = new HashSet<>();
		final Deque<Node> nodes = new Deque<>(deque);
		for (Node node = nodes.getHead(); node != null; node = nodes.getNext()) {
			final RSProjectile projectile;
			if (node instanceof RSProjectileNode && (projectile = ((RSProjectileNode) node).getProjectile()) != null) {
				projectiles.add(new Projectile(projectile));
			}
		}
		return projectiles.toArray(new Projectile[projectiles.size()]);
	}
}
