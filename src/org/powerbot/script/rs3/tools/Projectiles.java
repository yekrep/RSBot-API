package org.powerbot.script.rs3.tools;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.rs3.client.Client;
import org.powerbot.bot.rs3.client.Node;
import org.powerbot.bot.rs3.client.NodeDeque;
import org.powerbot.bot.rs3.client.RSProjectile;
import org.powerbot.bot.rs3.client.RSProjectileNode;
import org.powerbot.bot.rs3.tools.Deque;
import org.powerbot.script.lang.IdQuery;

/**
 * {@link Projectiles} is a utility which provides access to the game's projectiles.
 * <p/>
 * {@link Projectile}s are game projectiles on the current plane which target an entity.
 *
 * @author Timer
 */
public class Projectiles extends IdQuery<Projectile> {
	public Projectiles(final MethodContext factory) {
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

		final NodeDeque deque = client.getProjectileDeque();
		if (deque == null) {
			return items;
		}

		final Deque<Node> nodes = new Deque<Node>(deque, Node.class);
		for (Node node = nodes.getHead(); node != null; node = nodes.getNext()) {
			final RSProjectile projectile;
			if (node instanceof RSProjectileNode && (projectile = ((RSProjectileNode) node).getProjectile()) != null) {
				items.add(new Projectile(ctx, projectile));
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
