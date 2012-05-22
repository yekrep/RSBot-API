package org.powerbot.util.google.analytics;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;

/**
 * @author Paris
 */
public final class Config {
	public final String code, encoding, screenResolution, colorDepth, userLanguage, flashVersion;

	public Config(final String code) {
		this.code = code;

		encoding = System.getProperty("file.encoding");

		String region = System.getProperty("user.region");
		if (region == null) {
			region = System.getProperty("user.country");
		}
		userLanguage = System.getProperty("user.language") + "-" + region;

		String colorDepth = null, screenResolution = null;

		try {
			final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			int screenWidth = 0, screenHeight = 0;
			final String sep = ", ";
			final StringBuilder sb = new StringBuilder();
			for (final GraphicsDevice gs : ge.getScreenDevices()) {
				final DisplayMode dm = gs.getDisplayMode();
				screenWidth += dm.getWidth();
				screenHeight += dm.getHeight();
				sb.append(dm.getBitDepth());
				sb.append(sep);
			}
			sb.setLength(sb.length() - sep.length());
			colorDepth = sb.toString();
			if (screenHeight != 0 && screenWidth != 0) {
				screenResolution = String.format("%sx%s", screenWidth, screenHeight);
			}
		} catch (final HeadlessException ignored) {
			screenResolution = "1024x768";
			colorDepth = "32";
		}

		this.colorDepth = colorDepth;
		this.screenResolution = screenResolution;
		flashVersion = null;
	}
}
