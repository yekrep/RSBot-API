package org.powerbot.script.internal.methods;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import org.powerbot.client.Client;
import org.powerbot.client.RSAnimableNode;
import org.powerbot.client.RSGround;
import org.powerbot.client.RSGroundBytes;
import org.powerbot.client.RSGroundInfo;
import org.powerbot.client.RSInfo;
import org.powerbot.client.RSObject;
import org.powerbot.client.RSRotatableObject;
import org.powerbot.script.internal.wrappers.CollisionFlag;
import org.powerbot.script.internal.wrappers.CollisionMap;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;
import org.powerbot.script.wrappers.GameObject;

public class Map extends MethodProvider {
	public Map(MethodContext factory) {
		super(factory);
	}

	private final GameObject.Type[] TYPES = {
			GameObject.Type.BOUNDARY, GameObject.Type.BOUNDARY,
			GameObject.Type.FLOOR_DECORATION,
			GameObject.Type.WALL_DECORATION, GameObject.Type.WALL_DECORATION
	};

	public CollisionMap[] getCollisionMaps() {
		Client client = ctx.getClient();
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
		RSGroundBytes ground = info.getGroundBytes();
		byte[][][] settings = ground != null ? ground.getBytes() : null;
		if (settings == null) {
			return new CollisionMap[0];
		}
		CollisionMap[] collisionMaps = new CollisionMap[settings.length];
		for (int plane = 0; plane < collisionMaps.length; plane++) {
			int width = settings[plane].length;
			int height = Integer.MAX_VALUE;
			for (int x = 0; x < width; x++) {
				height = Math.min(height, settings[plane][x].length);
			}
			collisionMaps[plane] = new CollisionMap(width, height);
			for (int locX = 0; locX < width; locX++) {
				for (int locY = 0; locY < height; locY++) {
					List<GameObject> objects = getObjects(locX, locY, plane, grounds);
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

	private List<GameObject> getObjects(int x, int y, int plane, RSGround[][][] grounds) {
		List<GameObject> items = new ArrayList<>();
		RSGround ground;
		if (plane < grounds.length && x < grounds[plane].length && y < grounds[plane][x].length) {
			ground = grounds[plane][x][y];
		} else {
			return items;
		}
		if (ground == null) {
			return items;
		}

		for (RSAnimableNode animable = ground.getRSAnimableList(); animable != null; animable = animable.getNext()) {
			Object node = animable.getRSAnimable();
			if (node == null || !(node instanceof RSObject)) {
				continue;
			}
			RSObject obj = (RSObject) node;
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
		for (GameObject next : objects) {
			clippingType = GameObject.clippingTypeForId(next.getId());
			switch (next.getType()) {
			case BOUNDARY:
				if (clippingType == 0) {
					continue;
				}

				RSObject object = rsObject(next);
				if (object == null) {
					continue;
				}
				RSRotatableObject rot = (RSRotatableObject) object;
				collisionMap.markWall(localX, localY, rot.getType(), rot.getOrientation(), false);
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
				collisionMap.markInteractive(localX, localY, false);
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private RSObject rsObject(GameObject object) {
		try {
			Field field = object.getClass().getDeclaredField("object");
			boolean accessable = field.isAccessible();
			field.setAccessible(true);
			WeakReference<RSObject> reference = (WeakReference<RSObject>) field.get(object);
			if (reference != null) {
				return reference.get();
			}
			field.setAccessible(accessable);
		} catch (NoSuchFieldException | IllegalAccessException ignored) {
		}
		return null;
	}

	private CollisionMap getCollisionMap(int plane) {
		CollisionMap[] maps = getCollisionMaps();
		if (plane < 0 || plane >= maps.length) {
			return null;
		}
		return maps[plane];
	}

	public Node[] getPath(int startX, int startY, int endX, int endY, int plane) {
		CollisionMap map = getCollisionMap(plane);
		if (map == null) {
			return new Node[0];
		}
		Graph graph = new Graph(map);
		if (startX < 0 || startY < 0 || endX < 0 || endY < 0 ||
				startX >= graph.width || startY >= graph.height || endX >= graph.width || endY >= graph.height) {
			return new Node[0];
		}

		dijkstra(graph, graph.nodes[startX][startY], graph.nodes[endX][endY]);
		return path(graph.nodes[endX][endY]);
	}

	public int getDistance(int startX, int startY, int endX, int endY, int plane) {
		CollisionMap map = getCollisionMap(plane);
		if (map == null) {
			return -1;
		}
		Graph graph = new Graph(map);
		if (startX < 0 || startY < 0 || endX < 0 || endY < 0 ||
				startX >= graph.width || startY >= graph.height || endX >= graph.width || endY >= graph.height) {
			return -1;
		}

		dijkstra(graph, graph.nodes[startX][startY], graph.nodes[endX][endY]);
		double d = graph.nodes[endX][endY].g;
		if (Double.isInfinite(d)) {
			return -1;
		}
		return (int) d;
	}

	private Node[] path(Node target) {
		List<Node> nodes = new LinkedList<>();
		if (Double.isInfinite(target.g)) {
			return new Node[0];
		}
		while (target != null) {
			nodes.add(target);
			target = target.parent;
		}

		Collections.reverse(nodes);
		Node[] path = new Node[nodes.size()];
		return nodes.toArray(path);
	}

	private void dijkstra(Graph graph, Node source, Node target) {
		source.g = 0d;
		source.f = 0d;

		Queue<Node> queue = new PriorityQueue<>(8, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				return Double.compare(o1.f, o2.f);
			}
		});

		double sqrt2 = Math.sqrt(2);

		queue.add(source);
		source.opened = true;
		while (!queue.isEmpty()) {
			Node node = queue.poll();
			node.closed = true;
			if (node.equals(target)) {
				break;
			}
			for (Node neighbor : graph.neighbors(node)) {
				if (neighbor.closed) {
					continue;
				}
				double ng = node.g + ((neighbor.x - node.x == 0 || neighbor.y - node.y == 0) ? 1d : sqrt2);

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
		private int width, height;
		private Node[][] nodes;

		private Graph(CollisionMap map) {
			this.width = map.getWidth() - 6;
			this.height = map.getHeight() - 6;
			this.nodes = new Node[width][height];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					Node node = new Node(x, y);
					node.flag = map.getClippingValueAtLocal(x, y);
					nodes[x][y] = node;
				}
			}
		}

		private List<Node> neighbors(Node node) {
			List<Node> list = new ArrayList<>(8);
			int curr_x = node.x;
			int curr_y = node.y;
			if (curr_x < 0 || curr_y < 0 ||
					curr_x >= width || curr_y >= height) {
				return list;
			}
			if (curr_y > 0 &&
					!nodes[curr_x][curr_y].flag.marked(CollisionFlag.SOUTH) &&
					!nodes[curr_x][curr_y - 1].flag.marked(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) {
				list.add(nodes[curr_x][curr_y - 1]);
			}
			if (curr_x > 0 &&
					!nodes[curr_x][curr_y].flag.marked(CollisionFlag.WEST) &&
					!nodes[curr_x - 1][curr_y].flag.marked(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) {
				list.add(nodes[curr_x - 1][curr_y]);
			}
			if (curr_y < height - 1 &&
					!nodes[curr_x][curr_y].flag.marked(CollisionFlag.NORTH) &&
					!nodes[curr_x][curr_y + 1].flag.marked(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) {
				list.add(nodes[curr_x][curr_y + 1]);
			}
			if (curr_x < width - 1 &&
					!nodes[curr_x][curr_y].flag.marked(CollisionFlag.EAST) &&
					!nodes[curr_x + 1][curr_y].flag.marked(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) {
				list.add(nodes[curr_x + 1][curr_y]);
			}
			if (curr_x > 0 && curr_y > 0 &&
					!nodes[curr_x][curr_y].flag.marked(CollisionFlag.SOUTHWEST.mark(CollisionFlag.SOUTH.mark(CollisionFlag.WEST))) &&
					!nodes[curr_x - 1][curr_y - 1].flag.marked(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)) &&
					!nodes[curr_x][curr_y - 1].flag.marked(CollisionFlag.WEST.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) &&
					!nodes[curr_x - 1][curr_y].flag.marked(CollisionFlag.SOUTH.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)))) {
				list.add(nodes[curr_x - 1][curr_y - 1]);
			}
			if (curr_x > 0 && curr_y < height - 1 &&
					!nodes[curr_x][curr_y].flag.marked(CollisionFlag.NORTHWEST.mark(CollisionFlag.NORTH.mark(CollisionFlag.WEST))) &&
					!nodes[curr_x - 1][curr_y + 1].flag.marked(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)) &&
					!nodes[curr_x][curr_y + 1].flag.marked(CollisionFlag.WEST.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) &&
					!nodes[curr_x - 1][curr_y].flag.marked(CollisionFlag.NORTH.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)))) {
				list.add(nodes[curr_x - 1][curr_y + 1]);
			}
			if (curr_x < height - 1 && curr_y > 0 &&
					!nodes[curr_x][curr_y].flag.marked(CollisionFlag.SOUTHEAST.mark(CollisionFlag.SOUTH.mark(CollisionFlag.EAST))) &&
					!nodes[curr_x + 1][curr_y - 1].flag.marked(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)) &&
					!nodes[curr_x][curr_y - 1].flag.marked(CollisionFlag.EAST.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) &&
					!nodes[curr_x + 1][curr_y].flag.marked(CollisionFlag.SOUTH.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)))) {
				list.add(nodes[curr_x + 1][curr_y - 1]);
			}
			if (curr_x < width - 1 && curr_y < height - 1 &&
					!nodes[curr_x][curr_y].flag.marked(CollisionFlag.NORTHEAST.mark(CollisionFlag.NORTH.mark(CollisionFlag.WEST))) &&
					!nodes[curr_x + 1][curr_y + 1].flag.marked(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)) &&
					!nodes[curr_x][curr_y + 1].flag.marked(CollisionFlag.EAST.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK))) &&
					!nodes[curr_x + 1][curr_y].flag.marked(CollisionFlag.NORTH.mark(CollisionFlag.OBJECT_BLOCK.mark(CollisionFlag.DEAD_BLOCK)))) {
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

		private Node(int x, int y) {
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
		public boolean equals(Object o) {
			if (o == null || !(o instanceof Node)) {
				return false;
			}
			Node n = (Node) o;
			return x == n.x && y == n.y;
		}
	}
}
