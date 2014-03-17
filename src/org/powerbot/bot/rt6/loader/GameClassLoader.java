package org.powerbot.bot.rt6.loader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.powerbot.bot.rt6.loader.bytecode.AbstractProcessor;
import org.powerbot.bot.rt6.loader.bytecode.AppletTransform;
import org.powerbot.bot.rt6.loader.bytecode.ClassLoaderTransform;
import org.powerbot.bot.rt6.loader.bytecode.ListClassesTransform;
import org.powerbot.bot.rt6.loader.bytecode.Processor;

public class GameClassLoader extends ClassLoader {
	private final Map<String, byte[]> resources = new HashMap<String, byte[]>();
	private final Hashtable<String, Class<?>> loaded;
	private final ProtectionDomain domain;
	private final Processor processor;

	public GameClassLoader(final Map<String, byte[]> resources) {
		this.resources.putAll(resources);
		loaded = new Hashtable<String, Class<?>>();
		final CodeSource codesource = new CodeSource(null, (java.security.cert.Certificate[]) null);
		final Permissions permissions = new Permissions();
		permissions.add(new AllPermission());
		domain = new ProtectionDomain(codesource, permissions);
		final AppletTransform appletTransform = new AppletTransform();
		processor = new AbstractProcessor(appletTransform,
				new ClassLoaderTransform(appletTransform), new ListClassesTransform(appletTransform));
	}

	@Override
	protected final synchronized Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		if (loaded.containsKey(name)) {
			return loaded.get(name);
		}
		byte[] bytes = resources.remove(name + ".class");
		if (bytes != null) {
			bytes = processor.transform(bytes);
			final Class<?> clazz = defineClass(name, bytes, 0, bytes.length, domain);
			if (resolve) {
				resolveClass(clazz);
			}
			loaded.put(name, clazz);
			return clazz;
		}
		return super.findSystemClass(name);
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
