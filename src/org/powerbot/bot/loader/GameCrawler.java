package org.powerbot.bot.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.powerbot.Configuration;
import org.powerbot.util.HttpUtils;
import org.powerbot.util.IOUtils;

public class GameCrawler implements Callable<Boolean> {
	private final String pre;
	public final Map<String, String> parameters, properties;
	public String game, archive, clazz;

	public GameCrawler(final String pre) {
		this.pre = pre;
		parameters = new HashMap<String, String>();
		properties = new HashMap<String, String>();
	}

	@Override
	public Boolean call() {
		final String param = "param=", msg = "msg=";
		BufferedReader br = null;
		String k;

		try {
			br = new BufferedReader(new InputStreamReader(HttpUtils.openStream(new URL("http://" + pre + "."
					+ Configuration.URLs.GAME + "/k=3/l=" + System.getProperty("user.language", "en") + "/jav_config.ws"))));

			while ((k = br.readLine()) != null) {
				if (k.isEmpty()) {
					continue;
				}

				Map<String, String> map = properties;

				if (k.startsWith(param)) {
					map = parameters;
					k = k.substring(param.length());
				} else if (k.startsWith(msg)) {
					continue;
				}

				int z = k.indexOf('=');
				if (z != -1) {
					map.put(k.substring(0, z), ++z == k.length() ? "" : k.substring(z));
				}
			}
		} catch (final IOException ignored) {
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (final IOException ignored) {
				}
			}
		}

		if (properties.containsKey(k = "initial_class")) {
			clazz = properties.get(k);
			if (clazz.endsWith(k = ".class")) {
				clazz = clazz.substring(0, clazz.length() - k.length());
			}
		}

		if (properties.containsKey(k = "initial_jar")) {
			archive = properties.get("codebase") + properties.get(k);
		}

		game = "";
		return clazz != null && archive != null;
	}

	protected final String download(final String url, final String referer) {
		try {
			final HttpURLConnection con = HttpUtils.openConnection(new URL(url));
			if (referer != null && !referer.isEmpty()) {
				con.setRequestProperty("Referer", referer);
			}
			return IOUtils.readString(HttpUtils.openStream(con));
		} catch (final IOException ignored) {
			return null;
		}
	}
}
