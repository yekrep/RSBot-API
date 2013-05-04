package org.powerbot.script.xenon.wrappers;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import org.powerbot.client.RSProjectile;
import org.powerbot.script.xenon.Projectiles;

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
		return projectile != null && Arrays.asList(Projectiles.getLoaded()).contains(this);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Projectile)) return false;
		final Projectile c = (Projectile) o;
		final RSProjectile i;
		return (i = this.projectile.get()) != null && i == c.projectile.get();
	}
}
