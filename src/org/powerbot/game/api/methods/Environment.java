package org.powerbot.game.api.methods;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Properties;

import org.powerbot.game.bot.Context;

public class Environment {
	private final static Properties props = new Properties();

	public static Properties getProperties() {
		return props;
	}

	public static String getDisplayName() {
		return Context.get().getDisplayName();
	}

	public static int getUserId() {
		return Context.get().getUserId();
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

	public static void saveScreenCapture(final String name) {
		Context.saveScreenCapture(name);
	}

	public static File getStorageDirectory() {
		final File dir = new File(
				System.getProperty("java.io.tmpdir"),
				Context.get().getScriptHandler().getDefinition().getName().replace('.', File.pathSeparatorChar)
		);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		return dir;
	}

	@Deprecated
	public static void enableRandom(final Class<?> random, final boolean enable) {
	}

	@Deprecated
	public static boolean isRandomEnabled(final Class<?> random) {
		return true;
	}
}
