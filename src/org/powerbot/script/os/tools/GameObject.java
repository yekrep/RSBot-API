package org.powerbot.script.os.tools;

import java.awt.Color;
import java.awt.Point;
import java.lang.ref.WeakReference;

import org.powerbot.script.ClientContext;
import org.powerbot.bot.os.client.BasicObject;
import org.powerbot.bot.os.client.Client;

public class GameObject extends Interactive implements Locatable, Identifiable {
	private static final Color TARGET_COLOR = new Color(0, 255, 0, 20);
	private final WeakReference<BasicObject> object;

	GameObject(final ClientContext ctx, final BasicObject object) {
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

	public int getRelativePosition() {
		final BasicObject object = this.object.get();
		final int x, z;
		if (object != null) {
			if (object instanceof org.powerbot.bot.os.client.GameObject) {
				final org.powerbot.bot.os.client.GameObject o2 = (org.powerbot.bot.os.client.GameObject) object;
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
		return (x << 16) | z;
	}

	@Override
	public Tile getLocation() {
		final Client client = ctx.client();
		final int r = getRelativePosition();
		final int rx = r >> 16, rz = r & 0xffff;
		if (client != null && rx != 0 && rz != 0) {
			return new Tile(client.getOffsetX() + (rx >> 7), client.getOffsetY() + (rz >> 7), client.getFloor());
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
