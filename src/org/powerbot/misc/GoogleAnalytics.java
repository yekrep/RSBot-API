package org.powerbot.misc;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;

import org.powerbot.Boot;
import org.powerbot.Configuration;
import org.powerbot.gui.BotLauncher;
import org.powerbot.util.HttpUtils;
import org.powerbot.util.StringUtils;

public class GoogleAnalytics {
	private static final String TID = "UA-5170375-18", PATH = "/" + Configuration.NAME.toLowerCase() + "/", HOST = "services." + Configuration.URLs.DOMAIN;
	private static final GoogleAnalytics instance = new GoogleAnalytics();
	private final String payload;

	private GoogleAnalytics() {
		final StringBuilder s = new StringBuilder();
		s.append("v=1");
		s.append("&tid=").append(TID);
		final long uid = Configuration.UID;
		s.append("&cid=").append(new UUID(~uid, uid).toString());
		final Dimension sr = Toolkit.getDefaultToolkit().getScreenSize();
		s.append("&sr=").append((int) sr.getWidth()).append('x').append((int) sr.getHeight());
		s.append("&de=UTF-8");
		s.append("&sd=").append(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getColorModel().getPixelSize()).append("-bits");
		s.append("&je=1");
		final Locale l = Locale.getDefault();
		s.append("&ul=").append((l.getLanguage() + (l.getCountry().length() != 0 ? "-" + l.getCountry() : "")).toLowerCase());
		s.append("&dh=").append(HOST);
		s.append("&an=").append(StringUtils.urlEncode(Configuration.NAME));
		s.append("&aid=").append(StringUtils.urlEncode(Boot.class.getPackage().getName()));
		s.append("&av=").append(Configuration.VERSION);
		payload = s.toString();
	}

	public static GoogleAnalytics getInstance() {
		return instance;
	}

	public void pageview(final String path, final String title) {
		final StringBuilder s = getPayload();
		s.append("&t=pageview");
		s.append("&dp=").append(PATH).append(StringUtils.urlEncode(path));
		s.append("&dt=").append(StringUtils.urlEncode(title));
		send(s);
	}

	private StringBuilder getPayload() {
		final StringBuilder s = new StringBuilder().append(payload);

		final Component vp = BotLauncher.getInstance().window.get();
		if (vp != null) {
			s.append("&vp=").append(vp.getWidth()).append('x').append(vp.getHeight());
		}

		final NetworkAccount n = NetworkAccount.getInstance();
		if (n.isLoggedIn()) {
			s.append("&uid=").append(Integer.toString(n.getUID()));
		}

		return s;
	}

	private void send(final StringBuilder s) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				post(s.toString());
			}
		}).start();
	}

	private int post(final String data) {
		try {
			final HttpURLConnection con = HttpUtils.openConnection(new URL("https://ssl.google-analytics.com/collect"));
			con.setRequestMethod("POST");
			con.setDoOutput(true);

			final OutputStream out = con.getOutputStream();
			out.write(StringUtils.getBytesUtf8(data));
			out.flush();
			out.close();

			return con.getResponseCode();
		} catch (final IOException ignored) {
		}

		return -1;
	}
}
