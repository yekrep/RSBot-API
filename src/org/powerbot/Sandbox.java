package org.powerbot;

import java.awt.AWTPermission;
import java.awt.Desktop;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.security.Permission;
import java.util.logging.Logger;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import org.powerbot.bot.InputSimulator;
import org.powerbot.bot.ScriptClassLoader;
import org.powerbot.bot.ScriptController;
import org.powerbot.bot.ScriptEventDispatcher;
import org.powerbot.bot.rt6.HashTable;
import org.powerbot.bot.rt6.Login;
import org.powerbot.misc.GameAccounts;
import org.powerbot.misc.GoogleAnalytics;
import org.powerbot.util.StringUtils;

class Sandbox extends SecurityManager {
	private static final Logger log = Logger.getLogger("Sandbox");

	@Override
	public void checkCreateClassLoader() {
		if (isScriptThread() && !isCallingClass(javax.swing.UIDefaults.class, java.io.ObjectOutputStream.class, java.io.ObjectInputStream.class,
				java.lang.reflect.Proxy.class, InputSimulator.class, GoogleAnalytics.class, HttpURLConnection.class, DocumentBuilderFactoryImpl.class,
				ScriptController.class, Login.class, org.powerbot.bot.rt4.Login.class,
				org.powerbot.bot.rt6.HashTable.class, org.powerbot.bot.rt4.HashTable.class)) {
			log.severe("Creating class loader denied");
			throw new SecurityException();
		}
		super.checkCreateClassLoader();
	}

	@Override
	public void checkExec(final String cmd) {
		if (isScriptThread()) {
			throw new SecurityException();
		}
		super.checkExec(cmd);
	}

	@Override
	public void checkExit(final int status) {
		if (isScriptThread()) {
			throw new SecurityException();
		}
		super.checkExit(status);
	}

	@Override
	public void checkMulticast(final InetAddress maddr) {
		throw new SecurityException();
	}

	@Override
	@SuppressWarnings("deprecation")
	public void checkMulticast(final InetAddress maddr, final byte ttl) {
		throw new SecurityException();
	}

	@Override
	public void checkPermission(final Permission perm) {
		final String name = perm.getName();

		if (perm instanceof RuntimePermission) {
			if (name.equals("checkInternalApiAccess")) {
				if (!isScriptThread()) {
					return;
				}
				final Class<?>[] a = getClassContext();
				if (a[1].getClassLoader() != a[2].getClassLoader()) {
					throw new SecurityException(name);
				}
				return;
			}

			if (name.equals("setSecurityManager") || (name.equals("setContextClassLoader") && isScriptThread()
					&& !isCallingClass(ScriptController.ScriptThreadFactory.class, ScriptEventDispatcher.class))) {
				throw new SecurityException(name);
			}
		} else if (perm instanceof AWTPermission) {
			if (name.equals("showWindowWithoutWarningBanner") && isScriptThread()) {
				throw new SecurityException();
			}
		} else if (perm instanceof FilePermission) {
			final FilePermission fp = (FilePermission) perm;
			final String a = fp.getActions();
			if (isCallingClass(Desktop.class) || (a.equals("execute") && isCallingClass(Boot.class))) {
				return;
			}
			if (isCallingClass(GameAccounts.class)) {
				return;
			}
			if (isScriptThread()) {
				checkFilePath(fp.getName(), a.equalsIgnoreCase("read") || a.equalsIgnoreCase("readlink"));
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
	public void checkSetFactory() {
		if (isScriptThread()) {
			throw new SecurityException();
		}
		super.checkSetFactory();
	}

	@Override
	public void checkSystemClipboardAccess() {
		if (isScriptThread() && !isCallingClass(java.awt.event.InputEvent.class)) {
			throw new SecurityException();
		}
	}

	private void checkFilePath(final String pathRaw, final boolean readOnly) {
		if (Configuration.OS == Configuration.OperatingSystem.WINDOWS) {
			final Class<?>[] ctx = getClassContext();
			int n = 2;
			for (int i = n; i < ctx.length; i++) {
				final String a = ctx[i].getName();
				if (a.equals("java.io.Win32FileSystem")) {
					n = i;
					break;
				}
			}
			if (++n < ctx.length && ctx[n].getName().equals(File.class.getName())) {
				return;
			}
		}

		final String path = getCanonicalPath(new File(StringUtils.urlDecode(pathRaw))), tmp = getCanonicalPath(Configuration.TEMP);

		// allow write access to temp directory
		if (readOnly || (path + File.separator).startsWith(tmp)) {
			return;
		}

		throw new SecurityException("write: " + path);
	}

	private static String getCanonicalPath(final File f) {
		try {
			return f.getCanonicalPath();
		} catch (final IOException ignored) {
			return f.getAbsolutePath();
		}
	}

	private boolean isCallingClass(final Class<?>... classes) {
		for (final Class<?> context : getClassContext()) {
			for (final Class<?> clazz : classes) {
				if (clazz.equals(context)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isScriptThread() {
		return Thread.currentThread().getContextClassLoader() instanceof ScriptClassLoader;
	}
}
