package org.powerbot.script.methods;

import java.util.LinkedHashSet;
import java.util.Set;

import org.powerbot.bot.World;
import org.powerbot.client.Client;
import org.powerbot.client.RSAnimableNode;
import org.powerbot.client.RSGround;
import org.powerbot.client.RSGroundInfo;
import org.powerbot.client.RSInfo;
import org.powerbot.client.RSObject;
import org.powerbot.script.wrappers.GameObject;

public class Objects {
	public static GameObject[] getLoaded() {
		final Set<GameObject> objects = new LinkedHashSet<>();
		final Client client = World.getWorld().getClient();
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
}
