package org.powerbot.bot.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;

public class GameLoader implements Callable<ClassLoader> {
	private final Crawler crawler;
	private final Map<String, byte[]> resources;

	public GameLoader(Crawler crawler) {
		this.crawler = crawler;
		this.resources = new HashMap<String, byte[]>();
	}

	@Override
	public ClassLoader call() {
		byte[] buffer;
		try {
			final URLConnection clientConnection = HttpClient.getHttpConnection(new URL(crawler.archive));
			clientConnection.addRequestProperty("Referer", crawler.game);
			buffer = IOHelper.read(HttpClient.getInputStream(clientConnection));
		} catch (IOException ignored) {
			buffer = null;
		}
		if (buffer == null) {
			return null;
		}

		try {
			JarInputStream jar = new JarInputStream(new ByteArrayInputStream(buffer));
			JarEntry entry;
			while ((entry = jar.getNextJarEntry()) != null) {
				String entryName = entry.getName();
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
