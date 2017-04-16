package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class HashTable extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public HashTable(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Node[] getBuckets() {
		final Object[] arr = reflector.access(this, a, Object[].class);
		final Node[] arr2 = arr != null ? new Node[arr.length] : new Node[0];
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Node(reflector, arr[i]);
			}
		}
		return arr2;
	}
}
