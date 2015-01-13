package org.powerbot;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.zip.Adler32;

import org.powerbot.util.StringUtils;

public class Configuration {
	public static final String NAME = "RSBot";
	public static final int VERSION = 6052;

	public static final OperatingSystem OS;
	public static final boolean JRE6;
	public static final File HOME, TEMP;
	public static final long UID;

	public enum OperatingSystem {
		MAC, WINDOWS, LINUX, UNKNOWN
	}

	private static final String PROTO;

	public interface URLs {
		public static final String DOMAIN = "powerbot.org";
		static final String DOMAIN_SITE = "www." + DOMAIN;
		static final String DOMAIN_SITE_CDN = "powerbot-dequeue.netdna-ssl.com";
		static final String API_BASE = Configuration.PROTO + DOMAIN_SITE + "/rsbot/api";

		public static final String CONTROL = "http://" + DOMAIN_SITE_CDN + "/rsbot/control.ini";
		public static final String ICON = "http://" + DOMAIN_SITE_CDN + "/assets/img/logos/icon_bot.png";
		public static final String TSPEC = Configuration.PROTO + DOMAIN_SITE_CDN + "/rsbot/ts%s/%s.ts";
		public static final String TSPEC_BUCKETS = "http://buckets." + DOMAIN + "/process/?hash=%s";
		public static final String SCRIPTS = API_BASE + "/scripts/?a=%s";
		public static final String SCRIPTS_BROWSE = Configuration.PROTO + DOMAIN_SITE + "/go/scripts";
		public static final String LOGIN = API_BASE + "/login/?u=%s&p=%s&a=%s";
		public static final String LOGIN_PIN = API_BASE + "/login/pin/";
		public static final String LICENSE = "http://" + DOMAIN_SITE + "/terms/license/";

		public static final String GAME = "runescape.com";
		public static final String GAME_SERVICES_DOMAIN = "services." + GAME;
	}

	static {
		final String os = System.getProperty("os.name");
		if (os.contains("Mac")) {
			OS = OperatingSystem.MAC;
		} else if (os.contains("Windows")) {
			OS = OperatingSystem.WINDOWS;
		} else if (os.contains("Linux")) {
			OS = OperatingSystem.LINUX;
		} else {
			OS = OperatingSystem.UNKNOWN;
		}

		final String jre = System.getProperty("java.version");
		JRE6 = jre != null && jre.startsWith("1.6");
		PROTO = (OS == OperatingSystem.MAC && JRE6) || URLs.DOMAIN_SITE.endsWith(".localdomain") ? "http://" : "https://";

		if (OS == OperatingSystem.WINDOWS) {
			HOME = new File(System.getenv("APPDATA"), NAME);
		} else {
			final String user = System.getProperty("user.home");
			final File lib = new File(user, "/Library/");
			if (OS == OperatingSystem.MAC && lib.isDirectory()) {
				HOME = new File(lib, NAME);
			} else {
				HOME = new File(System.getProperty("user.home"), "." + NAME.toLowerCase());
			}
		}

		if (!HOME.isDirectory()) {
			//noinspection ResultOfMethodCallIgnored
			HOME.mkdirs();
		}

		TEMP = new File(System.getProperty("java.io.tmpdir"));

		final Adler32 c = new Adler32();
		c.update(StringUtils.getBytesUtf8(Configuration.NAME));

		Enumeration<NetworkInterface> e = null;
		try {
			e = NetworkInterface.getNetworkInterfaces();
		} catch (final SocketException ignored) {
		}

		if (e != null) {
			while (e.hasMoreElements()) {
				byte[] a = null;
				try {
					a = e.nextElement().getHardwareAddress();
				} catch (final SocketException ignored) {
				}
				if (a != null) {
					c.update(a);
				}
			}
		}

		UID = c.getValue();
	}
}
