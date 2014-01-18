package org.powerbot.misc;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.powerbot.Configuration;

public class Resources {
	public static final class Paths {
		public static final String ROOT = "resources";
		public static final String ROOT_IMG = ROOT + "/images";
		public static final String ICON = ROOT_IMG + "/icon.png";
		public static final String ARROWS = ROOT_IMG + "/arrows.png";
		public static final String PAUSE = ROOT_IMG + "/glyphicons_174_pause.png";
		public static final String PLAY = ROOT_IMG + "/glyphicons_173_play.png";
		public static final String STOP = ROOT_IMG + "/glyphicons_175_stop.png";
	}

	public static URL getResourceURL(final String path) throws MalformedURLException {
		return Configuration.FROMJAR ? Configuration.class.getResource("/" + path) : new File(path).toURI().toURL();
	}

	public static Image getImage(final String resource) {
		try {
			return Toolkit.getDefaultToolkit().getImage(getResourceURL(resource));
		} catch (final Exception ignored) {
		}
		return null;
	}
}
