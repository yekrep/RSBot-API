package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.rt6.NodeQueue;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.Projectile;
import org.powerbot.bot.rt6.client.ProjectileNode;

/**
 * Projectiles
 * {@link Projectiles} is a utility which provides access to the game's projectiles.
 * 
 * {@link org.powerbot.script.rt6.Projectile}s are game projectiles on the current plane which target an entity.
 */
public class Projectiles extends IdQuery<org.powerbot.script.rt6.Projectile> {
	public Projectiles(final ClientContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<org.powerbot.script.rt6.Projectile> get() {
		final List<org.powerbot.script.rt6.Projectile> items = new ArrayList<org.powerbot.script.rt6.Projectile>();

		final Client client = ctx.client();
		if (client == null) {
			return items;
		}

		for (final ProjectileNode n : NodeQueue.get(client.getProjectileDeque(), ProjectileNode.class)) {
			final Projectile p = n.getProjectile();
			if (!p.isNull()) {
				items.add(new org.powerbot.script.rt6.Projectile(ctx, p));
			}
		}

		return items;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.powerbot.script.rt6.Projectile nil() {
		return new org.powerbot.script.rt6.Projectile(ctx, new Projectile(ctx.client().reflector, null));
	}
}
