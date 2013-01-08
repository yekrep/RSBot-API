package org.powerbot.game.bot;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import org.powerbot.core.bot.Bot;
import org.powerbot.core.bot.handlers.ScriptHandler;
import org.powerbot.core.event.EventManager;
import org.powerbot.core.loader.applet.Rs2Applet;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.util.internal.Constants;
import org.powerbot.game.bot.handler.input.MouseExecutor;
import org.powerbot.game.bot.util.ScreenCapture;
import org.powerbot.game.client.Client;
import org.powerbot.service.NetworkAccount;

public class Context {//TODO remove idea of a 'context'
	public static final Map<ThreadGroup, Context> context = new HashMap<ThreadGroup, Context>();

	private final Bot bot;
	public int world = -1;

	public Context(final Bot bot) {
		this.bot = bot;
	}

	public static Context get() {
		return Bot.getInstance().getContext();
	}

	public static Bot resolve() {
		return get().bot;
	}

	public static Client client() {
		return get().getClient();
	}

	public static Constants constants() {
		return get().bot.composite.constants;
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

	@Deprecated
	/**
	 * @see org.powerbot.game.api.methods.input.Mouse#setSpeed(org.powerbot.game.api.methods.input.Mouse.Speed)
	 */
	public void b(final int s) {
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

	public Client getClient() {
		return bot.getClient();
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

	public ThreadPoolExecutor getContainer() {
		return bot.getExecutor();
	}

	public EventManager getEventManager() {
		return bot.getEventManager();
	}

	public ThreadGroup getThreadGroup() {
		return bot.threadGroup;
	}

	public Rs2Applet getApplet() {
		return bot.appletContainer;
	}

	public Calculations.Toolkit getToolkit() {
		return bot.composite.toolkit;
	}

	public Calculations.Viewport getViewport() {
		return bot.composite.viewport;
	}

	public String getDisplayName() {
		if (NetworkAccount.getInstance().isLoggedIn()) {
			return NetworkAccount.getInstance().getAccount().getDisplayName();
		}
		return null;
	}

	public int getUserId() {
		if (NetworkAccount.getInstance().isLoggedIn()) {
			return NetworkAccount.getInstance().getAccount().getID();
		}
		return -1;
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
