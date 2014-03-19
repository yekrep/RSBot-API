package org.powerbot.bot.loader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class GameClassLoader extends ClassLoader {
	private final Map<String, byte[]> resources = new HashMap<String, byte[]>();
	private final Transformer transform;
	private final Hashtable<String, Class<?>> loaded;
	private final ProtectionDomain domain;

	public GameClassLoader(final Map<String, byte[]> resources, final Transformer transform) {
		this.resources.putAll(resources);
		this.transform = transform;
		loaded = new Hashtable<String, Class<?>>();
		final CodeSource codesource = new CodeSource(null, (java.security.cert.Certificate[]) null);
		final Permissions permissions = new Permissions();
		permissions.add(new AllPermission());
		domain = new ProtectionDomain(codesource, permissions);
	}

	@Override
	protected final synchronized Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		if (loaded.containsKey(name)) {
			return loaded.get(name);
		}
		byte[] b = resources.remove(name + ".class");
		if (b != null) {
			b = transform.transform(b);
			final Class<?> c = defineClass(name, b, 0, b.length, domain);
			if (resolve) {
				resolveClass(c);
			}
			loaded.put(name, c);
			return c;
		}
		return findSystemClass(name);
	}

	@Override
	public final InputStream getResourceAsStream(final String name) {
		final byte[] resource = resources.get(name);
		if (resource != null) {
			return new ByteArrayInputStream(resource);
		}
		return ClassLoader.getSystemResourceAsStream(name);
	}
}
