package org.powerbot.script.rs3;

import java.lang.ref.WeakReference;

import org.powerbot.bot.rs3.client.RSInteractableData;
import org.powerbot.bot.rs3.client.RSInteractableLocation;
import org.powerbot.bot.rs3.client.RSProjectile;
import org.powerbot.script.Identifiable;
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
	public Tile getLocation() {
		final RSProjectile projectile = this.projectile.get();
		final RelativeLocation position = getRelative();
		if (projectile != null && position != RelativeLocation.NIL) {
			return ctx.game.getMapBase().derive((int) position.getX() >> 9, (int) position.getY() >> 9, projectile.getPlane());
		}
		return Tile.NIL;
	}

	public RelativeLocation getRelative() {
		final RSProjectile projectile = this.projectile.get();
		final RSInteractableData data = projectile != null ? projectile.getData() : null;
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return new RelativeLocation(location.getX(), location.getY());
		}
		return RelativeLocation.NIL;
	}
}
