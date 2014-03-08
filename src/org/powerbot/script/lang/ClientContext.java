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
}
