package org.powerbot.script;

import java.util.Collection;
import java.util.EventListener;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.bot.InputSimulator;
import org.powerbot.bot.ScriptController;
import org.powerbot.bot.ScriptEventDispatcher;

/**
 * A context class which interlinks all core classes for a {@link org.powerbot.script.Bot}.
 *
 * @param <C> the bot client
 */
public abstract class ClientContext<C extends Client> {
	private static final RuntimePermission CLIENT_PERMISSION = new RuntimePermission("checkGameClientAccess");
	private final AtomicReference<Bot<? extends ClientContext<C>>> bot;
	private final AtomicReference<C> client;

	/**
	 * The script controller.
	 */
	public final Script.Controller controller;
	/**
	 * A table of key/value pairs representing environmental properties.
	 */
	public final Properties properties;
	/**
	 * A collection representing the event listeners attached to the {@link org.powerbot.script.Bot}.
	 */
	public final Collection<EventListener> dispatcher;
	/**
	 * The input simulator for sending keyboard and mouse events.
	 */
	public final Input input;
	@Deprecated
	public final Input keyboard;
	@Deprecated
	public final Mouse mouse;

	/**
	 * Creates a new context with the given {@link org.powerbot.script.Bot}.
	 *
	 * @param bot the bot
	 */
	@SuppressWarnings("deprecation")
	protected ClientContext(final Bot<? extends ClientContext<C>> bot) {
		this.bot = new AtomicReference<Bot<? extends ClientContext<C>>>(bot);
		client = new AtomicReference<C>(null);
		@SuppressWarnings("unchecked")
		final ScriptController c = new ScriptController(this);
		controller = c;
		properties = new Properties();
		dispatcher = new ScriptEventDispatcher<C, EventListener>(this);
		input = new InputSimulator(bot);
		keyboard = input;
		@SuppressWarnings("unchecked")
		final Mouse mouse = new Mouse(this);
		this.mouse = mouse;
	}

	/**
	 * Creates a chained context.
	 *
	 * @param ctx the parent context
	 */
	@SuppressWarnings("deprecation")
	protected ClientContext(final ClientContext<C> ctx) {
		bot = ctx.bot;
		client = ctx.client;
		controller = ctx.controller;
		properties = ctx.properties;
		dispatcher = ctx.dispatcher;
		input = ctx.input;
		keyboard = input;
		mouse = ctx.mouse;
	}

	/**
	 * Returns the client version.
	 *
	 * @return the client version, which is {@code 6} for {@code rt6} and {@code} 4 for {@code rt4}
	 */
	public final String rtv() {
		final Class<?> c = getClass();
		if (org.powerbot.script.rt6.ClientContext.class.isAssignableFrom(c)) {
			return "6";
		}
		if (org.powerbot.script.rt4.ClientContext.class.isAssignableFrom(c)) {
			return "4";
		}
		return "";
	}

	/**
	 * Returns the bot.
	 *
	 * @return the bot
	 */
	public final Bot<? extends ClientContext<C>> bot() {
		return bot.get();
	}

	/**
	 * Returns the client.
	 *
	 * @return the client.
	 */
	public final C client() {
		System.getSecurityManager().checkPermission(CLIENT_PERMISSION);
		return client.get();
	}

	/**
	 * Sets the client.
	 *
	 * @param c the new client
	 * @return the previous value, which may be {@code null}
	 */
	public final C client(final C c) {
		return client.getAndSet(c);
	}

	/**
	 * Returns the script controller.
	 *
	 * @return the script controller
	 * @deprecated use {@link #controller}
	 */
	@Deprecated
	public final Script.Controller controller() {
		return controller;
	}

	/**
	 * Returns the primary script.
	 *
	 * @param <T> the type of script
	 * @return the primary script, or {@code null} if one is not attached
	 * @deprecated use {@link org.powerbot.script.Script.Controller#script()}
	 */
	@SuppressWarnings("unchecked")
	public final <T extends AbstractScript<? extends ClientContext<C>>> T script() {
		return (T) controller.script();
	}

	/**
	 * Returns the property value for the specified key, or an empty string as the default value.
	 *
	 * @param k the key to lookup
	 * @return the value for the specified key, otherwise an empty string if the requested entry does not exist
	 * @see #properties
	 */
	@Deprecated
	public final String property(final String k) {
		return property(k, "");
	}

	/**
	 * Returns the property value for the specified key, or a default value.
	 *
	 * @param k the key to lookup
	 * @param d the default value
	 * @return the value for the specified key, otherwise the default value if the requested entry does not exist
	 * @see #properties
	 */
	@Deprecated
	public final String property(final String k, final String d) {
		return properties.getProperty(k, d);
	}
}
