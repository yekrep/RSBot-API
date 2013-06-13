package org.powerbot.script.methods;

import java.util.HashSet;
import java.util.Set;

import org.powerbot.client.Client;
import org.powerbot.client.Node;
import org.powerbot.client.NodeDeque;
import org.powerbot.client.RSProjectile;
import org.powerbot.client.RSProjectileNode;
import org.powerbot.script.internal.wrappers.Deque;
import org.powerbot.script.lang.BasicQuery;
import org.powerbot.script.wrappers.Projectile;

/**
 * {@link Projectiles} is a static utility which provides access to the game's projectiles.
 * <p/>
 * {@link Projectile}s are game projectiles on the current plane which target an entity.
 *
 * @author Timer
 */
public class Projectiles extends BasicQuery<Projectile> {
	public Projectiles(ClientFactory factory) {
		super(factory);
	}

	/**
	 * Returns the {@link Projectile}s in the region.
	 *
	 * @return an array of loaded {@link Projectile}s
	 */
	@Override
	protected Projectile[] get() {
		Client client = ctx.getClient();
		if (client == null) {
			return new Projectile[0];
		}

		final NodeDeque deque = client.getProjectileDeque();
		if (deque == null) {
			return new Projectile[0];
		}

		final Set<Projectile> projectiles = new HashSet<>();
		final Deque<Node> nodes = new Deque<>(deque);
		for (Node node = nodes.getHead(); node != null; node = nodes.getNext()) {
			final RSProjectile projectile;
			if (node instanceof RSProjectileNode && (projectile = ((RSProjectileNode) node).getProjectile()) != null) {
				projectiles.add(new Projectile(ctx, projectile));
			}
		}
		return projectiles.toArray(new Projectile[projectiles.size()]);
	}
}
