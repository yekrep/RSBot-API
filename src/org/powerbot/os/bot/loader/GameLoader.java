package org.powerbot.os.bot.loader;

import org.powerbot.os.Configuration;
import org.powerbot.os.util.HttpUtils;
import org.powerbot.os.util.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class GameLoader implements Callable<ClassLoader> {
	public final GameCrawler crawler;
	private final Map<String, byte[]> resources;

	public GameLoader(final GameCrawler crawler) {
		this.crawler = crawler;
		resources = new HashMap<String, byte[]>();
	}

	@Override
	public ClassLoader call() {
		final File cache = new File(Configuration.TEMP, "game.jar");

		try {
			final HttpURLConnection con = HttpUtils.getHttpConnection(new URL(crawler.archive));
			con.addRequestProperty("Referer", crawler.game);
			HttpUtils.download(con, cache);
		} catch (final IOException ignored) {
		}

		if (!cache.exists()) {
			return null;
		}

		JarInputStream jar = null;
		try {
			jar = new JarInputStream(new FileInputStream(cache));
			JarEntry e;
			while ((e = jar.getNextJarEntry()) != null) {
				resources.put(e.getName(), read(jar));
			}
		} catch (final IOException ignored) {
		} finally {
			if (jar != null) {
				try {
					jar.close();
				} catch (final IOException ignored) {
				}
			}
		}

		return new GameClassLoader(resources);
	}

	private static byte[] read(final JarInputStream in) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte[] buf = new byte[2048];
		int l;
		while (in.available() > 0 && (l = in.read(buf, 0, buf.length)) != -1) {
			out.write(buf, 0, l);
		}
		return out.toByteArray();
	}
}
