package org.powerbot.util;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.powerbot.util.io.CryptFile;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;

/**
 * @author Paris
 */
public final class Tracker {
	private static Tracker instance;
	private final Executor exec;
	private static final String TRACKING_ID = "UA-5170375-18", PAGE_PREFIX = "/rsbot/", HOSTNAME = "services.powerbot.org";
	private final String locale, resolution, colours;
	private final Random r;
	private final int cookie, visitor;

	private Tracker() {
		exec = Executors.newSingleThreadExecutor();

		final Locale l = Locale.getDefault();
		locale = (l.getLanguage() + (l.getCountry().length() != 0 ? "-" + l.getCountry() : "")).toLowerCase();

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		resolution = (int) d.getWidth() + "x" + (int) d.getHeight();
		colours = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getColorModel().getPixelSize() + "-bit";

		r = new Random();
		visitor = (int) (Configuration.getUID() & Integer.MAX_VALUE);
		cookie = visitor;
	}

	public synchronized static Tracker getInstance() {
		if (instance == null) {
			instance = new Tracker();
		}
		return instance;
	}

	public void trackEvent(final String category, final String action) {
		trackEvent(category, action, null);
	}

	public void trackEvent(final String category, final String action, final String label) {
		final StringBuilder s = getBase();
		s.append("&utmt=event");
		s.append("&utme=5(").append(encode(category)).append("*").append(encode(action)).append(label != null ? "*" + encode(label) : "").append(")&utmu=6AAAAAAAI~");
		track(s.toString());
	}

	public void trackPage(final String page, final String title) {
		final StringBuilder s = getBase();
		s.append("&utmdt=").append(encode(title));
		s.append("&utmp=").append(encode(PAGE_PREFIX)).append(encode(page));
		s.append("&utmu=q~");
		track(s.toString());
	}

	private void track(final String url) {
		exec.execute(new Runnable() {
			@Override
			public void run() {
				call(url);
			}
		});
	}

	private StringBuilder getBase() {
		final StringBuilder s = new StringBuilder(), utmcc = new StringBuilder();
		final long now = System.currentTimeMillis();
		utmcc.append("__utma=").append(cookie).append(".").append(visitor).append(".").append(now).append(".").append(now).append(".").append(now).append(".1; ");
		utmcc.append("__utmz=").append(cookie).append(".").append(now).append(".1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none);");
		s.append("http://www.google-analytics.com/__utm.gif");
		s.append("?utmwv=4.9.4");
		s.append("&utmn=").append(r.nextInt());
		s.append("&utmhn=").append(HOSTNAME);
		s.append("&utmcs=UTF-8");
		s.append("&utmsr=").append(resolution);
		s.append("&utmsc=").append(colours);
		s.append("&utmul=").append(locale);
		s.append("&utmje=1");
		s.append("&utmfl=10.3%20r181");
		s.append("&utmhid=").append(r.nextInt() & Integer.MAX_VALUE);
		s.append("&utmr=-");
		s.append("&utmac=").append(TRACKING_ID);
		s.append("&utmcc=").append(encode(utmcc.toString()));
		return s;
	}

	private String encode(final String s) {
		try {
			return URLEncoder.encode(s, "UTF-8").replace("+", "%20");
		} catch (final UnsupportedEncodingException ignored) {
		}
		return "";
	}

	private static void call(final String url) {
		final CryptFile cf = new CryptFile("tracker-cookies.txt", Tracker.class);
		String cookies = null;
		final StringBuilder c = new StringBuilder();

		try {
			cookies = IOHelper.readString(cf.getInputStream());
		} catch (final IOException ignored) {
		}

		try {
			final HttpURLConnection con = HttpClient.getHttpConnection(new URL(url));

			if (cookies != null && !cookies.isEmpty()) {
				con.setRequestProperty("Cookie", cookies.trim());
			}

			con.connect();
			con.getResponseCode();

			for (final Entry<String, List<String>> header : con.getHeaderFields().entrySet()) {
				if (header.getKey() != null && header.getKey().equalsIgnoreCase("Set-Cookie")) {
					if (c.length() != 0) {
						c.append("; ");
					}
					c.append(header.getValue());
				}
			}

			con.disconnect();
		} catch (final IOException ignored) {
		}

		if (c.length() == 0) {
			cf.delete();
		} else {
			final ByteArrayInputStream bis = new ByteArrayInputStream(StringUtil.getBytesUtf8(c.toString().trim()));
			try {
				IOHelper.write(bis, cf.getOutputStream());
			} catch (final IOException ignored) {
			}
		}
	}
}
