package org.powerbot.util;

import java.net.MalformedURLException;

import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.Resources;

public class Configuration {
	public static final String NAME = "RSBot";
	public static final boolean FROMJAR;
	public static final int VERSION;

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
		int v = 0;
		try {
			v = Integer.parseInt(IOHelper.readString(Resources.getResourceURL(Resources.Paths.VERSION)).trim());
		} catch (final MalformedURLException e) {
		}
		VERSION = v;
	}
}
