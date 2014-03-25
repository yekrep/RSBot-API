package org.powerbot.bot.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.powerbot.util.HttpUtils;
import org.powerbot.util.IOUtils;

public abstract class GameLoader implements Callable<ClassLoader> {
	private final String archive;
	private final String referer;
	private final Map<String, byte[]> resources;
	protected final Map<String, byte[]> classes;
	protected String hash;

	public GameLoader(final String archive, final String referer) {
		this.archive = archive;
		this.referer = referer;
		resources = new ConcurrentHashMap<String, byte[]>();
		classes = new ConcurrentHashMap<String, byte[]>();
	}

	@Override
	public ClassLoader call() throws Exception {
		byte[] b;
		try {
			final URLConnection clientConnection = HttpUtils.getHttpConnection(new URL(archive));
			clientConnection.addRequestProperty("Referer", referer);
			b = IOUtils.read(HttpUtils.getInputStream(clientConnection));
		} catch (final IOException ignored) {
			b = null;
		}
		if (b == null) {
			return null;
		}
		hash = LoaderUtils.hash(b);
		JarInputStream j = null;
		try {
			j = new JarInputStream(new ByteArrayInputStream(b));
			JarEntry e;
			while ((e = j.getNextJarEntry()) != null) {
				final String n = e.getName();
				resources.put(n, stream(j));
				final int p = n.indexOf(".class");
				if (p != -1) {
					classes.put(n.substring(0, p), resources.get(n));
				}
			}
		} catch (final IOException ignored) {
		} finally {
			if (j != null) {
				try {
					j.close();
				} catch (final IOException ignored) {
				}
			}
		}
		return new GameClassLoader(resources, transformer());
	}

	protected abstract Transformer transformer();

	public byte[] resource(final String str) {
		return resources.get(str);
	}

	private static byte[] stream(final JarInputStream in) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte[] buf = new byte[2048];
		int l;
		while (in.available() > 0 && (l = in.read(buf, 0, buf.length)) != -1) {
			out.write(buf, 0, l);
		}
		return out.toByteArray();
	}
}
