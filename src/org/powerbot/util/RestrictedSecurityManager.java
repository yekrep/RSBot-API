package org.powerbot.util;

import java.io.File;
import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.powerbot.game.GameDefinition;
import org.powerbot.gui.BotChrome;
import org.powerbot.util.Configuration.OperatingSystem;
import org.powerbot.util.io.Resources;
import org.powerbot.util.io.SecureStore;

/**
 * @author Paris
 */
public class RestrictedSecurityManager extends SecurityManager {
	private static final Logger log = Logger.getLogger("Security");
	public static final String DNS1 = "8.8.8.8", DNS2 = "8.8.4.4";

	private String getCallingClass() {
		for (final Class<?> clazz : getClassContext()) {
			final String name = clazz.getName();
			if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("com.sun.") || name.startsWith("sun.")) {
				continue;
			}
			if (!name.equals(RestrictedSecurityManager.class.getName())) {
				return name;
			}
		}
		return null;
	}

	@Override
	public void checkAccept(final String host, final int port) {
		if (port == 53 && (host.equals(DNS1) || host.equals(DNS2))) {
			return;
		}
		throw new SecurityException();
	}

	@Override
	public void checkAccess(final Thread t) {
		checkAccess(t.getThreadGroup());
	}

	@Override
	public void checkAccess(final ThreadGroup g) {
	}

	@Override
	public void checkDelete(final String file) {
		checkFilePath(file, false);
		super.checkDelete(file);
	}

	@Override
	public void checkExec(final String cmd) {
		throw new SecurityException();
	}

	@Override
	public void checkExit(final int status) {
		final String calling = getCallingClass();
		if (calling.equals(BotChrome.class.getName())) {
			super.checkExit(status);
		} else {
			throw new SecurityException();
		}
	}

	@Override
	public void checkListen(final int port) {
		if (port != 0) {
			throw new SecurityException();
		}
	}

	@Override
	public void checkMulticast(final InetAddress maddr) {
		throw new SecurityException();
	}

	@Override
	public void checkMulticast(final InetAddress maddr, final byte ttl) {
		throw new SecurityException();
	}

	@Override
	public void checkPermission(final Permission perm) {
		if (perm instanceof RuntimePermission) {
			if (perm.getName().equals("setSecurityManager")) {
				throw new SecurityException();
			}
		}
	}

	@Override
	public void checkPermission(final Permission perm, final Object context) {
		checkPermission(perm);
	}

	@Override
	public void checkPrintJobAccess() {
		throw new SecurityException();
	}

	@Override
	public void checkRead(final String file) {
		checkFilePath(file, true);
		super.checkRead(file);
	}

	@Override
	public void checkRead(final String file, final Object context) {
		checkRead(file);
	}

	@Override
	public void checkSystemClipboardAccess() {
		throw new SecurityException();
	}

	@Override
	public boolean checkTopLevelWindow(final Object window) {
		return super.checkTopLevelWindow(window);
	}

	@Override
	public void checkWrite(final FileDescriptor fd) {
		super.checkWrite(fd);
	}

	@Override
	public void checkWrite(final String file) {
		checkFilePath(file, false);
		super.checkWrite(file);
	}

	private void checkFilePath(final String pathRaw, final boolean readOnly) {
		final String path = StringUtil.urlDecode(new File(pathRaw).getAbsolutePath());
		final String calling = getCallingClass();
		final String sysroot = System.getenv("SystemRoot"), home = System.getenv("HOME"), jre = System.getProperty("java.home"), tmp = System.getProperty("java.io.tmpdir");

		// allow read access to running jar
		if (Configuration.FROMJAR && path.equals(new File(RestrictedSecurityManager.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath()) && readOnly) {
			return;
		}

		// allow access for privileged thread groups
		if (Thread.currentThread().getThreadGroup().getName().startsWith(GameDefinition.THREADGROUPNAMEPREFIX)) {
			return;
		}

		// allow read access to temporary directory
		if (tmp != null && !tmp.isEmpty() && (path + File.separator).startsWith(tmp)) {
			return;
		}

		// allow access to secure store file for that specific class
		if (path.equals(new File(Configuration.STORE).getAbsolutePath()) && calling.equals(SecureStore.class.getName())) {
			return;
		}

		// allow read access to anything inside the JRE directory
		if (jre != null && !jre.isEmpty() && path.startsWith(jre) && readOnly) {
			return;
		}

		// allow read access to local resources
		if (!Configuration.FROMJAR && path.startsWith(new File(Resources.Paths.VERSION).getParentFile().getAbsolutePath()) && readOnly) {
			return;
		}

		// allow read access to local classes
		if (!Configuration.FROMJAR && (path.endsWith(".class") || (path + File.separator).startsWith(new File("bin").getAbsolutePath()) ||
				(path + File.separator).startsWith(new File("out").getAbsolutePath()))) {
			return;
		}

		// allow read access to hosts resolve file
		if (path.equals("/etc/resolv.conf") && readOnly) {
			return;
		}

		// allow read access to Windows system root directory
		if (sysroot != null && !sysroot.isEmpty() && path.startsWith(sysroot) && readOnly) {
			return;
		}

		// allow read access to any font directory
		final List<String> fonts = new ArrayList<String>(3);
		switch (Configuration.OS) {
		case WINDOWS:
			if (sysroot != null && !sysroot.isEmpty()) {
				fonts.add(sysroot + "\\Fonts");
			}
			break;
		case MAC:
			fonts.add("/Library/Fonts");
			fonts.add("/System/Library/Fonts");
			if (home != null && !home.isEmpty()) {
				fonts.add(home + "/Library/Fonts");
			}
			break;
		case LINUX:
			fonts.add("/usr/share/fonts/");
			fonts.add("/usr/local/share/fonts");
			if (home != null && !home.isEmpty()) {
				fonts.add(home + "/Library/Fonts");
			}
			break;
		}
		for (final String font : fonts) {
			if (path.startsWith(font)) {
				if (readOnly) {
					return;
				}
			}
		}

		// allow reading of font .ttf files for Windows XP (odd quirk)
		if (Configuration.OS == OperatingSystem.WINDOWS && path.endsWith(".ttf") && readOnly) {
			return;
		}

		log.severe((readOnly ? "Read" : "Write") + " denied: " + path + " (" + calling + ") on " + Thread.currentThread().getName() + "/" + Thread.currentThread().getThreadGroup().getName());
		throw new SecurityException();
	}
}
