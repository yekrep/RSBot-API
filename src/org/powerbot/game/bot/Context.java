package org.powerbot.game.bot;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.util.internal.Constants;
import org.powerbot.game.api.util.internal.Multipliers;
import org.powerbot.game.client.Client;
import org.powerbot.game.loader.applet.Rs2Applet;

public class Context {
	private static Logger log = Logger.getLogger(Context.class.getName());
	protected static final Map<ThreadGroup, Context> context = new HashMap<ThreadGroup, Context>();

	private final Bot bot;

	public Context(final Bot bot) {
		this.bot = bot;
	}

	public static Context get() {
		final Context context = Context.context.get(Thread.currentThread().getThreadGroup());
		if (context == null) {
			final RuntimeException exception = new RuntimeException(Thread.currentThread() + "@" + Thread.currentThread().getThreadGroup());
			log.log(Level.SEVERE, "Client does not exist: ", exception);
			throw exception;
		}
		return context;
	}

	public static Bot resolve() {
		return get().bot;
	}

	public static Client client() {
		return get().getClient();
	}

	public static Multipliers multipliers() {
		return get().bot.multipliers;
	}

	public static Constants constants() {
		return get().bot.constants;
	}

	public Bot getBot() {
		return bot;
	}

	public Client getClient() {
		return bot.getClient();
	}

	public BufferedImage getImage() {
		return bot.getImage();
	}

	public Rs2Applet getApplet() {
		return bot.appletContainer;
	}

	public Calculations.Toolkit getToolkit() {
		return bot.toolkit;
	}

	public Calculations.Viewport getViewport() {
		return bot.viewport;
	}

	public void associate(final ThreadGroup threadGroup) {
		if (!EventQueue.isDispatchThread() && Context.context.containsKey(threadGroup)) {
			throw new RuntimeException("overlapping thread groups!");
		}
		Context.context.put(threadGroup, this);
	}

	public void disregard(final ThreadGroup threadGroup) {
		Context.context.remove(threadGroup);
	}
}
