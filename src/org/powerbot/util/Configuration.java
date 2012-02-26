package org.powerbot.util;

import java.net.MalformedURLException;

import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.Resources;

public class Configuration {
	public static final String NAME = "RSBot";
	private static final int VERSION = 4000;
	public static final boolean FROMJAR;

	public interface Paths {
		public interface URLs {
			public static final String BASE = "http://links.powerbot.org/";
			public static final String SITE = BASE + "site";

			public static final String GAME = "runescape.com";
			public static final String CLIENT_PATCH = BASE + "/modscript/";
		}
	}

	static {
		FROMJAR = Configuration.class.getClassLoader().getResource(Resources.Paths.VERSION) != null;
	}

	public static int getVersion() {
		return VERSION;
	}

	public static String getVersionFormatted() {
		return StringUtil.formatVersion(getVersion());
	}
}
