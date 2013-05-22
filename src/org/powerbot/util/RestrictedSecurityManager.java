package org.powerbot.util;

import java.io.File;
import java.net.InetAddress;
import java.security.Permission;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javafx.application.Platform;
import org.powerbot.bot.RSClassLoader;
import org.powerbot.ipc.Controller;
import org.powerbot.script.internal.LocalScriptClassLoader;
import org.powerbot.script.internal.ScriptClassLoader;
import org.powerbot.util.Configuration.OperatingSystem;
import org.powerbot.util.io.CryptFile;

/**
 * @author Paris
 */
public class RestrictedSecurityManager extends SecurityManager {
	private static final Logger log = Logger.getLogger("Security");
	public static final String DNS1 = "8.8.8.8", DNS2 = "8.8.4.4";
	private static final int PUBLIC_PORT_START = 54700, PUBLIC_PORT_END = 54800;
	private final Class<?>[] whitelist;

	public RestrictedSecurityManager(final Class<?>... whitelist) {
		this.whitelist = whitelist;
	}

	@Override
	public void checkAccept(final String host, final int port) {
		if (isCallingClass(Controller.class)) {
			return;
		}
		if (port >= PUBLIC_PORT_START || port <= PUBLIC_PORT_END) {
			return;
		}
		if (port == 53 && (host.equals(DNS1) || host.equals(DNS2))) {
			super.checkAccept(host, port);
			return;
		}
		throw new SecurityException();
	}

	@Override
	public void checkConnect(final String host, final int port) {
		checkConnect(host, port, null);
	}

	@Override
	public void checkConnect(final String host, final int port, final Object context) {
		if (!(port == 80 || port == 443 || port == 53 || port == 43594 || port == -1)) {
			if (!isCallingClass(Controller.class)) {
				log.severe("Connection denied to port " + port);
				throw new SecurityException();
			}
		}
		if (host.equals("localhost") || host.endsWith(".localdomain") || host.startsWith("127.") || host.startsWith("192.168.") || host.startsWith("10.") || host.endsWith("::1")) {
			if (!isCallingClass(Configuration.class, Controller.class) && !Configuration.SUPERDEV && !(port >= PUBLIC_PORT_START && port <= PUBLIC_PORT_END)) {
				log.severe("Connection denied to localhost");
				throw new SecurityException();
			}
		}
		if (context == null) {
			super.checkConnect(host, port);
		} else {
			super.checkConnect(host, port, context);
		}
	}

	@Override
	public void checkCreateClassLoader() {
		if (isScriptThread() && !isCallingClass(javax.swing.UIDefaults.class, java.io.ObjectOutputStream.class, java.io.ObjectInputStream.class,
				java.lang.reflect.Proxy.class)) {
			log.severe("Creating class loader denied");
			throw new SecurityException();
		}
		super.checkCreateClassLoader();
	}

	@Override
	public void checkDelete(final String file) {
		checkFilePath(file, false);
		super.checkDelete(file);
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
		super.checkRead(file, context);
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
		if (isCallingClass(java.awt.event.InputEvent.class)) {
			return;
		}
		if (isScriptThread()) {
			throw new SecurityException();
		}
	}

	@Override
	public void checkWrite(final String file) {
		checkFilePath(file, false);
		super.checkWrite(file);
	}

	private void checkFilePath(final String pathRaw, final boolean readOnly) {
		final String path = StringUtil.urlDecode(new File(pathRaw).getAbsolutePath());

		// permission controls for crypt files
		for (final Entry<File, Class<?>[]> entry : CryptFile.PERMISSIONS.entrySet()) {
			final Class<?>[] entries = new Class<?>[entry.getValue().length + 1];
			entries[0] = CryptFile.class;
			System.arraycopy(entry.getValue(), 0, entries, 1, entries.length - 1);
			final String pathDecoded = StringUtil.urlDecode(entry.getKey().getAbsolutePath());
			if (pathDecoded.equals(pathRaw)) {
				if (!isCallingClass(entries)) {
					throw new SecurityException();
				}
			}
		}

		if (isCallingClass(whitelist)) {
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

		// hack fix for odd occurrence on OS X
		if (Configuration.OS == OperatingSystem.MAC && path.startsWith("/var/folders/") && Thread.currentThread().getName().equals("main")) {
			return;
		}

		// allow home directory for secure file controller
		if ((path + File.separator).startsWith(Configuration.HOME.getAbsolutePath()) && isCallingClass(CryptFile.class)) {
			return;
		}

		// allow write access to temp directory
		if ((path + File.separator).startsWith(Configuration.TEMP.getAbsolutePath())) {
			return;
		}

		// TODO: don't assume entire FX thread should be whitelisted
		if (Platform.isFxApplicationThread()) {
			return;
		}

		if (!isGameThread()) {
			log.severe((readOnly ? "Read" : "Write") + " denied: " + path + " on " + Thread.currentThread().getName() + "/" + Thread.currentThread().getThreadGroup().getName());
		}
		throw new SecurityException();
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
			if (loader != null && (loader.getClass().isAssignableFrom(ScriptClassLoader.class) || loader.getClass().isAssignableFrom(LocalScriptClassLoader.class))) {
				return true;
			}
		}
		return false;
	}

	private boolean isGameThread() {
		final Class<?>[] context = getClassContext();
		for (int i = 1; i < 5; i++) {
			if (context[i].isAssignableFrom(RestrictedSecurityManager.class)) {
				continue;
			}
			final ClassLoader loader = context[i].getClassLoader();
			if (loader != null && loader.getClass().isAssignableFrom(RSClassLoader.class)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isScriptThread(final Thread t) {
		final ClassLoader loader = t.getContextClassLoader();
		return loader != null && (loader.getClass().isAssignableFrom(ScriptClassLoader.class) || loader.getClass().isAssignableFrom(LocalScriptClassLoader.class));
	}

	public static void assertNonScript() {
		final SecurityManager sm = System.getSecurityManager();
		if (sm == null || !(sm instanceof RestrictedSecurityManager) || ((RestrictedSecurityManager) sm).isScriptThread()) {
			throw new SecurityException();
		}
	}
}
