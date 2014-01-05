package org.powerbot.bot.loader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.powerbot.Configuration;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;

/**
 * @author Paris
 */
public class Crawler implements Runnable {
	private AtomicBoolean run, passed;
	public final Map<String, String> parameters, details;
	public String game, archive, clazz;

	public Crawler() {
		run = new AtomicBoolean(false);
		passed = new AtomicBoolean(false);
		parameters = new HashMap<String, String>();
		details = new HashMap<String, String>();
	}

	public boolean crawl() {
		if (!run.get()) {
			run();
		}
		return passed.get();
	}

	@Override
	public void run() {
		if (!run.compareAndSet(false, true)) {
			return;
		}

		Pattern p;
		Matcher m;
		String url, referer, html;

		url = "http://www." + Configuration.URLs.GAME + "/game";
		referer = null;
		html = download(url, referer);
		if (html == null) {
			return;
		}
		p = Pattern.compile("<iframe id=\"game\" src=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
		m = p.matcher(html);
		if (!m.find()) {
			return;
		}
		referer = url;
		url = m.group(1);

		html = download(url, referer);
		if (html == null) {
			return;
		}
		game = url;
		p = Pattern.compile("<applet name=runescape id=game .+\\barchive=(\\S+).+\\bcode=(\\S+)\\.class", Pattern.CASE_INSENSITIVE);
		m = p.matcher(html);
		if (!m.find()) {
			return;
		}
		archive = game.substring(0, game.lastIndexOf('/') + 1) + m.group(1);
		clazz = m.group(2);

		p = Pattern.compile("<param name=\"([^\\s]+)\"\\s+value=\"([^>]*)\">", Pattern.CASE_INSENSITIVE);
		m = p.matcher(html);

		while (m.find()) {
			parameters.put(m.group(1), m.group(2));
		}
		parameters.remove("haveie6");

		p = Pattern.compile("<(title)\\b[^>]*>([^<]+)</\\1>", Pattern.CASE_INSENSITIVE);
		m = p.matcher(html);
		if (m.find()) {
			details.put(m.group(1), m.group(2).trim());
		}

		passed.set(true);
	}

	private String download(final String url, final String referer) {
		try {
			final HttpURLConnection con = HttpClient.getHttpConnection(new URL(url));
			con.setRequestProperty("User-Agent", HttpClient.HTTP_USERAGENT_FAKE);
			if (referer != null) {
				con.setRequestProperty("Referer", referer);
			}
			return IOHelper.readString(HttpClient.getInputStream(con));
		} catch (final IOException ignored) {
			ignored.printStackTrace();
			return null;
		}
	}
}
