package org.powerbot.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.powerbot.Configuration;

public class HttpUtils {
	public static final String HTTP_USERAGENT_FAKE, HTTP_USERAGENT_REAL;

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

	public static HttpURLConnection getHttpConnection(final URL url) throws IOException {
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

	public static HttpURLConnection download(final URL url, final File file) throws IOException {
		return download(getHttpConnection(url), file);
	}

	public static HttpURLConnection download(final HttpURLConnection con, final File file) throws IOException {
		if (file.exists()) {
			try {
				con.setIfModifiedSince(file.lastModified());
			} catch (final IllegalStateException ignored) {
			}
		}

		switch (con.getResponseCode()) {
		case HttpURLConnection.HTTP_OK:
			IOUtils.write(getInputStream(con), file);
			break;
		case HttpURLConnection.HTTP_NOT_FOUND:
		case HttpURLConnection.HTTP_GONE:
			if (file.exists()) {
				file.delete();
			}
			break;
		}

		con.disconnect();
		return con;
	}

	public static InputStream openStream(final URL url) throws IOException {
		return getInputStream(getHttpConnection(url));
	}

	public static InputStream openStream(final String link, final Object... args) throws IOException {
		final String[] s = splitPostURL(link, args);
		final URLConnection con = HttpUtils.getHttpConnection(new URL(s[0]));
		if (s.length > 1) {
			con.setDoOutput(true);
			if (s[1] != null && !s[1].isEmpty()) {
				OutputStreamWriter out = null;
				try {
					out = new OutputStreamWriter(con.getOutputStream());
					out.write(s[1]);
					out.flush();
				} catch (final IOException ignored) {
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (final IOException ignored) {
						}
					}
				}
			}
		}
		return HttpUtils.getInputStream(con);
	}

	public static String[] splitPostURL(final String link, final Object... args) {
		final String s = String.format(link, args), marker = "{POST}";
		final int z = s.indexOf(marker);
		if (z == -1) {
			return new String[]{s};
		}
		final int o = z + marker.length();
		String pre = s.substring(0, z);
		final String post = o >= s.length() ? "" : s.substring(o);
		if (pre.length() > 0 && pre.charAt(pre.length() - 1) == '?') {
			pre = pre.substring(0, pre.length() - 1);
		}
		return new String[]{pre, post};
	}

	public static InputStream getInputStream(final URLConnection con) throws IOException {
		return getInputStream(con.getInputStream(), con.getHeaderField("Content-Encoding"));
	}

	public static InputStream getInputStream(final InputStream in, final String encoding) throws IOException {
		if (encoding == null || encoding.isEmpty()) {
			return in;
		}
		if (encoding.equalsIgnoreCase("gzip")) {
			return new GZIPInputStream(in);
		} else if (encoding.equalsIgnoreCase("deflate")) {
			return new InflaterInputStream(in, new Inflater(true));
		}
		return in;
	}

	public static OutputStream getOutputStream(final OutputStream out, final String encoding) throws IOException {
		if (encoding == null || encoding.isEmpty()) {
			return out;
		}
		if (encoding.equalsIgnoreCase("gzip")) {
			return new GZIPOutputStream(out);
		} else if (encoding.equals("deflate")) {
			return new DeflaterOutputStream(out, new Deflater(4, true));
		}
		return out;
	}
}
