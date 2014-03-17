package org.powerbot.script.rt6;

import java.lang.ref.WeakReference;

import org.powerbot.bot.rt6.client.RSInteractableData;
import org.powerbot.bot.rt6.client.RSInteractableLocation;
import org.powerbot.bot.rt6.client.RSProjectile;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Tile;
import org.powerbot.script.Validatable;

public class Projectile extends ClientAccessor implements Locatable, Identifiable, Validatable {
	private final WeakReference<RSProjectile> projectile;

	public Projectile(final ClientContext ctx, final RSProjectile projectile) {
		super(ctx);
		this.projectile = new WeakReference<RSProjectile>(projectile);
	}

	@Override
	public int id() {
		final RSProjectile projectile = this.projectile.get();
		return projectile != null ? projectile.getID() : -1;
	}

	@Override
	public boolean valid() {
		final RSProjectile projectile = this.projectile.get();
		return projectile != null && ctx.projectiles.select().contains(this);
	}

	@Override
	public int hashCode() {
		final RSProjectile i;
		return (i = this.projectile.get()) != null ? System.identityHashCode(i) : 0;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Projectile)) {
			return false;
		}
		final Projectile c = (Projectile) o;
		final RSProjectile i;
		return (i = this.projectile.get()) != null && i == c.projectile.get();
	}

	@Override
	public Tile tile() {
		final RSProjectile projectile = this.projectile.get();
		final RelativeLocation position = relative();
		if (projectile != null && position != RelativeLocation.NIL) {
			return ctx.game.mapOffset().derive((int) position.x() >> 9, (int) position.z() >> 9, projectile.getPlane());
		}
		return Tile.NIL;
	}

	public RelativeLocation relative() {
		final RSProjectile projectile = this.projectile.get();
		final RSInteractableData data = projectile != null ? projectile.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return new RelativeLocation(location.getX(), location.getY());
		}
		return RelativeLocation.NIL;
	}
}
