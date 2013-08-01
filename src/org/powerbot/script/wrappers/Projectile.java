package org.powerbot.script.wrappers;

import java.lang.ref.WeakReference;

import org.powerbot.client.RSProjectile;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;

public class Projectile extends MethodProvider implements Identifiable, Validatable {
	private final WeakReference<RSProjectile> projectile;

	public Projectile(MethodContext ctx, final RSProjectile projectile) {
		super(ctx);
		this.projectile = new WeakReference<>(projectile);
	}

	@Override
	public int getId() {
		final RSProjectile projectile = this.projectile.get();
		return projectile != null ? projectile.getID() : -1;
	}

	@Override
	public boolean isValid() {
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
}
