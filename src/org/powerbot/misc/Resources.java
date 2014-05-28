package org.powerbot.misc;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;

import org.powerbot.Configuration;

public class Resources {
	public static final class Paths {
		public static final String ROOT = "resources";
		public static final String ICON = ROOT + "/icon.png";
		public static final String ARROWS = ROOT + "/arrows.png";
	}

	public static URL getResourceURL(final String path) throws MalformedURLException {
		return Configuration.class.getResource("/" + path);
	}

	public static Image getImage(final String resource) {
		try {
			return Toolkit.getDefaultToolkit().getImage(getResourceURL(resource));
		} catch (final Exception ignored) {
		}
		return null;
	}
}
