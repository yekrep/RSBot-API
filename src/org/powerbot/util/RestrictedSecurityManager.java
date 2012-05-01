package org.powerbot.util;

import java.io.File;
import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;
import java.util.logging.Logger;

import org.powerbot.game.GameDefinition;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.BotLicense;
import org.powerbot.service.scripts.ScriptClassLoader;
import org.powerbot.util.io.SecureStore;

/**
 * @author Paris
 */
public class RestrictedSecurityManager extends SecurityManager {
	private static final Logger log = Logger.getLogger("Security");
	public static final String DNS1 = "8.8.8.8", DNS2 = "8.8.4.4";

	private String getCallingClass() {
		final Class<?>[] context = getClassContext();
		for (int i = 1; i < context.length; i++) {
			final Class<?> clazz = context[i];
			final String name = clazz.getName();
			if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("com.sun.") || name.startsWith("sun.") ||
					name.equals(RestrictedSecurityManager.class.getName())) {
				continue;
			}
			return name;
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
		if (isScriptThread()) {
			log.severe("Thread access denied to: " + getCallingClass());
			throw new SecurityException();
		}
		super.checkAccess(t);
	}

	@Override
	public void checkAccess(final ThreadGroup g) {
		if (isScriptThread()) {
			log.severe("Thread access denied to: " + getCallingClass());
			throw new SecurityException();
		}
		super.checkAccess(g);
	}

	@Override
	public void checkConnect(final String host, final int port) {
		checkConnect(host, port, null);
	}

	@Override
	public void checkConnect(final String host, final int port, final Object context) {
		if (!(port == 80 || port == 443 || port == 53 || port == 43594 || port == -1)) {
			log.severe("Connection denied to port " + port);
			throw new SecurityException();
		}
		if (host.equals("localhost") || host.endsWith(".localdomain") || host.startsWith("127.") || host.startsWith("192.168.") || host.startsWith("10.") || host.endsWith("::1")) {
			log.severe("Connection denied to localhost");
			throw new SecurityException();
		}
		if (context == null) {
			super.checkConnect(host, port);
		} else {
			super.checkConnect(host, port, context);
		}
	}

	@Override
	public void checkCreateClassLoader() {
		if (isScriptThread()) {
			log.severe("Creating class loader denied to " + getCallingClass());
			throw new SecurityException();
		}
	}

	@Override
	public void checkDelete(final String file) {
		checkFilePath(file, false);
		super.checkDelete(file);
	}

	@Override
	public void checkExec(final String cmd) {
		if (isCallingClass(BotChrome.class, LoadUpdates.class)) {
			super.checkExec(cmd);
		} else {
			throw new SecurityException();
		}
	}

	@Override
	public void checkExit(final int status) {
		if (isCallingClass(BotChrome.class, LoadUpdates.class, BotLicense.class)) {
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

	private boolean isCallingClass(final Class<?>... classes) {
		final String calling = getCallingClass();
		for (final Class<?> clazz : classes) {
			final String name = clazz.getName();
			if (calling.equals(name) || calling.startsWith(name + "$")) {
				return true;
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
}
