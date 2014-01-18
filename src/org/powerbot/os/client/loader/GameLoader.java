package org.powerbot.os.client.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.powerbot.os.Configuration;
import org.powerbot.os.util.HttpUtils;
import org.powerbot.os.util.IOUtils;

public class GameLoader implements Callable<ClassLoader> {
	public final GameCrawler crawler;
	private final Map<String, byte[]> resources;

	public GameLoader(final GameCrawler crawler) {
		this.crawler = crawler;
		resources = new HashMap<String, byte[]>();
	}

	@Override
	public ClassLoader call() {
		byte[] buf = null;

		try {
			final HttpURLConnection con = HttpUtils.getHttpConnection(new URL(crawler.archive));
			con.addRequestProperty("Referer", crawler.game);
			final File cache = new File(Configuration.TEMP, "client.jar");
			HttpUtils.download(con, cache);
			buf = IOUtils.read(cache);
		} catch (final IOException ignored) {
		}

		if (buf == null) {
			return null;
		}

		try {
			final JarInputStream jar = new JarInputStream(new ByteArrayInputStream(buf));
			JarEntry e;
			while ((e = jar.getNextJarEntry()) != null) {
				resources.put(e.getName(), read(jar));
			}
		} catch (final IOException ignored) {
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
