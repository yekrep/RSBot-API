package org.powerbot.script.methods;

import org.powerbot.client.Client;
import org.powerbot.client.RSAnimableNode;
import org.powerbot.client.RSGround;
import org.powerbot.client.RSGroundInfo;
import org.powerbot.client.RSInfo;
import org.powerbot.client.RSObject;
import org.powerbot.script.lang.LocatableIdQuery;
import org.powerbot.script.wrappers.GameObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Objects extends LocatableIdQuery<GameObject> {
	public Objects(ClientFactory factory) {
		super(factory);
	}

	@Override
	protected List<GameObject> get() {
		final List<GameObject> items = new ArrayList<>();

		Client client = ctx.getClient();
		if (client == null) {
			return items;
		}

		final RSInfo info;
		final RSGroundInfo groundInfo;
		final RSGround[][][] grounds;
		if ((info = client.getRSGroundInfo()) == null || (groundInfo = info.getRSGroundInfo()) == null ||
				(grounds = groundInfo.getRSGroundArray()) == null) {
			return items;
		}

		final GameObject.Type[] types = {
				GameObject.Type.BOUNDARY, GameObject.Type.BOUNDARY,
				GameObject.Type.FLOOR_DECORATION,
				GameObject.Type.WALL_DECORATION, GameObject.Type.WALL_DECORATION
		};

		final int plane = client.getPlane();

		final RSGround[][] objArr = plane > -1 && plane < grounds.length ? grounds[plane] : null;
		if (objArr == null) {
			return items;
		}

		Set<RSObject> refs = new HashSet<>();
		for (int x = 0; x <= objArr.length - 1; x++) {
			for (int y = 0; y <= objArr[x].length - 1; y++) {
				final RSGround ground = objArr[x][y];
				if (ground == null) {
					continue;
				}

				for (RSAnimableNode node = ground.getRSAnimableList(); node != null; node = node.getNext()) {
					final RSObject obj = node.getRSAnimable();
					if (obj != null && obj.getId() != -1 && !refs.contains(obj)) {
						refs.add(obj);
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
						items.add(new GameObject(ctx, objs[i], types[i]));
					}
				}
			}
		}
		return items;
	}
}
