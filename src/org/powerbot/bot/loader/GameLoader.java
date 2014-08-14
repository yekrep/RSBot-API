package org.powerbot.bot.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.powerbot.util.HttpUtils;
import org.powerbot.util.IOUtils;

public abstract class GameLoader implements Callable<ClassLoader> {
	private final String archive, referer;
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
		final HttpURLConnection con = HttpUtils.openConnection(new URL(archive));
		con.addRequestProperty("Referer", referer);
		final byte[] b = IOUtils.read(HttpUtils.openStream(con));

		if (b == null || b.length == 0) {
			throw new IOException();
		}

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
		} finally {
			if (j != null) {
				try {
					j.close();
				} catch (final IOException ignored) {
				}
			}
		}

		hash = LoaderUtils.hash(classes);
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
