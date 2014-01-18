package org.powerbot.os.loader;

import org.powerbot.os.Configuration;
import org.powerbot.os.util.HttpUtils;
import org.powerbot.os.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class GameLoader implements Callable<ClassLoader> {
	private final Crawler crawler;
	private final Map<String, byte[]> resources;

	public GameLoader(final Crawler crawler) {
		this.crawler = crawler;
		this.resources = new HashMap<String, byte[]>();
	}

	@Override
	public ClassLoader call() {
		byte[] buffer;
		try {
			final HttpURLConnection con = HttpUtils.getHttpConnection(new URL(crawler.archive));
			con.addRequestProperty("Referer", crawler.game);
			final File cache = new File(Configuration.TEMP, "client.jar");
			HttpUtils.download(con, cache);
			buffer = IOUtils.read(cache);
		} catch (final IOException ignored) {
			buffer = null;
		}
		if (buffer == null) {
			return null;
		}

		try {
			final JarInputStream jar = new JarInputStream(new ByteArrayInputStream(buffer));
			JarEntry entry;
			while ((entry = jar.getNextJarEntry()) != null) {
				final String entryName = entry.getName();
				resources.put(entryName, read(jar));
			}
		} catch (IOException ignored) {
		}
		return new GameClassLoader(resources);
	}

	public Map<String, byte[]> getResources() {
		return Collections.unmodifiableMap(resources);
	}

	public Crawler getCrawler() {
		return crawler;
	}

	private static byte[] read(final JarInputStream inputStream) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte[] buffer = new byte[2048];
		int read;
		while (inputStream.available() > 0) {
			read = inputStream.read(buffer, 0, buffer.length);
			if (read < 0) {
				break;
			}
			out.write(buffer, 0, read);
		}
		return out.toByteArray();
	}
}
