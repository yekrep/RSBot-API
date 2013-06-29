package org.powerbot.bot;

import org.powerbot.bot.loader.transform.TransformSpec;
import org.powerbot.client.RandomAccessFile;
import org.powerbot.util.io.IOHelper;

import java.io.File;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

public class RSClassLoader extends ClassLoader {
	private final Map<String, byte[]> classes = new HashMap<>();
	private final ProtectionDomain domain;
	private final TransformSpec spec;

	public RSClassLoader(final Map<String, byte[]> classes, TransformSpec spec) {
		this.spec = spec;
		this.classes.putAll(classes);

		CodeSource codesource = new CodeSource(null, (java.security.cert.Certificate[]) null);
		Permissions permissions = new Permissions();
		permissions.add(new AllPermission());
		domain = new ProtectionDomain(codesource, permissions);
	}

	@Override
	public final Class<?> loadClass(final String name) throws ClassNotFoundException {
		if (classes.containsKey(name)) {
			final byte[] buffer = spec.process(name, classes.remove(name));
			try {
				return defineClass(name, buffer, 0, buffer.length, domain);
			} catch (final Throwable t) {
				t.printStackTrace();
			}
		}
		return super.loadClass(name);
	}
}
