package org.powerbot.core.script.wrappers;

import java.lang.ref.WeakReference;

import org.powerbot.core.script.methods.Projectiles;
import org.powerbot.game.client.RSProjectile;

public class Projectile implements Validatable {
	private final WeakReference<RSProjectile> projectile;

	public Projectile(final RSProjectile projectile) {
		this.projectile = new WeakReference<>(projectile);
	}

	public int getId() {
		final RSProjectile projectile = this.projectile.get();
		return projectile != null ? projectile.getID() : -1;
	}

	@Override
	public boolean isValid() {
		final RSProjectile projectile = this.projectile.get();
		if (projectile == null) return false;
		return Projectiles.getLoaded().contains(this);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Projectile)) return false;
		final Projectile c = (Projectile) o;
		final RSProjectile p1 = this.projectile.get();
		final RSProjectile p2 = c.projectile.get();
		return p1 != null && p2 != null && p1 == p2;
	}
}
