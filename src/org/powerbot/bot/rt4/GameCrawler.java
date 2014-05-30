package org.powerbot.bot.rt4;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.powerbot.Configuration;

class GameCrawler extends org.powerbot.bot.loader.GameCrawler {

	@Override
	public Boolean call() {
		Pattern p;
		Matcher m;
		String url, referer, html;

		url = "http://oldschool." + Configuration.URLs.GAME + "/";
		html = download(url, null);
		if (html == null) {
			return false;
		}
		p = Pattern.compile("<a href=\"(http://[^\\\"]+)\">.+?Play Now", Pattern.CASE_INSENSITIVE);
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

		p = Pattern.compile("<(title)\\b[^>]*>\\s*([^<]*)</\\1>", Pattern.CASE_INSENSITIVE);
		m = p.matcher(html);
		if (m.find()) {
			String t = m.group(2), s = "Game";
			if (t.endsWith(s)) {
				t = t.substring(0, t.length() - s.length());
			}
			properties.put(m.group(1), t.trim());
		}

		final int z = html.indexOf("id=game");
		if (z != -1) {
			html = html.substring(z);
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
}
