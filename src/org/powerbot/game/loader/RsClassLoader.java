package org.powerbot.game.loader;

import java.awt.*;
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

/**
 * Loads the game classes with restrictions.
 *
 * @author Alex
 */
public class RsClassLoader extends ClassLoader {
	private final Map<String, byte[]> classes = new HashMap<String, byte[]>();
	private ProtectionDomain domain;

	public RsClassLoader(Map<String, byte[]> classes, URL source) {
		CodeSource codeSource = new CodeSource(source, (CodeSigner[]) null);
		domain = new ProtectionDomain(codeSource, createPermissions());
		this.classes.putAll(classes);
	}

	private Permissions createPermissions() {
		Permissions instance = new Permissions();
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
		String[] dirs = {"c:/rscache/", "/rscache/", "c:/windows/", "c:/winnt/", "c:/", uDir, "/tmp/", "."};
		String[] rsDirs = {".jagex_cache_32", ".file_store_32"};
		for (String dir : dirs) {
			File f = new File(dir);
			instance.add(new FilePermission(dir, "read"));
			if (!f.exists()) {
				continue;
			}
			dir = f.getPath();
			for (String rsDir : rsDirs) {
				instance.add(new FilePermission(dir + File.separator + rsDir + File.separator + "-", "read"));
				instance.add(new FilePermission(dir + File.separator + rsDir + File.separator + "-", "write"));
			}
		}
		Calendar.getInstance();
		//TimeZone.getDefault(); //set by Calendar
		instance.setReadOnly();
		return instance;
	}

	@Override
	public final Class<?> loadClass(String name) throws ClassNotFoundException {
		if (classes.containsKey(name)) {
			byte buffer[] = classes.remove(name);
			try {
				return defineClass(name, buffer, 0, buffer.length, domain);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return super.loadClass(name);
	}
}
