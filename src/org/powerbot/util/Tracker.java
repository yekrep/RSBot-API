package org.powerbot.util;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.powerbot.ipc.Controller;
import org.powerbot.util.io.CryptFile;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.IniParser;

/**
 * @author Paris
 */
public final class Tracker {
	private static Tracker instance;
	private final Executor exec;
	private static final String TRACKING_ID = "UA-5170375-18", PAGE_PREFIX = "/rsbot/", HOSTNAME = "services.powerbot.org";
	private static final String KEY_VISITS = "visits", KEY_FIRST = "first", KEY_PREVIOUS = "prev";
	private final String locale, resolution, colours;
	private final Random r;
	private final CryptFile store;
	private final int visitor;
	private final long[] timestamps;
	private final AtomicInteger visits;

	private Tracker() {
		exec = Executors.newSingleThreadExecutor();

		final Locale l = Locale.getDefault();
		locale = (l.getLanguage() + (l.getCountry().length() != 0 ? "-" + l.getCountry() : "")).toLowerCase();

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		resolution = (int) d.getWidth() + "x" + (int) d.getHeight();
		colours = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getColorModel().getPixelSize() + "-bit";

		r = new Random();
		visitor = (int) (Configuration.getUID() & Integer.MAX_VALUE);
		timestamps = new long[2];
		visits = new AtomicInteger(0);

		store = new CryptFile("tracker.ini", Tracker.class);
		if (store.exists()) {
			try (final InputStream in = store.getInputStream()) {
				final Map<String, String> data = IniParser.deserialise(in).get(IniParser.EMPTYSECTION);
				if (data.containsKey(KEY_VISITS)) {
					try {
						visits.set(Integer.parseInt(data.get(KEY_VISITS)));
					} catch (final NumberFormatException ignored) {
					}
				}
				if (data.containsKey(KEY_FIRST)) {
					try {
						timestamps[0] = Long.parseLong(data.get(KEY_FIRST));
					} catch (final NumberFormatException ignored) {
					}
				}
				if (data.containsKey(KEY_PREVIOUS)) {
					try {
						timestamps[1] = Long.parseLong(data.get(KEY_PREVIOUS));
					} catch (final NumberFormatException ignored) {
					}
				}
			} catch (final IOException ignored) {
			}
		}

		if (timestamps[0] == 0) {
			timestamps[0] = System.currentTimeMillis() / 1000L;
			timestamps[1] = timestamps[0];
		} else if (timestamps[1] == 0) {
			timestamps[1] = timestamps[0];
		}
	}

	public synchronized static Tracker getInstance() {
		if (instance == null) {
			instance = new Tracker();
		}
		return instance;
	}

	private long[] getTimestamps() {
		synchronized (timestamps) {
			final long prev = timestamps[1];
			timestamps[1] = System.currentTimeMillis() / 1000L;

			final Map<String, String> data = new HashMap<>(3);
			data.put(KEY_VISITS, Integer.toString(visits.incrementAndGet()));
			data.put(KEY_FIRST, Long.toString(timestamps[0]));
			data.put(KEY_FIRST, Long.toString(prev));
			final Map<String, Map<String, String>> map = new HashMap<>(1);
			map.put(IniParser.EMPTYSECTION, data);
			try (final OutputStream out = store.getOutputStream()) {
				IniParser.serialise(map, out);
			} catch (final IOException ignored) {
			}

			Controller.getInstance().updateTrackerTimestamps(visits.get(), timestamps[1]);

			return new long[] {timestamps[0], prev, timestamps[1]};
		}
	}

	public void setTimestamps(final int visits, final long prev) {
		synchronized (timestamps) {
			this.visits.set(visits);
			timestamps[1] = prev;
		}
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
		final long[] t = getTimestamps();
		utmcc.append("__utma=999.").append(visitor).append('.').append(t[0]).append('.').append(t[1]).append('.').append(t[2]).append('.').append(visits.get()).append("; ");
		utmcc.append("__utmz=999.").append(t[2]).append(".1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none);");
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
