package org.powerbot.util;

import java.io.File;
import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;
import java.util.logging.Logger;

import org.powerbot.game.GameDefinition;
import org.powerbot.gui.BotChrome;
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
		final String calling = getCallingClass();
		if (calling.startsWith(BotChrome.class.getName())) {
			super.checkExec(cmd);
		} else {
			throw new SecurityException();
		}
	}

	@Override
	public void checkExit(final int status) {
		final String calling = getCallingClass();
		if (calling.startsWith(BotChrome.class.getName())) {
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
	public void checkSetFactory() {
		if (getCallingClass() != null) {
			throw new SecurityException();
		}
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
		final String tmp = System.getProperty("java.io.tmpdir");

		// allow access to secure store file for that specific class
		if (path.equals(new File(Configuration.STORE).getAbsolutePath()) && calling.equals(SecureStore.class.getName())) {
			return;
		}

		// allow access for privileged thread groups
		if (Thread.currentThread().getThreadGroup().getName().startsWith(GameDefinition.THREADGROUPNAMEPREFIX)) {
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

		log.severe((readOnly ? "Read" : "Write") + " denied: " + path + " (" + calling + ") on " + Thread.currentThread().getName() + "/" + Thread.currentThread().getThreadGroup().getName());
		throw new SecurityException();
	}
}
