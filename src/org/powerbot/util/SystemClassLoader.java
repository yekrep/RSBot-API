package org.powerbot.util;

import java.net.URLClassLoader;

public class SystemClassLoader extends URLClassLoader {

	public SystemClassLoader() {
		super(((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs());
	}

	@Override
	protected synchronized Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		return super.loadClass(name, resolve);
	}
}
