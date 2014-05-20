package org.powerbot.bot.rt6.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.RSAnimableNode;
import org.powerbot.bot.rt6.client.RSGround;
import org.powerbot.bot.rt6.client.RSGroundBytes;
import org.powerbot.bot.rt6.client.RSGroundInfo;
import org.powerbot.bot.rt6.client.RSInfo;
import org.powerbot.bot.rt6.client.RSObject;
import org.powerbot.bot.rt6.client.RSRotatableObject;
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
		final RSInfo info;
		final RSGroundInfo groundInfo;
		final RSGround[][][] grounds;
		if ((info = client.getRSGroundInfo()) == null || (groundInfo = info.getRSGroundInfo()) == null ||
				(grounds = groundInfo.getRSGroundArray()) == null) {
			return new CollisionMap[0];
		}
		final RSGroundBytes ground = info.getGroundBytes();
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

	private List<GameObject> getObjects(final int x, final int y, final int plane, final RSGround[][][] grounds) {
		final List<GameObject> items = new ArrayList<GameObject>();
		final RSGround ground;
		if (plane < grounds.length && x < grounds[plane].length && y < grounds[plane][x].length) {
			ground = grounds[plane][x][y];
		} else {
			return items;
		}
		if (ground == null) {
			return items;
		}

		for (RSAnimableNode animable = ground.getRSAnimableList(); animable != null; animable = animable.getNext()) {
			final Object node = animable.getRSAnimable();
			if (node == null) {
				continue;
			}
			final RSObject obj = (RSObject) node;
			if (obj.getId() != -1) {
				items.add(new GameObject(ctx, obj, GameObject.Type.INTERACTIVE));
			}
		}

		final RSObject[] objs = {
				ground.getBoundary1(), ground.getBoundary2(),
				ground.getFloorDecoration(),
				ground.getWallDecoration1(), ground.getWallDecoration2()
		};

		for (int i = 0; i < objs.length; i++) {
			if (objs[i] != null && objs[i].getId() != -1) {
				items.add(new GameObject(ctx, objs[i], TYPES[i]));
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

				final RSObject object = next.internal();
				if (object == null) {
					continue;
				}
				final RSRotatableObject rot = new RSRotatableObject(object.reflector, object.obj.get());
				collisionMap.markWall(localX, localY, rot.getType(), rot.getOrientation());
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
					neighbor.g = ng;
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
		private final Node[][] nodes;

		private Graph(final CollisionMap map) {
			width = map.width() - 6;
			height = map.height() - 6;
			nodes = new Node[width][height];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					final Node node = new Node(x, y);
					node.flag = map.flagAt(x, y);
					nodes[x][y] = node;
				}
			}
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
