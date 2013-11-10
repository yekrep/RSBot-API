package org.powerbot.util;

import java.awt.Desktop;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.InetAddress;
import java.security.Permission;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.powerbot.Configuration;
import org.powerbot.service.GameAccounts;
import org.powerbot.service.NetworkAccount;
import org.powerbot.service.scripts.ScriptClassLoader;
import org.powerbot.util.io.CryptFile;

/**
 * @author Paris
 */
public class Sandbox extends SecurityManager {
	private static final Logger log = Logger.getLogger("Sandbox");

	@Override
	public void checkAccept(final String host, final int port) {
		throw new SecurityException();
	}

	@Override
	public void checkConnect(final String host, final int port) {
		checkConnect(host, port, null);
	}

	@Override
	public void checkConnect(final String host, final int port, final Object context) {
		if (isGameThread()) {
			return;
		}
		if (context == null) {
			super.checkConnect(host, port);
		} else {
			super.checkConnect(host, port, context);
		}
	}

	@Override
	public void checkMulticast(final InetAddress maddr) {
		throw new SecurityException();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void checkMulticast(final InetAddress maddr, final byte ttl) {
		throw new SecurityException();
	}

	@Override
	public void checkPermission(final Permission perm) {
		final String name = perm.getName();

		if (perm instanceof RuntimePermission) {
			if (name.equals("setSecurityManager")) {
				throw new SecurityException(name);
			} else if (isScriptThread() && (name.equals("modifyThreadGroup") || name.equals("createClassLoader") || name.equals("setFactory") ||
					name.startsWith("exitVM"))) {
				throw new SecurityException(name);
			}
		} else if (perm instanceof FilePermission) {
			final FilePermission fp = (FilePermission) perm;
			final String a = fp.getActions();
			if (a.equals("execute") && isScriptThread()) {
				throw new SecurityException(a + ": " + fp.getName());
			}
			if (isCallingClass(Desktop.class)) {
				return;
			}
			checkFilePath(fp.getName(), a.equalsIgnoreCase("read") || a.equalsIgnoreCase("readlink"));
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
	public void checkSystemClipboardAccess() {
		if (isCallingClass(java.awt.event.InputEvent.class)) {
			return;
		}
		throw new SecurityException();
	}

	private void checkFilePath(final String pathRaw, final boolean readOnly) {
		if (Configuration.OS == Configuration.OperatingSystem.WINDOWS) {
			final Class[] ctx = getClassContext();
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

		final String path = getCanonicalPath(new File(StringUtil.urlDecode(pathRaw))), tmp = getCanonicalPath(Configuration.TEMP);
		// permission controls for crypt files
		for (final Entry<File, Class<?>[]> entry : CryptFile.PERMISSIONS.entrySet()) {
			final Class<?>[] entries = new Class<?>[entry.getValue().length + 1];
			entries[0] = CryptFile.class;
			System.arraycopy(entry.getValue(), 0, entries, 1, entries.length - 1);
			final String pathDecoded = getCanonicalPath(new File(StringUtil.urlDecode(entry.getKey().getAbsolutePath())));
			if (pathDecoded.equals(path)) {
				if (!isCallingClass(entries)) {
					throw new SecurityException();
				}
			}
		}

		if ((path + File.separator).startsWith(Configuration.HOME.getAbsolutePath()) &&
				isCallingClass(NetworkAccount.class, GameAccounts.class, Tracker.class, CryptFile.class)) {
			return;
		}

		// allow access for privileged thread groups
		if (isGameThread()) {
			return;
		}

		// allow read permissions to all files
		if (readOnly) {
			return;
		}

		// allow write access to temp directory
		if ((path + File.separator).startsWith(tmp)) {
			return;
		}

		throw new SecurityException((readOnly ? "read" : "write") + ": " + path);
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
				if (clazz.isAssignableFrom(context)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isScriptThread() {
		final Class<?>[] context = getClassContext();
		for (int i = 1; i < context.length; i++) {
			final ClassLoader loader = context[i].getClassLoader();
			if (loader != null && loader.getClass().isAssignableFrom(ScriptClassLoader.class)) {
				return true;
			}
		}
		return false;
	}

	private boolean isGameThread() {
		return Thread.currentThread().getThreadGroup().getName().endsWith("-game");
	}

	public static boolean isScriptThread(final Thread t) {
		final ClassLoader loader = t.getContextClassLoader();
		return loader != null && loader.getClass().isAssignableFrom(ScriptClassLoader.class);
	}

	public static void assertNonScript() {
		final SecurityManager sm = System.getSecurityManager();
		if (sm == null || !(sm instanceof Sandbox) || ((Sandbox) sm).isScriptThread()) {
			throw new SecurityException();
		}
	}
}
