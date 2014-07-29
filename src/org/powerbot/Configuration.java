package org.powerbot;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.zip.Adler32;

import org.powerbot.util.StringUtils;

public class Configuration {
	public static final String NAME = "RSBot";
	public static final int VERSION = 6040;

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

		public static final String VERSION = "http://" + DOMAIN_SITE_CDN + "/rsbot/version.txt";
		public static final String ICON = "http://" + DOMAIN_SITE_CDN + "/assets/img/logos/icon_bot.png";
		public static final String TSPEC = Configuration.PROTO + DOMAIN_SITE_CDN + "/rsbot/ts%s/%s.ts";
		public static final String TSPEC_BUCKETS = "http://buckets." + DOMAIN + "/process/?hash=%s";
		public static final String SCRIPTS = Configuration.PROTO + DOMAIN_SITE + "/scripts/api/collection/?a=%s";
		public static final String SCRIPTS_BROWSE = Configuration.PROTO + DOMAIN_SITE + "/go/scripts";
		public static final String LOGIN = Configuration.PROTO + DOMAIN_SITE + "/rsbot/login/?u=%s&p=%s&a=%s";
		public static final String LICENSE = "http://" + DOMAIN_SITE + "/terms/license/";
		public static final String ADS = "http://" + DOMAIN_SITE_CDN + "/rsbot/ads.txt";

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
		PROTO = OS == OperatingSystem.MAC && JRE6 ? "http://" : "https://";

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
