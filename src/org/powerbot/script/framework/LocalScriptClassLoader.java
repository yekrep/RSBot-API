package org.powerbot.script.framework;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.powerbot.util.io.IOHelper;

/**
 * @author Paris
 */
public final class LocalScriptClassLoader extends ClassLoader {
	private final URL base;

	public LocalScriptClassLoader(final URL base) {
		this.base = base;
	}

	@Override
	public Class<?> loadClass(final String name) throws ClassNotFoundException {
		final Class<?> clazz = findLoadedClass(name);
		if (clazz != null) {
			return clazz;
		}
		try {
			final String path = name.replace('.', '/') + ".class";
			final byte[] buf = IOHelper.read(getResourceAsStream(path));
			return defineClass(name, buf, 0, buf.length);
		} catch (final Exception ignored) {
			return super.loadClass(name);
		}
	}

	@Override
	public InputStream getResourceAsStream(final String name) {
		try {
			return new URL(base, name).openStream();
		} catch (IOException ignored) {
			return null;
		}
	}
}
