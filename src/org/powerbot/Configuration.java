package org.powerbot;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.zip.Adler32;

import org.powerbot.util.StringUtils;

public class Configuration {
	public static final String NAME = "RSBot";
	public static final int VERSION = 7017;
	public static final OperatingSystem OS;
	public static final boolean JRE6;
	public static final File HOME, TEMP;

	public enum OperatingSystem {
		MAC, WINDOWS, LINUX, UNKNOWN
	}

	public interface URLs {
		String DOMAIN = "powerbot.org";
		String DOMAIN_SITE = "www." + DOMAIN;
		String DOMAIN_SITE_CDN = "powerbot-dequeue.netdna-ssl.com";
		String API_BASE = "http://" + DOMAIN_SITE + "/rsbot/api";

		String CONTROL = "http://" + DOMAIN_SITE_CDN + "/rsbot/control.ini";
		String ICON = "http://" + DOMAIN_SITE_CDN + "/assets/img/logos/icon_bot.png";
		String TSPEC = "http://" + DOMAIN_SITE_CDN + "/rsbot/spec-401/%s/%s";
		String TSPEC_PROCESS = "http://" + DOMAIN_SITE + "/rsbot/api/transform/process/?hash=%s";
		String SCRIPTS = API_BASE + "/scripts/{COOKIE}a=%s";
		String SCRIPTS_BROWSE = "http://" + DOMAIN_SITE + "/go/scripts";
		String LOGIN = API_BASE + "/login/{COOKIE}u=%s&p=%s&a=%s";
		String LOGIN_PIN = API_BASE + "/login/pin/";
		String SUPPORT = "http://" + DOMAIN_SITE + "/contact/";
		String LICENSE = "http://" + DOMAIN_SITE + "/terms/license/";

		String GAME = "runescape.com";
		String GAME_SERVICES_DOMAIN = "services." + GAME;
	}

	public static class UID {
		public static final long VALUE;

		static {
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

			VALUE = c.getValue();
		}
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
	}
}
