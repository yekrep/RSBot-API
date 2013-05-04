package org.powerbot.game.bot;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.powerbot.bot.Bot;
import org.powerbot.bot.RSLoader;
import org.powerbot.game.api.methods.Environment;
import org.powerbot.game.bot.util.ScreenCapture;

@Deprecated
public class Context {//TODO remove idea of a 'context'
	public static final Map<ThreadGroup, Context> context = new HashMap<>();
	private final Bot bot;
	public int world = -1;

	public Context(final Bot bot) {
		this.bot = bot;
	}

	public static BufferedImage captureScreen() {
		return ScreenCapture.capture(Bot.context());
	}

	public static BufferedImage getScreenBuffer() {
		return ScreenCapture.getScreenBuffer(Bot.context());
	}

	public static void saveScreenCapture() {
		ScreenCapture.save(Bot.context());
	}

	public static void setLoginWorld(final int world) {
		Bot.context().world = world;
	}

	public static void saveScreenCapture(final File path) {
		ScreenCapture.save(Bot.context(), path, "png");
	}

	public Bot getBot() {
		return bot;
	}

	public BufferedImage getImage() {
		return bot.getImage();
	}

	public BufferedImage getBuffer() {
		return bot.getBuffer();
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
