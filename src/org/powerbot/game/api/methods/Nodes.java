package org.powerbot.game.api.methods;

import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.HashTable2Buckets;
import org.powerbot.game.client.HashTableBuckets;
import org.powerbot.game.client.Node;

/**
 * @author Timer
 */
public class Nodes {
	public Node lookup(final Object nc, final long id) {
		try {
			if (nc == null || id < 0l) {
				return null;
			}
			Node[] nodes;
			Object object = null;
			if (nc instanceof HashTableBuckets) {
				object = ((HashTableBuckets) nc).getHashTableBuckets();
			} else if (nc instanceof HashTable2Buckets) {
				object = ((HashTable2Buckets) nc).getHashTable2Buckets();
			}
			if (object == null) {
				return null;
			}
			nodes = (Node[]) object;
			final int idMultiplier = Bot.resolve().multipliers.NODE_ID;
			final Node n = nodes[(int) (id & (long) (nodes.length - 1))].getPrevious();
			for (Node node = n.getPrevious(); node != n; node = node.getPrevious()) {
				if (node.getID() * idMultiplier == id) {
					return node;
				}
			}
		} catch (final Exception ignored) {
		}
		return null;
	}
}
