package org.powerbot.script.methods;

import org.powerbot.client.Client;
import org.powerbot.client.Node;
import org.powerbot.client.NodeDeque;
import org.powerbot.client.RSProjectile;
import org.powerbot.client.RSProjectileNode;
import org.powerbot.script.internal.wrappers.Deque;
import org.powerbot.script.lang.IdQuery;
import org.powerbot.script.wrappers.Projectile;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Projectiles} is a static utility which provides access to the game's projectiles.
 * <p/>
 * {@link Projectile}s are game projectiles on the current plane which target an entity.
 *
 * @author Timer
 */
public class Projectiles extends IdQuery<Projectile> {
	public Projectiles(ClientFactory factory) {
		super(factory);
	}

	/**
	 * Returns the {@link Projectile}s in the region.
	 *
	 * @return an array of loaded {@link Projectile}s
	 */
	@Override
	protected List<Projectile> list() {
		final List<Projectile> items = new ArrayList<>();

		Client client = ctx.getClient();
		if (client == null) {
			return items;
		}

		final NodeDeque deque = client.getProjectileDeque();
		if (deque == null) {
			return items;
		}

		final Deque<Node> nodes = new Deque<>(deque);
		for (Node node = nodes.getHead(); node != null; node = nodes.getNext()) {
			final RSProjectile projectile;
			if (node instanceof RSProjectileNode && (projectile = ((RSProjectileNode) node).getProjectile()) != null) {
				items.add(new Projectile(ctx, projectile));
			}
		}
		return items;
	}
}
