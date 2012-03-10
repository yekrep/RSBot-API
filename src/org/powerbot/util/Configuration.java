package org.powerbot.util;

import java.io.File;
import java.net.MalformedURLException;

import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public class Configuration {
	public static final String NAME = "RSBot";
	public static final boolean FROMJAR;
	public static final int VERSION;
	public static final String STORE;

	public interface URLs {
		public static final String DOMAIN = "powerbot.org";
		public static final String BASE_LINKS = "http://links." + DOMAIN + "/";
		public static final String BASE = "https://www." + DOMAIN + "/";

		public static final String SITE = BASE_LINKS + "site";
		public static final String REGISTER = BASE_LINKS + "register";

		public static final String GAME = "runescape.com";
		public static final String CLIENT_PATCH = BASE_LINKS + "modscript/";
	}

	static {
		FROMJAR = Configuration.class.getClassLoader().getResource(Resources.Paths.VERSION) != null;
		int v = 0;
		try {
			v = Integer.parseInt(IOHelper.readString(Resources.getResourceURL(Resources.Paths.VERSION)).trim());
		} catch (final MalformedURLException ignored) {
		}
		VERSION = v;
		final String appdata = System.getenv("APPDATA"), home = System.getProperty("user.home");
		final String root = appdata != null && new File(appdata).isDirectory() ? appdata : home == null ? "~" : home;
		STORE = root + File.separator + NAME + ".db";
	}
}
