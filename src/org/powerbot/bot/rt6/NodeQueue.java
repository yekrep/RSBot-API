package org.powerbot.bot.rt6;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt6.client.Node;
import org.powerbot.bot.rt6.client.NodeDeque;
import org.powerbot.bot.rt6.client.NodeSub;
import org.powerbot.bot.rt6.client.NodeSubQueue;

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

	public static <E extends NodeSub> List<E> get(final NodeSubQueue q, final Class<E> type) {
		final List<E> list = new ArrayList<E>();
		NodeSub e;
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
		e = e.getNextSub();

		for (; !e.isNull() && e.isTypeOf(type) && !e.equals(s); e = e.getNextSub()) {
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
