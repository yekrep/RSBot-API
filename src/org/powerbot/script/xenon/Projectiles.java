package org.powerbot.script.xenon;

import java.util.HashSet;
import java.util.Set;

import org.powerbot.bot.Bot;
import org.powerbot.client.Client;
import org.powerbot.client.Node;
import org.powerbot.client.NodeDeque;
import org.powerbot.client.RSProjectile;
import org.powerbot.client.RSProjectileNode;
import org.powerbot.script.internal.wrappers.Deque;
import org.powerbot.script.xenon.wrappers.Projectile;

public class Projectiles {
	public static Projectile[] getLoaded() {
		final Client client = Bot.client();
		if (client == null) return new Projectile[0];

		final NodeDeque deque = client.getProjectileDeque();
		if (deque == null) return new Projectile[0];

		final Set<Projectile> projectiles = new HashSet<>();
		final Deque<Node> nodes = new Deque<>(deque);
		for (Node node = nodes.getHead(); node != null; node = nodes.getNext()) {
			final RSProjectile projectile;
			if (node instanceof RSProjectileNode && (projectile = ((RSProjectileNode) node).getProjectile()) != null) {
				projectiles.add(new Projectile(projectile));
			}
		}
		return projectiles.toArray(new Projectile[projectiles.size()]);
	}
}
