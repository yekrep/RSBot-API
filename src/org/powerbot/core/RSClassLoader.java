package org.powerbot.core;

import java.awt.AWTPermission;
import java.io.File;
import java.io.FilePermission;
import java.net.SocketPermission;
import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyPermission;

import org.powerbot.game.client.RandomAccessFile;
import org.powerbot.util.io.IOHelper;

public class RSClassLoader extends ClassLoader {
	private final Map<String, byte[]> classes = new HashMap<String, byte[]>();
	private final ProtectionDomain domain;

	public RSClassLoader(final Map<String, byte[]> classes, final URL source) {
		final CodeSource codeSource = new CodeSource(source, (CodeSigner[]) null);
		domain = new ProtectionDomain(codeSource, createPermissions());
		this.classes.putAll(classes);

		try {
			final Class<?> raf = RandomAccessFile.class;
			final byte[] data = IOHelper.read(raf.getClassLoader().getResourceAsStream(raf.getName().replace('.', File.separatorChar) + ".class"));
			this.classes.put(raf.getName(), data);
		} catch (final Exception ignored) {
		}
	}

	private Permissions createPermissions() {
		final Permissions instance = new Permissions();
		instance.add(new AWTPermission("accessEventQueue"));
		instance.add(new PropertyPermission("user.home", "read"));
		instance.add(new PropertyPermission("java.vendor", "read"));
		instance.add(new PropertyPermission("java.version", "read"));
		instance.add(new PropertyPermission("os.name", "read"));
		instance.add(new PropertyPermission("os.arch", "read"));
		instance.add(new PropertyPermission("os.version", "read"));
		instance.add(new SocketPermission("*", "connect,resolve"));
		String uDir = System.getProperty("user.home");
		if (uDir != null) {
			uDir += "/";
		} else {
			uDir = "~/";
		}
		final String[] dirs = {"c:/rscache/", "/rscache/", "c:/windows/", "c:/winnt/", "c:/", uDir, "/tmp/", "."};
		final String[] rsDirs = {".jagex_cache_32", ".file_store_32"};
		for (String dir : dirs) {
			final File f = new File(dir);
			instance.add(new FilePermission(dir, "read"));
			if (!f.exists()) {
				continue;
			}
			dir = f.getPath();
			for (final String rsDir : rsDirs) {
				instance.add(new FilePermission(dir + File.separator + rsDir + File.separator + "-", "read"));
				instance.add(new FilePermission(dir + File.separator + rsDir + File.separator + "-", "write"));
			}
		}
		Calendar.getInstance();
		instance.setReadOnly();
		return instance;
	}

	@Override
	public final Class<?> loadClass(final String name) throws ClassNotFoundException {
		if (classes.containsKey(name)) {
			final byte buffer[] = classes.remove(name);
			try {
				return defineClass(name, buffer, 0, buffer.length, domain);
			} catch (final Throwable t) {
				t.printStackTrace();
			}
		}
		return super.loadClass(name);
	}
}
