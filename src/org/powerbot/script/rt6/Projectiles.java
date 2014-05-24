package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.RSProjectile;
import org.powerbot.bot.rt6.client.RSProjectileNode;
import org.powerbot.bot.rt6.tools.NodeQueue;

/**
 * {@link Projectiles} is a utility which provides access to the game's projectiles.
 *
 * {@link Projectile}s are game projectiles on the current plane which target an entity.
 *
 */
public class Projectiles extends IdQuery<Projectile> {
	public Projectiles(final ClientContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Projectile> get() {
		final List<Projectile> items = new ArrayList<Projectile>();

		final Client client = ctx.client();
		if (client == null) {
			return items;
		}

		for (final RSProjectileNode n : NodeQueue.get(client.getProjectileDeque(), RSProjectileNode.class)) {
			final RSProjectile p = n.getProjectile();
			if (p != null) {
				items.add(new Projectile(ctx, p));
			}
		}

		return items;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Projectile nil() {
		return new Projectile(ctx, new RSProjectile(ctx.client().reflector, null));
	}
}
