package org.powerbot.game.bot;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.powerbot.core.Bot;
import org.powerbot.core.bot.RSLoader;
import org.powerbot.core.bot.handlers.ScriptHandler;
import org.powerbot.core.event.EventManager;
import org.powerbot.core.script.internal.Constants;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Environment;
import org.powerbot.game.bot.handler.input.MouseExecutor;
import org.powerbot.game.bot.util.ScreenCapture;
import org.powerbot.game.client.Client;

public class Context {//TODO remove idea of a 'context'
	public static final Map<ThreadGroup, Context> context = new HashMap<ThreadGroup, Context>();

	private final Bot bot;
	public int world = -1;

	public Context(final Bot bot) {
		this.bot = bot;
	}

	@Deprecated
	public static Context get() {
		return Bot.context();
	}

	public static Bot resolve() {
		return get().bot;
	}

	@Deprecated
	public static Client client() {
		return Bot.client();
	}

	@Deprecated
	public static Constants constants() {
		return Bot.constants();
	}

	public static BufferedImage captureScreen() {
		return ScreenCapture.capture(Context.get());
	}

	public static BufferedImage getScreenBuffer() {
		return ScreenCapture.getScreenBuffer(Context.get());
	}

	public static void saveScreenCapture() {
		ScreenCapture.save(Context.get());
	}

	public static void setLoginWorld(final int world) {
		get().world = world;
	}

	public static void cancelMouse() {
		get().bot.getMouseExecutor().cancel();
	}

	public static void saveScreenCapture(final String fileName) {
		ScreenCapture.save(Context.get(), fileName);
	}

	public Bot getBot() {
		return bot;
	}

	public ScriptHandler getScriptHandler() {
		return bot.getScriptHandler();
	}

	@Deprecated
	public Client getClient() {
		return Bot.client();
	}

	public MouseExecutor getExecutor() {
		return bot.getMouseExecutor();
	}

	public BufferedImage getImage() {
		return bot.getImage();
	}

	public BufferedImage getBuffer() {
		return bot.getBuffer();
	}

	public EventManager getEventManager() {
		return bot.getEventManager();
	}

	public ThreadGroup getThreadGroup() {
		return bot.threadGroup;
	}

	public RSLoader getLoader() {
		return bot.appletContainer;
	}

	public String getDisplayName() {
		return Environment.getProperties().getProperty("user.name");
	}

	public int getUserId() {
		final String s = Environment.getProperties().getProperty("user.id");
		return s == null || s.isEmpty() ? -1 : Integer.parseInt(s);
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
