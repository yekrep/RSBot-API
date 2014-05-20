package org.powerbot.script.rt6;

import org.powerbot.bot.rt6.client.RSInteractableData;
import org.powerbot.bot.rt6.client.RSInteractableLocation;
import org.powerbot.bot.rt6.client.RSProjectile;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Tile;
import org.powerbot.script.Validatable;

public class Projectile extends ClientAccessor implements Locatable, Identifiable, Validatable {
	private final RSProjectile projectile;

	public Projectile(final ClientContext ctx, final RSProjectile projectile) {
		super(ctx);
		this.projectile = projectile;
	}

	@Override
	public int id() {
		return projectile.getID();
	}

	@Override
	public boolean valid() {
		return projectile.obj.get() != null && ctx.projectiles.select().contains(this);
	}

	@Override
	public int hashCode() {
		final Object i;
		return (i = this.projectile.obj.get()) != null ? System.identityHashCode(i) : 0;
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof Projectile && projectile.equals(((Projectile) o).projectile);
	}

	@Override
	public Tile tile() {
		final RelativeLocation position = relative();
		if (projectile.obj.get() != null && position != RelativeLocation.NIL) {
			return ctx.game.mapOffset().derive((int) position.x() >> 9, (int) position.z() >> 9, projectile.getPlane());
		}
		return Tile.NIL;
	}

	public RelativeLocation relative() {
		final RSInteractableData data = projectile.getData();
		final RSInteractableLocation location = data != null ? data.getLocation() : null;
		if (location != null) {
			return new RelativeLocation(location.getX(), location.getY());
		}
		return RelativeLocation.NIL;
	}
}
