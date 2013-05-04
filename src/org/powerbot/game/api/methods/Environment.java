package org.powerbot.game.api.methods;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Properties;

import org.powerbot.bot.Bot;
import org.powerbot.game.bot.Context;

public class Environment {
	private final static Properties props = new Properties();

	public static Properties getProperties() {
		return props;
	}

	public static String getDisplayName() {
		return Bot.context().getDisplayName();
	}

	public static int getUserId() {
		return Bot.context().getUserId();
	}

	public static BufferedImage captureScreen() {
		return Context.captureScreen();
	}

	public static BufferedImage getScreenBuffer() {
		return Context.getScreenBuffer();
	}

	public static void saveScreenCapture() {
		Context.saveScreenCapture();
	}

	public static void saveScreenCapture(final File path) {
		Context.saveScreenCapture(path);
	}

	@Deprecated
	public static void enableRandom(final Class<?> random, final boolean enable) {
	}

	@Deprecated
	public static boolean isRandomEnabled(final Class<?> random) {
		return true;
	}
}
