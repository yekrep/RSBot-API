package org.powerbot.bot.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.powerbot.Configuration;
import org.powerbot.util.HttpUtils;

public class GameCrawler {
	public final Map<String, String> parameters, properties;
	public final String game, archive, clazz;

	private GameCrawler(final Map<String, String> parameters, final Map<String, String> properties, final String archive, final String clazz) {
		this.parameters = parameters;
		this.properties = properties;
		this.archive = archive;
		this.clazz = clazz;
		game = "";
	}

	public static GameCrawler download(final String pre) {
		final String param = "param=", msg = "msg=";
		BufferedReader br = null;
		String k;

		final Map<String, String> parameters = new HashMap<String, String>(), properties = new HashMap<String, String>();

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
		} catch (final NullPointerException ignored) {
		} catch (final IOException ignored) {
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (final IOException ignored) {
				}
			}
		}

		String clazz = null, archive = null;

		if (properties.containsKey(k = "initial_class")) {
			clazz = properties.get(k);
			if (clazz.endsWith(k = ".class")) {
				clazz = clazz.substring(0, clazz.length() - k.length());
			}
		}

		if (properties.containsKey(k = "initial_jar")) {
			archive = properties.get("codebase") + properties.get(k);
		}

		return clazz != null && archive != null ? new GameCrawler(parameters, properties, archive, clazz) : null;
	}
}
