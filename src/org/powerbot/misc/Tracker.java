package org.powerbot.misc;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.powerbot.Configuration;
import org.powerbot.util.HttpUtils;

public final class Tracker {
	private static Tracker instance;
	private static final String TRACKING_ID = "UA-5170375-18", PAGE_PREFIX = "/rsbot/", HOSTNAME = "services." + Configuration.URLs.DOMAIN;
	private final String locale, resolution, colours;
	private final Random r;
	private final File cache;
	private final AtomicLong cacheTime;
	private final int visitor;
	private final long[] timestamps;
	private final AtomicInteger visits;

	private Tracker() {
		final Locale l = Locale.getDefault();
		locale = (l.getLanguage() + (l.getCountry().length() != 0 ? "-" + l.getCountry() : "")).toLowerCase();

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		resolution = (int) d.getWidth() + "x" + (int) d.getHeight();
		colours = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getColorModel().getPixelSize() + "-bit";

		cache = new File(Configuration.HOME, CryptFile.getHashedName(TRACKING_ID + ".1.ga"));
		cacheTime = new AtomicLong(0);

		r = new Random();
		visitor = (int) (Configuration.getUID() & Integer.MAX_VALUE);
		timestamps = new long[2];
		visits = new AtomicInteger(-1);
		getTimestamps();

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
			if (cacheTime.get() < cache.lastModified()) {
				DataInputStream in = null;
				try {
					in = new DataInputStream(new FileInputStream(cache));
					for (int i = 0; i < timestamps.length; i++) {
						timestamps[i] = in.readLong();
					}
					visits.set(in.readInt());
					in.close();
				} catch (final IOException ignored) {
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (final IOException ignored) {
						}
					}
				}
			}

			final long prev = timestamps[1];
			timestamps[1] = System.currentTimeMillis() / 1000L;

			DataOutputStream out = null;
			try {
				out = new DataOutputStream(new FileOutputStream(cache));
				for (final long t : timestamps) {
					out.writeLong(t);
				}
				out.writeInt(visits.incrementAndGet());
				out.close();
				final long mod = System.currentTimeMillis();
				cacheTime.set(mod);
				cache.setLastModified(mod);
			} catch (final IOException ignored) {
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (final IOException ignored) {
					}
				}
			}

			return new long[]{timestamps[0], prev, timestamps[1]};
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
		new Thread(new Runnable() {
			@Override
			public void run() {
				call(url);
			}
		}).start();
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
		if (s == null || s.isEmpty()) {
			return "";
		}
		try {
			return URLEncoder.encode(s, "UTF-8").replace("+", "%20");
		} catch (final UnsupportedEncodingException ignored) {
		}
		return "";
	}

	private static void call(final String url) {
		try {
			final HttpURLConnection con = HttpUtils.openConnection(new URL(url));
			con.connect();
			con.getResponseCode();
			con.disconnect();
		} catch (final IOException ignored) {
		}
	}
}
