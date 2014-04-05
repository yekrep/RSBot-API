package org.powerbot.script;

import java.util.Collection;
import java.util.EventListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.bot.ScriptEventDispatcher;

public abstract class ClientContext<C extends Client> {
	private final AtomicReference<Bot<? extends ClientContext<C>>> bot;
	private final AtomicReference<C> client;

	public final Map<String, String> properties;
	public final Collection<EventListener> dispatcher;

	protected ClientContext(final Bot<? extends ClientContext<C>> bot) {
		this.bot = new AtomicReference<Bot<? extends ClientContext<C>>>(bot);
		client = new AtomicReference<C>(null);
		properties = new ConcurrentHashMap<String, String>();
		dispatcher = new ScriptEventDispatcher<C, EventListener>(this);
	}

	protected ClientContext(final ClientContext<C> ctx) {
		bot = ctx.bot;
		client = ctx.client;
		properties = ctx.properties;
		dispatcher = ctx.dispatcher;
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
