package org.powerbot.bot.rt4.loader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.powerbot.Configuration;
import org.powerbot.bot.loader.transform.TransformSpec;
import org.powerbot.misc.CryptFile;
import org.powerbot.util.HttpUtils;

public class GameClassLoader extends ClassLoader {
	private final Map<String, byte[]> resources = new HashMap<String, byte[]>();
	private final Hashtable<String, Class<?>> loaded;
	private final ProtectionDomain domain;
	private final TransformSpec spec;

	public GameClassLoader(final Map<String, byte[]> resources) {
		this.resources.putAll(resources);
		loaded = new Hashtable<String, Class<?>>();
		final CodeSource codesource = new CodeSource(null, (java.security.cert.Certificate[]) null);
		final Permissions permissions = new Permissions();
		permissions.add(new AllPermission());
		domain = new ProtectionDomain(codesource, permissions);

		final String packid = ""; // TODO: get standard hash of rt4 client pack (same order of files etc.)
		try {
			final CryptFile cache = new CryptFile("rt4.ts", getClass());
			spec = new TransformSpec(cache.download(HttpUtils.getHttpConnection(new URL(String.format(Configuration.URLs.TSPEC, "4", packid)))));
		} catch (final IOException e) {
			throw new IllegalStateException("bad resource", e);
		}

		spec.adapt();
	}

	@Override
	protected final synchronized Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		if (loaded.containsKey(name)) {
			return loaded.get(name);
		}
		final byte[] bytes_ = resources.remove(name + ".class");
		if (bytes_ != null) {
			final byte[] bytes = spec.process(bytes_);
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
