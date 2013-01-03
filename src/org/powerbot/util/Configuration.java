package org.powerbot.util;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.zip.Adler32;

import org.powerbot.ipc.Controller;
import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public class Configuration {
	public static final String NAME = "RSBot";
	public static final boolean FROMJAR;
	public static boolean DEVMODE = false;
	public static final boolean SUPERDEV;
	public static final int VERSION = 4046;
	public static volatile int VERSION_LATEST = -1;
	public static final OperatingSystem OS;

	public enum OperatingSystem {
		MAC, WINDOWS, LINUX, UNKNOWN
	}

	public interface URLs {
		static final boolean TESTING = false;

		public static final String DOMAIN = "powerbot.org";
		static final String DOMAIN_SITE_LIVE = "www." + DOMAIN;
		static final String DOMAIN_SITE_TESTING = DOMAIN + ".localdomain";
		static final String DOMAIN_SITE = TESTING ? DOMAIN_SITE_TESTING : DOMAIN_SITE_LIVE;
		static final String PROTOCOL = TESTING ? "http://" : "https://";
		static final String DOMAIN_LINKS = "links." + DOMAIN;

		public static final String DOWNLOAD = "http://" + DOMAIN_SITE + "/rsbot/releases/RSBot-%s.jar";
		public static final String VERSION = "http://" + DOMAIN_LINKS + "/version.txt";

		public static final String CLIENTPATCH = "https://" + DOMAIN_SITE + "/rsbot/ts/%s.ts";
		public static final String CLIENTBUCKET = "http://buckets." + DOMAIN + "/process/?hash=%s";
		public static final String SCRIPTSAUTH = PROTOCOL + DOMAIN_SITE + "/scripts/api/auth/?{POST}a=%s&id=%s&n=%s";
		public static final String SCRIPTSCOLLECTION = PROTOCOL + DOMAIN_SITE + "/scripts/api/collection/?{POST}a=%s";
		public static final String SCRIPTSDOWNLOAD = PROTOCOL + DOMAIN_SITE + "/scripts/api/collection/download/?{POST}a=%s&s=%s&t=%s";
		public static final String SIGNIN = PROTOCOL + DOMAIN_SITE + "/api/login/?{POST}u=%s&p=%s&a=%s";
		public static final String LINKFILTER = "http://" + DOMAIN_SITE + "/api/safelink/?u=%s";

		public static final String SITE = "http://" + DOMAIN_SITE + "/";
		public static final String REGISTER = "http://" + DOMAIN_LINKS + "/register";
		public static final String LOSTPASS = "http://" + DOMAIN_LINKS + "/lostpass";
		public static final String SCRIPTSLIST = "http://" + DOMAIN_LINKS + "/scripts";

		public static final String TWITTER = "http://" + DOMAIN_LINKS + "/twitter";
		public static final String FACEBOOK = "http://" + DOMAIN_LINKS + "/facebook";
		public static final String YOUTUBE = "http://" + DOMAIN_LINKS + "/youtube";

		public static final String ADS = "http://" + DOMAIN_LINKS + "/ads";

		public static final String GAME = "runescape.com";
	}

	static {
		FROMJAR = Configuration.class.getClassLoader().getResource(Resources.Paths.ICON) != null;

		long hash = 0;
		try {
			final File control = new File("lib", "control.txt");
			hash = control.isFile() ? IOHelper.crc32(control) : -1;
		} catch (final IOException ignored) {
		}
		SUPERDEV = !Configuration.FROMJAR && hash == 3286621395L;

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
	}

	public static boolean isServerOS() {
		// Windows Server
		if (System.getProperty("os.name").indexOf("erver") != -1) {
			return true;
		} else if (OS == OperatingSystem.LINUX) {
			File f;
			String s;

			// OpenVZ
			f = new File("/etc/sysconfig/network");
			if (f.exists() && f.canRead()) {
				s = IOHelper.readString(f);
				if (s != null && s.indexOf("venet") != -1) {
					return true;
				}
			}

			// Linode
			try {
				s = IOHelper.readString(Runtime.getRuntime().exec("/bin/sh uname -r").getInputStream());
				if (s != null && s.indexOf("linode") != -1) {
					return true;
				}
			} catch (final IOException ignored) {
			}

			// Xen
			try {
				s = IOHelper.readString(Runtime.getRuntime().exec("/bin/sh dmesg | egrep -i 'xen|front'| wc -l").getInputStream());
				if (s != null && !s.isEmpty() && !s.trim().equals("0")) {
					return true;
				}
			} catch (final IOException ignored) {
			}
		}

		return false;
	}

	public static long getUID() {
		final Adler32 c = new Adler32();
		c.update(StringUtil.getBytesUtf8(Configuration.NAME));
		try {
			final Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				final byte[] a = e.nextElement().getHardwareAddress();
				if (a != null) {
					c.update(a);
				}
			}
		} catch (final SocketException ignored) {
		}
		return c.getValue();
	}

	public static long getUIDInstance() {
		return getUID() + Controller.getInstance().instanceID;
	}
}
