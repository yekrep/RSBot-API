package org.powerbot.os.api.tools;

import java.awt.Color;
import java.awt.Point;
import java.lang.ref.WeakReference;

import org.powerbot.os.api.ClientContext;
import org.powerbot.os.bot.client.BasicObject;
import org.powerbot.os.bot.client.Client;

public class GameObject extends Interactive implements Locatable, Identifiable {
	private static final Color TARGET_COLOR = new Color(0, 255, 0, 20);
	private final WeakReference<BasicObject> object;

	public GameObject(final ClientContext ctx, final BasicObject object) {
		super(ctx);
		this.object = new WeakReference<BasicObject>(object);
	}

	@Override
	public int getId() {
		final BasicObject object = this.object.get();
		return object != null ? (object.getUid() >> 14) & 0xffff : -1;
	}

	public int getOrientation() {
		final BasicObject object = this.object.get();
		return object != null ? object.getMeta() >> 6 : 0;
	}

	public int getType() {
		final BasicObject object = this.object.get();
		return object != null ? object.getMeta() & 0x3f : 0;
	}

	public RelativePosition getRelativePosition() {
		final BasicObject object = this.object.get();
		final int x, z;
		if (object != null) {
			if (object instanceof org.powerbot.os.bot.client.GameObject) {
				final org.powerbot.os.bot.client.GameObject o2 = (org.powerbot.os.bot.client.GameObject) object;
				x = o2.getX();
				z = o2.getZ();
			} else {
				final int uid = object.getUid();
				x = (uid & 0x7f) << 7;
				z = ((uid >> 7) & 0x7f) << 7;
			}
		} else {
			x = z = 0;
		}
		return new RelativePosition(x, z);
	}

	@Override
	public Tile getLocation() {
		final Client client = ctx.client();
		final RelativePosition r = getRelativePosition();
		if (client != null && r.x != 0 && r.z != 0) {
			return new Tile(client.getOffsetX() + (r.x >> 7), client.getOffsetY() + (r.z >> 7), client.getFloor());
		}
		return new Tile(-1, -1, -1);
	}

	@Override
	public Point getCenterPoint() {
		return getLocation().getMatrix(ctx).getCenterPoint();
	}

	@Override
	public Point getNextPoint() {
		return getLocation().getMatrix(ctx).getNextPoint();
	}

	@Override
	public boolean contains(final Point point) {
		return getLocation().getMatrix(ctx).contains(point);
	}
}
