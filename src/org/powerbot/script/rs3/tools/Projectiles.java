package org.powerbot.script.rs3.tools;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.rs3.client.Client;
import org.powerbot.bot.rs3.client.RSProjectile;
import org.powerbot.bot.rs3.client.RSProjectileNode;
import org.powerbot.bot.rs3.tools.NodeQueue;
import org.powerbot.script.lang.IdQuery;

/**
 * {@link Projectiles} is a utility which provides access to the game's projectiles.
 * <p/>
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

		final Client client = ctx.getClient();
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
	public Projectile getNil() {
		return new Projectile(ctx, null);
	}
}
