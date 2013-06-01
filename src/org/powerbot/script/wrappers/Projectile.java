package org.powerbot.script.wrappers;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import org.powerbot.client.RSProjectile;
import org.powerbot.script.methods.World;
import org.powerbot.script.methods.WorldImpl;

public class Projectile extends WorldImpl implements Validatable {
	private final WeakReference<RSProjectile> projectile;

	public Projectile(World world, final RSProjectile projectile) {
		super(world);
		this.projectile = new WeakReference<>(projectile);
	}

	public int getId() {
		final RSProjectile projectile = this.projectile.get();
		return projectile != null ? projectile.getID() : -1;
	}

	@Override
	public boolean isValid() {
		final RSProjectile projectile = this.projectile.get();
		return projectile != null && Arrays.asList(world.projectiles.getLoaded()).contains(this);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Projectile)) return false;
		final Projectile c = (Projectile) o;
		final RSProjectile i;
		return (i = this.projectile.get()) != null && i == c.projectile.get();
	}
}
