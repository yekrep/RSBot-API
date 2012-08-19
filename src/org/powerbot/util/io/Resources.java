package org.powerbot.util.io;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.powerbot.util.Configuration;

/**
 * @author Paris
 */
public class Resources {
	public static class Paths {
		public static final String ROOT = "resources";
		public static final String LICENSE = "license.txt";
		public static final String SERVER = ROOT + "/server.ini";
		public static final String ROOT_IMG = ROOT + "/images";
		public static final String ICON = ROOT_IMG + "/icon.png";
		public static final String ICON_SMALL = ROOT_IMG + "/icon_small.png";
		public static final String INFO = ROOT_IMG + "/glyphicons_195_circle_info.png";
		public static final String ADD = ROOT_IMG + "/glyphicons_190_circle_plus.png";
		public static final String ADD_WHITE = ROOT_IMG + "/glyphicons_190_circle_plus_white.png";
		public static final String ARROWS = ROOT_IMG + "/arrows.png";
		public static final String REFRESH = ROOT_IMG + "/glyphicons_081_refresh.png";
		public static final String PAUSE = ROOT_IMG + "/control_pause.png";
		public static final String PLAY = ROOT_IMG + "/control_play.png";
		public static final String STOP = ROOT_IMG + "/control_stop.png";
		public static final String CROSS_SMALL = ROOT_IMG + "/cross_small.png";
		public static final String CROSS_SMALL_GRAY = ROOT_IMG + "/cross_small_gray.png";
		public static final String REMOVE = ROOT_IMG + "/glyphicons_197_remove.png";
		public static final String KEYS = ROOT_IMG + "/glyphicons_044_keys.png";
		public static final String KEYBOARD = ROOT_IMG + "/glyphicons_269_keyboard_wired.png";
		public static final String KEYBOARD_WHITE = ROOT_IMG + "/glyphicons_269_keyboard_wired_white.png";
		public static final String ADDRESS = ROOT_IMG + "/glyphicons_088_adress_book.png";
		public static final String FILE = ROOT_IMG + "/glyphicons_036_file.png";
		public static final String SEARCH = ROOT_IMG + "/glyphicons_027_search.png";
		public static final String PENCIL = ROOT_IMG + "/glyphicons_030_pencil.png";
		public static final String SETTINGS = ROOT_IMG + "/glyphicons_280_settings.png";
		public static final String SETTINGS_WHITE = ROOT_IMG + "/glyphicons_280_settings_white.png";
		public static final String TWITTER = ROOT_IMG + "/twitter.png";
		public static final String FACEBOOK = ROOT_IMG + "/facebook.png";
		public static final String YOUTUBE = ROOT_IMG + "/youtube.png";
		public static final String SKILLS = ROOT_IMG + "/skills.png";
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
