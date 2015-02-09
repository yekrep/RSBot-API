package org.powerbot.bot.rt4;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt4.client.Node;
import org.powerbot.bot.rt4.client.NodeDeque;

public class NodeQueue {
	public static <E extends ReflectProxy> List<E> get(final NodeDeque q, final Class<E> type) {
		final List<E> list = new ArrayList<E>();
		Node e;
		final Constructor<E> c;
		try {
			c = type.getDeclaredConstructor(Reflector.class, Object.class);
		} catch (final NoSuchMethodException ignored) {
			return list;
		}
		final Node s;
		if (q == null || (s = e = q.getSentinel()) == null) {
			return list;
		}
		e = e.getNext();

		for (; !e.isNull() && e.isTypeOf(type) && !e.equals(s); e = e.getNext()) {
			try {
				list.add(c.newInstance(q.reflector, e));
			} catch (final InstantiationException ignored) {
			} catch (final IllegalAccessException ignored) {
			} catch (final InvocationTargetException ignored) {
			}
		}

		return list;
	}
}
