package org.powerbot.bot.rt6;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt6.client.*;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.*;
import org.powerbot.script.rt6.GameObject;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.LinkedList;

public class Map extends ClientAccessor {
	public Map(final ClientContext factory) {
		super(factory);
	}

	private final GameObject.Type[] TYPES = {
			GameObject.Type.BOUNDARY, GameObject.Type.BOUNDARY,
			GameObject.Type.FLOOR_DECORATION,
			GameObject.Type.WALL_DECORATION, GameObject.Type.WALL_DECORATION
	};

	public CollisionMap[] getCollisionMaps() {
		final Client c = ctx.client();
		if (c == null) {
			return new CollisionMap[0];
		}
		final World w = c.getWorld();
		final byte[][][] settings = w.getFloorSettings().getBytes();
		final Tile[][][] tiles = w.getLandscape().getTiles();
		if (settings.length == 0 || tiles.length == 0) {
			return new CollisionMap[0];
		}
		final CollisionMap[] maps = new CollisionMap[settings.length];
		for (int floor = 0; floor < settings.length; floor++) {
			final int length = settings[floor].length;
			final int height = length > 0 ? settings[floor][0].length : -1;
			if (height < 0) {
				continue;
			}
			maps[floor] = new CollisionMap(length, height);
			for (int x = 0; x < length; x++) {
				for (int y = 0; y < height; y++) {
					final List<GameObject> objects = getObjects(x, y, floor, tiles);
					readCollision(maps[floor], x, y, objects);
					if ((settings[floor][x][y] & 0x1) == 0) {
						continue;
					}
					int floor2 = floor;
					if ((settings[1][x][y] & 0x2) != 0) {
						--floor2;
					}
					if (floor2 >= 0) {
						if (maps[floor2] == null) {
							maps[floor2] = new CollisionMap(settings[floor2].length, settings[floor2][0].length);
						}
						maps[floor2].markDeadBlock(x, y);
					}
				}
			}
		}
		return maps;
	}

	private List<GameObject> getObjects(final int x, final int y, final int floor, final Tile[][][] grounds) {
		final List<GameObject> items = new ArrayList<>();
		final Tile g;
		if (floor < grounds.length && x < grounds[floor].length && y < grounds[floor][x].length) {
			g = grounds[floor][x][y];
		} else {
			return items;
		}
		if (g.isNull()) {
			return items;
		}
		for (RenderableNode node = g.getInteractives(); !node.isNull(); node = node.getNext()) {
			final RenderableEntity r = node.getEntity();
			if (r.isNull()) {
				continue;
			}
			if (r.isTypeOf(org.powerbot.bot.rt6.client.GameObject.class)) {
				final org.powerbot.bot.rt6.client.GameObject o = new org.powerbot.bot.rt6.client.GameObject(r.reflector, r);
				if (o.getId() != -1) {
					items.add(new GameObject(ctx, new BasicObject(o, floor), GameObject.Type.INTERACTIVE));
				}
			} else if (r.isTypeOf(DynamicGameObject.class)) {
				final DynamicGameObject o = new DynamicGameObject(r.reflector, r);
				if (o.getBridge().getId() != -1) {
					items.add(new GameObject(ctx, new BasicObject(o, floor), GameObject.Type.INTERACTIVE));
				}
			}
		}

		final Object[] objs = {
				g.getBoundary1(), g.getBoundary2(),
				g.getFloorDecoration(),
				g.getWallDecoration1(), g.getWallDecoration2()
		};
		final Class<?> o_types[][] = {
				{BoundaryObject.class, DynamicBoundaryObject.class}, {BoundaryObject.class, DynamicBoundaryObject.class},
				{FloorObject.class, DynamicFloorObject.class},
				{WallObject.class, DynamicWallObject.class}, {WallObject.class, DynamicWallObject.class}
		};
		for (int i = 0; i < objs.length; i++) {
			if (objs[i] == null) {
				continue;
			}
			Class<?> type = null;
			for (final Class<?> e : o_types[i]) {
				@SuppressWarnings("unchecked")
				final Class<? extends ReflectProxy> c = (Class<? extends ReflectProxy>) e;
				if (c != null && g.reflector.isTypeOf(objs[i], c)) {
					type = c;
					break;
				}
			}
			if (type == null) {
				continue;
			}
			try {
				items.add(new GameObject(ctx,
						new BasicObject((RenderableEntity) type.getConstructor(Reflector.class, Object.class).newInstance(g.reflector, objs[i]), floor),
						TYPES[i]));
			} catch (final InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
			}
		}
		return items;
	}

	private void readCollision(final CollisionMap collisionMap, final int localX, final int localY, final List<GameObject> objects) {
		int clippingType;
		for (final GameObject next : objects) {
			clippingType = next.clippingType();
			switch (next.type()) {
			case BOUNDARY:
				if (clippingType == 0) {
					continue;
				}
				collisionMap.markWall(localX, localY, next.object.getType(), next.object.getOrientation());
				break;
			case FLOOR_DECORATION:
				if (clippingType != 1) {
					continue;
				}
				collisionMap.markDecoration(localX, localY);
				break;
			case INTERACTIVE:
				if (clippingType == 0) {
					continue;
				}
				collisionMap.markInteractive(localX, localY);
				break;
			}
		}
	}

	private CollisionMap getCollisionMap(final int plane) {
		final CollisionMap[] maps = getCollisionMaps();
		if (plane < 0 || plane >= maps.length) {
			return null;
		}
		return maps[plane];
	}

	public Node[] getPath(final int startX, final int startY, final int endX, final int endY, final int plane) {
		final CollisionMap map = getCollisionMap(plane);
		if (map == null) {
			return new Node[0];
		}
		final Graph graph = new Graph(map);
		if (startX < 0 || startY < 0 || endX < 0 || endY < 0 ||
				startX >= graph.width || startY >= graph.height || endX >= graph.width || endY >= graph.height) {
			return new Node[0];
		}

		dijkstra(graph, graph.nodes[startX][startY], graph.nodes[endX][endY]);
		return path(graph.nodes[endX][endY]);
	}

	public int getDistance(final int startX, final int startY, final int endX, final int endY, final int plane) {
		final CollisionMap map = getCollisionMap(plane);
		if (map == null) {
			return -1;
		}
		final Graph graph = new Graph(map);
		if (startX < 0 || startY < 0 || endX < 0 || endY < 0 ||
				startX >= graph.width || startY >= graph.height || endX >= graph.width || endY >= graph.height) {
			return -1;
		}

		dijkstra(graph, graph.nodes[startX][startY], graph.nodes[endX][endY]);
		final double d = graph.nodes[endX][endY].g;
		if (Double.isInfinite(d)) {
			return -1;
		}
		return (int) d;
	}

	private Node[] path(Node target) {
		final List<Node> nodes = new LinkedList<>();
		if (Double.isInfinite(target.g)) {
			return new Node[0];
		}
		while (target != null) {
			nodes.add(target);
			target = target.parent;
		}

		Collections.reverse(nodes);
		final Node[] path = new Node[nodes.size()];
		return nodes.toArray(path);
	}

	private void dijkstra(final Graph graph, final Node source, final Node target) {
		source.g = 0d;
		source.f = 0d;

		final Queue<Node> queue = new PriorityQueue<>(8, Comparator.comparingDouble(o -> o.f));

		final double sqrt2 = Math.sqrt(2);

		queue.add(source);
		source.opened = true;
		while (!queue.isEmpty()) {
			final Node node = queue.poll();
			node.closed = true;
			if (node.equals(target)) {
				break;
			}
			for (final Node neighbor : graph.neighbors(node)) {
				if (neighbor.closed) {
					continue;
				}
				final double ng = node.g + ((neighbor.x - node.x == 0 || neighbor.y - node.y == 0) ? 1d : sqrt2);

				if (!neighbor.opened || ng < neighbor.g) {
					neighbor.g = ng + graph.getNodeCost(node);
					neighbor.h = 0;//no heuristic
					neighbor.f = neighbor.g + neighbor.h;
					neighbor.parent = node;

					if (!neighbor.opened) {
						queue.offer(neighbor);
						neighbor.opened = true;
					}
				}
			}
		}
	}

	private class Graph {
		private final int width, height;
		private final double[][] costs;
		private final Node[][] nodes;

		private Graph(final CollisionMap map) {
			width = map.width() - 6;
			height = map.height() - 6;
			nodes = new Node[width][height];
			costs = new double[width][height];
			final CollisionFlag catch_all = CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DECORATION_BLOCK)
					.mark(CollisionFlag.DEAD_BLOCK)
					.mark(CollisionFlag.NORTH).mark(CollisionFlag.NORTHWEST).mark(CollisionFlag.NORTHEAST)
					.mark(CollisionFlag.SOUTH).mark(CollisionFlag.SOUTHWEST).mark(CollisionFlag.SOUTHEAST)
					.mark(CollisionFlag.WEST)
					.mark(CollisionFlag.EAST);
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					final Node node = new Node(x, y);
					node.flag = map.flagAt(x, y);
					nodes[x][y] = node;

					if (node.flag.contains(catch_all)) {
						for (int dx = Math.max(0, x - 1); dx <= Math.min(width - 1, x + 1); dx++) {
							for (int dy = Math.max(0, y - 1); dy <= Math.min(height - 1, y + 1); dy++) {
								costs[dx][dy] += Random.nextDouble();
							}
						}
					}
				}
			}
		}

		private double getNodeCost(final Node node) {
			final int curr_x = node.x;
			final int curr_y = node.y;
			if (curr_x < 0 || curr_y < 0 ||
					curr_x >= width || curr_y >= height) {
				return 0d;
			}
			return costs[curr_x][curr_y];
		}

		private List<Node> neighbors(final Node node) {
			final List<Node> list = new ArrayList<>(8);
			final int curr_x = node.x;
			final int curr_y = node.y;
			if (curr_x < 0 || curr_y < 0 ||
					curr_x >= width || curr_y >= height) {
				return list;
			}
			if (curr_y > 0 &&
					!nodes[curr_x][curr_y].flag.contains(CollisionFlag.SOUTH) &&
					!nodes[curr_x][curr_y - 1].flag.contains(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) {
				list.add(nodes[curr_x][curr_y - 1]);
			}
			if (curr_x > 0 &&
					!nodes[curr_x][curr_y].flag.contains(CollisionFlag.WEST) &&
					!nodes[curr_x - 1][curr_y].flag.contains(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) {
				list.add(nodes[curr_x - 1][curr_y]);
			}
			if (curr_y < height - 1 &&
					!nodes[curr_x][curr_y].flag.contains(CollisionFlag.NORTH) &&
					!nodes[curr_x][curr_y + 1].flag.contains(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) {
				list.add(nodes[curr_x][curr_y + 1]);
			}
			if (curr_x < width - 1 &&
					!nodes[curr_x][curr_y].flag.contains(CollisionFlag.EAST) &&
					!nodes[curr_x + 1][curr_y].flag.contains(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) {
				list.add(nodes[curr_x + 1][curr_y]);
			}
			if (curr_x > 0 && curr_y > 0 &&
					!nodes[curr_x][curr_y].flag.contains(CollisionFlag.SOUTHWEST.mark(CollisionFlag.SOUTH.mark(CollisionFlag.WEST))) &&
					!nodes[curr_x - 1][curr_y - 1].flag.contains(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)) &&
					!nodes[curr_x][curr_y - 1].flag.contains(CollisionFlag.WEST.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) &&
					!nodes[curr_x - 1][curr_y].flag.contains(CollisionFlag.SOUTH.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)))) {
				list.add(nodes[curr_x - 1][curr_y - 1]);
			}
			if (curr_x > 0 && curr_y < height - 1 &&
					!nodes[curr_x][curr_y].flag.contains(CollisionFlag.NORTHWEST.mark(CollisionFlag.NORTH.mark(CollisionFlag.WEST))) &&
					!nodes[curr_x - 1][curr_y + 1].flag.contains(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)) &&
					!nodes[curr_x][curr_y + 1].flag.contains(CollisionFlag.WEST.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) &&
					!nodes[curr_x - 1][curr_y].flag.contains(CollisionFlag.NORTH.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)))) {
				list.add(nodes[curr_x - 1][curr_y + 1]);
			}
			if (curr_x < height - 1 && curr_y > 0 &&
					!nodes[curr_x][curr_y].flag.contains(CollisionFlag.SOUTHEAST.mark(CollisionFlag.SOUTH.mark(CollisionFlag.EAST))) &&
					!nodes[curr_x + 1][curr_y - 1].flag.contains(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)) &&
					!nodes[curr_x][curr_y - 1].flag.contains(CollisionFlag.EAST.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) &&
					!nodes[curr_x + 1][curr_y].flag.contains(CollisionFlag.SOUTH.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)))) {
				list.add(nodes[curr_x + 1][curr_y - 1]);
			}
			if (curr_x < width - 1 && curr_y < height - 1 &&
					!nodes[curr_x][curr_y].flag.contains(CollisionFlag.NORTHEAST.mark(CollisionFlag.NORTH.mark(CollisionFlag.EAST))) &&
					!nodes[curr_x + 1][curr_y + 1].flag.contains(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)) &&
					!nodes[curr_x][curr_y + 1].flag.contains(CollisionFlag.EAST.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) &&
					!nodes[curr_x + 1][curr_y].flag.contains(CollisionFlag.NORTH.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)))) {
				list.add(nodes[curr_x + 1][curr_y + 1]);
			}
			return list;
		}
	}

	public class Node {
		public final int x, y;
		private boolean opened, closed;
		private Node parent;
		private double f, g, h;
		private CollisionFlag flag;

		private Node(final int x, final int y) {
			this.x = x;
			this.y = y;
			reset();
		}

		private void reset() {
			this.opened = this.closed = false;
			this.parent = null;
			this.f = this.g = this.h = Double.POSITIVE_INFINITY;
			this.flag = CollisionFlag.PADDING;
		}

		@Override
		public String toString() {
			return Node.class.getSimpleName() + "[x=" + x + ",y=" + y + "]";
		}

		@Override
		public boolean equals(final Object o) {
			if (!(o instanceof Node)) {
				return false;
			}
			final Node n = (Node) o;
			return x == n.x && y == n.y;
		}
	}
}
