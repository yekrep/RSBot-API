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
	public Iterator<E> iterator() {
		return new ListIterator<C, E>(ctx, c.iterator());
	}

	@Override
	public int size() {
		return c.size();
	}

	private final class ListIterator<Ca extends Client, Ea> implements Iterator<Ea> {
		private final ClientContext<Ca> ctx;
		private final Iterator<Ea> iterator;
		private final AtomicReference<Ea> ref;

		public ListIterator(final ClientContext<Ca> ctx, final Iterator<Ea> iterator) {
			this.ctx = ctx;
			this.iterator = iterator;
			ref = new AtomicReference<Ea>(null);
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Ea next() {
			ref.set(iterator.next());
			return ref.get();
		}

		@Override
		public void remove() {
			if (ref.get() == null) {
				throw new IllegalStateException();
			}
			ctx.bot().dispatcher.remove(ref.getAndSet(null));
			iterator.remove();
		}
	}
}
