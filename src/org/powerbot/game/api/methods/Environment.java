package org.powerbot.game.api.methods;

import java.awt.image.BufferedImage;

import org.powerbot.game.api.util.ScreenCapture;
import org.powerbot.game.bot.Bot;
import org.powerbot.service.NetworkAccount;

public class Environment {
	public static String getDisplayName() {
		if (NetworkAccount.getInstance().isLoggedIn()) {
			return NetworkAccount.getInstance().getAccount().getDisplayName();
		}
		return null;
	}

	public static int getUserId() {
		if (NetworkAccount.getInstance().isLoggedIn()) {
			return NetworkAccount.getInstance().getAccount().getID();
		}
		return -1;
	}

	public static BufferedImage captureScreen() {
		return ScreenCapture.capture(Bot.resolve());
	}

	public static void saveScreenCapture() {
		ScreenCapture.save(Bot.resolve());
	}

	public static void saveScreenCapture(final String fileName) {
		ScreenCapture.save(Bot.resolve(), fileName);
	}
}
