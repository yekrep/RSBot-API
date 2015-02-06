package org.powerbot.bot.rt6;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt4.client.FloorObject;
import org.powerbot.bot.rt6.client.BoundaryObject;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.DynamicFloorObject;
import org.powerbot.bot.rt6.client.DynamicGameObject;
import org.powerbot.bot.rt6.client.DynamicWallObject;
import org.powerbot.bot.rt6.client.FloorSettings;
import org.powerbot.bot.rt6.client.Landscape;
import org.powerbot.bot.rt6.client.RenderableEntity;
import org.powerbot.bot.rt6.client.RenderableNode;
import org.powerbot.bot.rt6.client.Tile;
import org.powerbot.bot.rt6.client.WallObject;
import org.powerbot.bot.rt6.client.World;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.BasicObject;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.CollisionFlag;
import org.powerbot.script.rt6.CollisionMap;
import org.powerbot.script.rt6.GameObject;

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
		final Client client = ctx.client();
		if (client == null) {
			return new CollisionMap[0];
		}
		final World info;
		final Landscape groundInfo;
		final Tile[][][] grounds;
		if ((info = client.getWorld()) == null || (groundInfo = info.getLandscape()) == null ||
				(grounds = groundInfo.getTiles()) == null) {
			return new CollisionMap[0];
		}
		final FloorSettings ground = info.getFloorSettings();
		final byte[][][] settings = ground != null ? ground.getBytes() : null;
		if (settings == null) {
			return new CollisionMap[0];
		}
		final CollisionMap[] collisionMaps = new CollisionMap[settings.length];
		for (int plane = 0; plane < collisionMaps.length; plane++) {
			final int width = settings[plane].length;
			int height = Integer.MAX_VALUE;
			for (int x = 0; x < width; x++) {
				height = Math.min(height, settings[plane][x].length);
			}
			collisionMaps[plane] = new CollisionMap(width, height);
			for (int locX = 0; locX < width; locX++) {
				for (int locY = 0; locY < height; locY++) {
					final List<GameObject> objects = getObjects(locX, locY, plane, grounds);
					if ((settings[plane][locX][locY] & 0x1) == 0) {
						readCollision(collisionMaps[plane], locX, locY, objects);
						continue;
					}
					readCollision(collisionMaps[plane], locX, locY, objects);
					int planeOffset = plane;
					if ((settings[1][locX][locY] & 0x2) != 0) {
						planeOffset--;
					}
					if (planeOffset < 0) {
						continue;
					}
					if (collisionMaps[planeOffset] == null) {
						collisionMaps[planeOffset] = new CollisionMap(width, height);
					}
					collisionMaps[planeOffset].markDeadBlock(locX, locY);
				}
			}
		}
		return collisionMaps;
	}

	private List<GameObject> getObjects(final int x, final int y, final int floor, final Tile[][][] grounds) {
		final List<GameObject> items = new ArrayList<GameObject>();
		final Tile g;
		if (floor < grounds.length && x < grounds[floor].length && y < grounds[floor][x].length) {
			g = grounds[floor][x][y];
		} else {
			return items;
		}
		if (g == null) {
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
					items.add(new GameObject(ctx, new BasicObject(r, floor), GameObject.Type.INTERACTIVE));
				}
			} else if (r.isTypeOf(DynamicGameObject.class)) {
				final DynamicGameObject o = new DynamicGameObject(r.reflector, r);
				if (o.getBridge().getId() != -1) {
					items.add(new GameObject(ctx, new BasicObject(r, floor), GameObject.Type.INTERACTIVE));
				}
			}
		}

		final Object[] objs = {
				g.getBoundary1(), g.getBoundary2(),
				g.getFloorDecoration(),
				g.getWallDecoration1(), g.getWallDecoration2()
		};
		final Class<?> o_types[][] = {
				{BoundaryObject.class, null}, {BoundaryObject.class, null},
				{FloorObject.class, DynamicFloorObject.class},
				{WallObject.class, DynamicWallObject.class}, {WallObject.class, DynamicWallObject.class}
		};
		for (int i = 0; i < objs.length; i++) {
			if (objs[i] == null) {
				continue;
			}
			Class<?> type = null;
			for (final Class<?> c : o_types[i]) {
				if (c != null && g.reflector.isTypeOf(objs[i], (Class<? extends ReflectProxy>) c)) {
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
			} catch (final InstantiationException ignored) {
			} catch (final IllegalAccessException ignored) {
			} catch (final InvocationTargetException ignored) {
			} catch (final NoSuchMethodException ignored) {
			}
		}
		return items;
	}

	private void readCollision(final CollisionMap collisionMap, final int localX, final int localY, final List<GameObject> objects) {
		int clippingType;
		for (final GameObject next : objects) {
			clippingType = ctx.objects.type(next.id());
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
		final List<Node> nodes = new LinkedList<Node>();
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

		final Queue<Node> queue = new PriorityQueue<Node>(8, new Comparator<Node>() {
			@Override
			public int compare(final Node o1, final Node o2) {
				return Double.compare(o1.f, o2.f);
			}
		});

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
			final List<Node> list = new ArrayList<Node>(8);
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
			if (o == null || !(o instanceof Node)) {
				return false;
			}
			final Node n = (Node) o;
			return x == n.x && y == n.y;
		}
	}
}
