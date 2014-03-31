package org.powerbot.bot.rt6;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.powerbot.Configuration;

class GameCrawler extends org.powerbot.bot.loader.GameCrawler {

	@Override
	public Boolean call() {
		Pattern p;
		Matcher m;
		String url, referer, html;

		url = "http://www." + Configuration.URLs.GAME + "/game";
		referer = null;
		html = download(url, referer);
		if (html == null) {
			return false;
		}
		p = Pattern.compile("<iframe id=\"game\" src=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
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
		p = Pattern.compile("<applet name=runescape id=game .+\\barchive=(\\S+).+\\bcode=(\\S+)\\.class", Pattern.CASE_INSENSITIVE);
		m = p.matcher(html);
		if (!m.find()) {
			return false;
		}
		archive = game.substring(0, game.lastIndexOf('/') + 1) + m.group(1);
		clazz = m.group(2);

		p = Pattern.compile("<param name=\"([^\\s]+)\"\\s+value=\"([^>]*)\">", Pattern.CASE_INSENSITIVE);
		m = p.matcher(html);

		while (m.find()) {
			parameters.put(m.group(1), m.group(2));
		}
		parameters.remove("haveie6");

		p = Pattern.compile("<(title)\\b[^>]*>\\s*([^<\\s]+)([^<]*)</\\1>", Pattern.CASE_INSENSITIVE);
		m = p.matcher(html);
		if (m.find()) {
			properties.put(m.group(1), m.group(2).trim());
		}

		return true;
	}
}
