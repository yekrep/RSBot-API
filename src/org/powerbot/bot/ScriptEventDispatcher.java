package org.powerbot.bot;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.script.Client;
import org.powerbot.script.ClientContext;

public class ScriptEventDispatcher<C extends Client, E extends EventListener> extends AbstractCollection<E> {
	private final ClientContext<C> ctx;
	private final Collection<E> c;

	public ScriptEventDispatcher(final ClientContext<C> ctx) {
		this.ctx = ctx;
		c = new ArrayList<E>();
	}

	@Override
	public boolean add(final E e) {
		return ((AbstractBot) ctx.bot()).dispatcher.add(e) && c.add(e);
	}

	@Override
	public Iterator<E> iterator() {
		return new ListIterator<C, E>(ctx, c.iterator());
	}

	@Override
	public int size() {
		return c.size();
	}

	private final class ListIterator<C1 extends C, E1 extends E> implements Iterator<E1> {
		private final ClientContext<C1> ctx;
		private final Iterator<E1> iterator;
		private final AtomicReference<E1> ref;

		public ListIterator(final ClientContext<C1> ctx, final Iterator<E1> iterator) {
			this.ctx = ctx;
			this.iterator = iterator;
			ref = new AtomicReference<E1>(null);
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public E1 next() {
			ref.set(iterator.next());
			return ref.get();
		}

		@Override
		public void remove() {
			if (ref.get() == null) {
				throw new IllegalStateException();
			}
			((AbstractBot) ctx.bot()).dispatcher.remove(ref.getAndSet(null));
			iterator.remove();
		}
	}
}
