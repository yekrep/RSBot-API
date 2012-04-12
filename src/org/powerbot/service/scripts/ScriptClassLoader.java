package org.powerbot.service.scripts;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.powerbot.util.io.IOHelper;

/**
 * @author Paris
 */
public final class ScriptClassLoader extends ClassLoader {
	private final URL base;

	public ScriptClassLoader(final URL base) {
		this.base = base;
	}

	@Override
	public Class<?> loadClass(final String name) throws ClassNotFoundException {
		final Class<?> clazz = findLoadedClass(name);
		if (clazz != null) {
			return clazz;
		}
		try {
			final InputStream is = getResourceAsStream(name.replace('.', '/') + ".class");
			final byte[] buf = IOHelper.read(is);
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
