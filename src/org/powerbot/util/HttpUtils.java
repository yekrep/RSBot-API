package org.powerbot.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.powerbot.Configuration;

public class HttpUtils {
	private static final String HTTP_USERAGENT_FAKE, HTTP_USERAGENT_REAL;

	static {
		final boolean x64 = System.getProperty("sun.arch.data.model").equals("64");
		final StringBuilder s = new StringBuilder(60);

		s.append(Configuration.NAME).append('/').append(Configuration.VERSION).append(" (");
		switch (Configuration.OS) {
		case UNKNOWN:
		case WINDOWS:
			s.append("Windows NT ").append(System.getProperty("os.version"));
			if (x64) {
				s.append("; WOW64");
			}
			break;
		case MAC:
			s.append("Macintosh; Intel ").append(System.getProperty("os.name")).append(' ').append(System.getProperty("os.version").replace('.', '_'));
			break;
		case LINUX:
			s.append("X11; Linux ").append(x64 ? "x86_64" : "i686");
			break;
		}
		s.append(") Java/").append(System.getProperty("java.version"));

		HTTP_USERAGENT_REAL = s.toString();

		s.setLength(0);
		s.append("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; ");
		if (x64) {
			s.append("WOW64; ");
		}
		s.append("Trident/6.0)");
		HTTP_USERAGENT_FAKE = s.toString();
	}

	public static HttpURLConnection openConnection(final URL url) throws IOException {
		final HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.addRequestProperty("Host", url.getHost());
		con.addRequestProperty("Connection", "close");
		con.addRequestProperty("Accept-Encoding", "gzip,deflate");
		con.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
		con.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		con.addRequestProperty("User-Agent", ("." + url.getHost()).endsWith("." + Configuration.URLs.GAME) ? HTTP_USERAGENT_FAKE : HTTP_USERAGENT_REAL);
		con.setConnectTimeout(10000);
		return con;
	}

	public static HttpURLConnection openConnection(final String url, final String... args) throws IOException {
		for (int i = 0; i < args.length; i++) {
			args[i] = StringUtils.urlDecode(args[i]);
		}

		final String post = "{POST}", cookie = "{COOKIE}";
		String u = String.format(url, (Object[]) args), p = null, c = null;

		int z = u.indexOf(cookie);
		if (z != -1) {
			c = u.substring(z + cookie.length()).replace("&", "; ");
			u = u.substring(0, z);
		}

		z = u.indexOf(post);
		if (z != -1) {
			p = u.substring(z + post.length());
			u = u.substring(0, z);
		}

		final HttpURLConnection con = openConnection(new URL(u));

		if (c != null) {
			con.addRequestProperty("Cookie", c);
		}

		if (p != null) {
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			IOUtils.write(new ByteArrayInputStream(StringUtils.getBytesUtf8(p)), con.getOutputStream());
		}

		return con;
	}

	public static HttpURLConnection download(final URL url, final File file) throws IOException {
		return download(openConnection(url), file);
	}

	private static HttpURLConnection download(final HttpURLConnection con, final File file) throws IOException {
		if (file.exists()) {
			try {
				con.setIfModifiedSince(file.lastModified());
			} catch (final IllegalStateException ignored) {
			}
		}

		switch (con.getResponseCode()) {
		case HttpURLConnection.HTTP_OK:
			IOUtils.write(openStream(con), file);
			break;
		case HttpURLConnection.HTTP_NOT_FOUND:
		case HttpURLConnection.HTTP_GONE:
			if (file.exists()) {
				//noinspection ResultOfMethodCallIgnored
				file.delete();
			}
			break;
		}

		con.disconnect();
		return con;
	}

	public static InputStream openStream(final URL url) throws IOException {
		return openStream(openConnection(url));
	}

	public static InputStream openStream(final URLConnection con) throws IOException {
		InputStream in;
		try {
			in = con.getInputStream();
		} catch (final FileNotFoundException e) {
			if (con instanceof HttpURLConnection) {
				in = ((HttpURLConnection) con).getErrorStream();
			} else {
				throw e;
			}
		}
		final String e = con.getHeaderField("Content-Encoding");
		if (e == null || e.isEmpty()) {
			return in;
		}
		if (e.equalsIgnoreCase("gzip")) {
			return new GZIPInputStream(in);
		} else if (e.equalsIgnoreCase("deflate")) {
			return new InflaterInputStream(in, new Inflater(true));
		}
		return in;
	}
}
