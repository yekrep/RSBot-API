package org.powerbot.bot.loader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.powerbot.util.HttpUtils;
import org.powerbot.util.IOUtils;

public abstract class GameCrawler implements Callable<Boolean> {
	public final Map<String, String> parameters, properties;
	public String game, archive, clazz;

	public GameCrawler() {
		parameters = new HashMap<String, String>();
		properties = new HashMap<String, String>();
	}

	protected final String download(final String url, final String referer) {
		try {
			final HttpURLConnection con = HttpUtils.openConnection(new URL(url));
			if (referer != null && !referer.isEmpty()) {
				con.setRequestProperty("Referer", referer);
			}
			return IOUtils.readString(HttpUtils.openStream(con));
		} catch (final IOException ignored) {
			ignored.printStackTrace();
			return null;
		}
	}
}
