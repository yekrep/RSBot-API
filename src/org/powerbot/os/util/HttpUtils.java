package org.powerbot.os.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.powerbot.os.Configuration;

/**
 * @author Paris
 */
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

	public static void download(final HttpURLConnection con, final File file) throws IOException {
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
	}

	public static HttpURLConnection getHttpConnection(final URL url) throws IOException {
		final HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.addRequestProperty("Host", url.getHost());
		con.addRequestProperty("Connection", "close");
		con.addRequestProperty("User-Agent", url.getHost().endsWith("." + Configuration.URLs.DOMAIN) || url.getHost().equals(Configuration.URLs.DOMAIN) ? HTTP_USERAGENT_REAL : HTTP_USERAGENT_FAKE);
		con.addRequestProperty("Accept-Encoding", "gzip, deflate");
		con.addRequestProperty("Accept-Charset", "ISO-8859-1,UTF-8;q=0.7,*;q=0.7");
		con.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
		con.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		con.setConnectTimeout(10000);
		return con;
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
