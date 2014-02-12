package org.powerbot.os.api.wrappers;

import java.awt.Rectangle;
import java.lang.ref.WeakReference;

import org.powerbot.os.api.methods.ClientContext;
import org.powerbot.os.client.Actor;

public class ActorCuboid extends Cuboid {
	private final WeakReference<org.powerbot.os.client.Actor> actor;

	public ActorCuboid(final ClientContext ctx, final Actor actor) {
		super(ctx);
		this.actor = new WeakReference<Actor>(actor);
	}

	@Override
	public int getX() {
		final Actor actor = this.actor.get();
		return actor != null ? actor.getX() : -1;
	}

	@Override
	public int getZ() {
		final Actor actor = this.actor.get();
		return actor != null ? actor.getZ() : -1;
	}

	@Override
	public int getHeight() {
		final Actor actor = this.actor.get();
		return actor != null ? actor.getHeight() : -1;
	}

	@Override
	public Rectangle getBounds() {
		final int x = getX(), z = getZ();
		if (x == -1 || z == -1) {
			return new Rectangle(-1, -1, -1, -1);
		}
		final int gx = x >> 7, gy = z >> 7;
		return new Rectangle(gx, gy, 0, 0);
	}
}
