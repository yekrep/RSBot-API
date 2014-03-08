package org.powerbot.script.lang;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class ClientContext {
	private final AtomicReference<Bot> bot;

	public final Map<String, String> properties;

	protected ClientContext(final Bot bot) {
		this.bot = new AtomicReference<Bot>(bot);
		properties = new ConcurrentHashMap<String, String>();
	}

	public final Bot bot() {
		return bot.get();
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
