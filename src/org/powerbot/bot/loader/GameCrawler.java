package org.powerbot.bot.loader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.powerbot.Configuration;
import org.powerbot.util.HttpUtils;
import org.powerbot.util.IOUtils;

public class GameCrawler implements Callable<Boolean> {
	public final Map<String, String> parameters, properties;
	public String game, archive, clazz;

	public GameCrawler() {
		parameters = new HashMap<String, String>();
		properties = new HashMap<String, String>();
	}

	@Override
	public Boolean call() {
		Pattern p;
		Matcher m;
		String url;
		final String referer;
		String html;

		url = "http://oldschool." + Configuration.URLs.GAME_DOMAIN + "/";
		html = download(url, null);
		if (html == null) {
			return false;
		}
		p = Pattern.compile("<a href=\"(http://[^\\\"]+)\">Choose best members only world for me ", Pattern.CASE_INSENSITIVE);
		m = p.matcher(html);
		if (!m.find()) {
			return false;
		}

		referer = url;
		url = m.group(1);
		html = download(url, referer);
		if (html == null) {
			return false;
		}
		game = url;

		p = Pattern.compile(".+\\barchive=(\\S+)", Pattern.CASE_INSENSITIVE);
		m = p.matcher(html);
		if (!m.find()) {
			return false;
		}
		archive = game.substring(0, game.lastIndexOf('/') + 1) + m.group(1);
		p = Pattern.compile(".+\\bcode=(\\S+).class", Pattern.CASE_INSENSITIVE);
		m = p.matcher(html);
		if (!m.find()) {
			return false;
		}
		clazz = m.group(1);

		p = Pattern.compile("<param name=\"([^\\s]+)\"\\s+value=\"([^>]*)\">", Pattern.CASE_INSENSITIVE);
		m = p.matcher(html);

		while (m.find()) {
			parameters.put(m.group(1), m.group(2));
		}
		parameters.remove("haveie6");

		final int z = html.indexOf("id=game");
		if (z != -1) {
			for (final String k : new String[]{"width", "height"}) {
				p = Pattern.compile("\\b" + k + "=(?:['|\\\"])?(\\d+)", Pattern.CASE_INSENSITIVE);
				m = p.matcher(html);
				if (m.find()) {
					properties.put(k, m.group(1));
				}
			}
		}

		return true;
	}

	private String download(final String url, final String referer) {
		try {
			final HttpURLConnection con = HttpUtils.getHttpConnection(new URL(url));
			con.setRequestProperty("User-Agent", HttpUtils.HTTP_USERAGENT_FAKE);
			if (referer != null) {
				con.setRequestProperty("Referer", referer);
			}
			return IOUtils.readString(HttpUtils.getInputStream(con));
		} catch (final IOException ignored) {
			ignored.printStackTrace();
			return null;
		}
	}
}
