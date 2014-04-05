package org.powerbot.script;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ClientContext<C extends Client> {
	private final AtomicReference<Bot<? extends ClientContext<C>>> bot;
	private final AtomicReference<C> client;

	public final Map<String, String> properties;

	private final Collection<EventListener> events;

	protected ClientContext(final Bot<? extends ClientContext<C>> bot) {
		this.bot = new AtomicReference<Bot<? extends ClientContext<C>>>(bot);
		client = new AtomicReference<C>(null);
		properties = new ConcurrentHashMap<String, String>();
		events = new ArrayList<EventListener>();
	}

	protected ClientContext(final ClientContext<C> ctx) {
		bot = ctx.bot;
		client = ctx.client;
		properties = ctx.properties;
		events = ctx.events;
	}

	public abstract String rtv();

	public final Bot<? extends ClientContext<C>> bot() {
		return bot.get();
	}

	public final C client() {
		return client.get();
	}

	public final C client(final C c) {
		return client.getAndSet(c);
	}

	public abstract Script.Controller controller();

	public final Collection<EventListener> dispatcher() {
		return new AbstractCollection<EventListener>() {
			@Override
			public boolean add(final EventListener e) {
				return events.add(e) && bot.get().dispatcher.add(e);
			}

			@Override
			public Iterator<EventListener> iterator() {
				final Iterator<EventListener> it = events.iterator();
				return new Iterator<EventListener>() {
					private final AtomicReference<EventListener> item = new AtomicReference<EventListener>(null);

					@Override
					public boolean hasNext() {
						return it.hasNext();
					}

					@Override
					public EventListener next() {
						item.set(it.next());
						return item.get();
					}

					@Override
					public void remove() {
						if (item.get() == null) {
							throw new IllegalStateException();
						}
						bot.get().dispatcher.remove(item.getAndSet(null));
					}
				};
			}

			@Override
			public int size() {
				return events.size();
			}
		};
	}

	public final String property(final String k) {
		return property(k, "");
	}

	public final String property(final String k, final String d) {
		if (k == null || k.isEmpty()) {
			return "";
		}
		final String v = properties.get(k);
		return v == null || v.isEmpty() ? d : v;
	}
}
