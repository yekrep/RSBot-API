package org.powerbot.bot.nloader;

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
	private final Map<String, byte[]> resources = new HashMap<>();
	private final Hashtable<String, Class<?>> loaded;
	private final ProtectionDomain domain;
	private Processor processor;

	public GameClassLoader(final Map<String, byte[]> resources) {
		this.resources.putAll(resources);
		loaded = new Hashtable<>();
		CodeSource codesource = new CodeSource(null, (java.security.cert.Certificate[]) null);
		Permissions permissions = new Permissions();
		permissions.add(new AllPermission());
		domain = new ProtectionDomain(codesource, permissions);
		AppletTransform appletTransform = new AppletTransform();
		processor = new AbstractProcessor(appletTransform,
				new ClassLoaderTransform(appletTransform), new ListClassesTransform(appletTransform));
	}

	@Override
	protected final synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (loaded.containsKey(name)) {
			return loaded.get(name);
		}
		byte[] bytes = resources.remove(name + ".class");
		if (bytes != null) {
			bytes = processor.transform(bytes);
			Class<?> clazz = defineClass(name, bytes, 0, bytes.length, domain);
			if (resolve) {
				resolveClass(clazz);
			}
			loaded.put(name, clazz);
			return clazz;
		}
		return super.findSystemClass(name);
	}

	@Override
	public final InputStream getResourceAsStream(String name) {
		byte[] resource = resources.get(name);
		if (resource != null) {
			return new ByteArrayInputStream(resource);
		}
		return ClassLoader.getSystemResourceAsStream(name);
	}
}
