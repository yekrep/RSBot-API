package org.powerbot.script.rt6;

import org.powerbot.bot.rt6.client.GameLocation;
import org.powerbot.bot.rt6.client.RelativePosition;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Locatable;
import org.powerbot.script.Tile;
import org.powerbot.script.Validatable;

/**
 * Projectile
 */
public class Projectile extends ClientAccessor implements Locatable, Identifiable, Validatable {
	private final org.powerbot.bot.rt6.client.Projectile projectile;

	public Projectile(final ClientContext ctx, final org.powerbot.bot.rt6.client.Projectile projectile) {
		super(ctx);
		this.projectile = projectile;
	}

	@Override
	public int id() {
		return projectile.getId();
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
			return ctx.game.mapOffset().derive((int) position.x() >> 9, (int) position.z() >> 9, position.floor());
		}
		return Tile.NIL;
	}

	public RelativeLocation relative() {
		final GameLocation data = projectile.getLocation();
		final RelativePosition location = data != null ? data.getRelativePosition() : null;
		if (location != null) {
			return new RelativeLocation(location.getX(), location.getZ(), ctx.game.floor());
		}
		return RelativeLocation.NIL;
	}
}
