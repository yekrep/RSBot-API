package org.powerbot.script.wrappers;

import java.lang.ref.WeakReference;

import org.powerbot.client.RSProjectile;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.methods.ClientLink;

public class Projectile extends ClientLink implements Validatable {
	private final WeakReference<RSProjectile> projectile;

	public Projectile(ClientFactory ctx, final RSProjectile projectile) {
		super(ctx);
		this.projectile = new WeakReference<>(projectile);
	}

	public int getId() {
		final RSProjectile projectile = this.projectile.get();
		return projectile != null ? projectile.getID() : -1;
	}

	@Override
	public boolean isValid() {
		final RSProjectile projectile = this.projectile.get();
		return projectile != null && ctx.projectiles.contains(this);
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
