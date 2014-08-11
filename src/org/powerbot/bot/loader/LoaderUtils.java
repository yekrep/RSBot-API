package org.powerbot.bot.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.powerbot.Configuration;
import org.powerbot.misc.GoogleAnalytics;
import org.powerbot.util.HttpUtils;
import org.powerbot.util.IOUtils;
import org.powerbot.util.StringUtils;

public class LoaderUtils {

	private static String hash(final Map<String, byte[]> map) {
		final MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		final SortedSet<String> keys = new TreeSet<String>(map.keySet());
		for (final String k : keys) {
			md.update(map.get(k));
		}
		return StringUtils.byteArrayToHexString(md.digest());
	}

	public static String hash(final byte[] bytes) {
		final MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-1");
			return StringUtils.byteArrayToHexString(digest.digest(bytes));
		} catch (final NoSuchAlgorithmException ignored) {
			return null;
		}
	}

	private static Cipher cipher(final String hash, final int mode) throws IOException {
		try {
			final MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(StringUtils.getBytesUtf8(hash));
			final byte[] b = new byte[16];
			System.arraycopy(md.digest(), 0, b, 0, b.length);
			final SecretKey key = new SecretKeySpec(b, 0, b.length, "ARCFOUR");

			final Cipher c = Cipher.getInstance("RC4");
			c.init(mode, key);
			return c;
		} catch (final GeneralSecurityException e) {
			throw new IOException(e);
		}
	}

	public static TransformSpec get(final Map<String, byte[]> classes, final String hash) throws IOException, PendingException {
		final HttpURLConnection con = HttpUtils.openConnection(new URL(String.format(Configuration.URLs.TSPEC, hash)));
		con.connect();
		final int r = con.getResponseCode();
		GoogleAnalytics.getInstance().pageview("loader/spec/" + hash, Integer.toString(r));

		if (r == HttpURLConnection.HTTP_OK) {
			try {
				return TransformSpec.parse(new CipherInputStream(HttpUtils.openStream(con), cipher(hash, Cipher.DECRYPT_MODE)));
			} catch (final NullPointerException e) {
				throw new IOException(e);
			}
		} else if (r == HttpURLConnection.HTTP_FORBIDDEN || r == HttpURLConnection.HTTP_NOT_FOUND) {
			put(classes, hash);
		}

		throw new IOException();
	}

	private static void put(final Map<String, byte[]> classes, final String hash) throws IOException, PendingException {
		final File f = File.createTempFile(Integer.toHexString(classes.hashCode()), null);
		final OutputStream out = new CipherOutputStream(new GZIPOutputStream(new FileOutputStream(f)), cipher(hash, Cipher.ENCRYPT_MODE));
		writePack(classes, out);
		out.close();

		final HttpURLConnection con = HttpUtils.openConnection(new URL(String.format(Configuration.URLs.TSPEC_PROCESS,
				hash + ".pack", Long.toString(f.length()), hash)));
		con.setInstanceFollowRedirects(false);
		final int r = con.getResponseCode();
		final String pre = "loader/spec/" + hash + "/bucket";
		GoogleAnalytics.getInstance().pageview(pre, Integer.toString(r));
		con.disconnect();

		try {
			switch (r) {
			case HttpURLConnection.HTTP_SEE_OTHER:
				final HttpURLConnection upload = HttpUtils.openConnection(new URL(con.getURL(), con.getHeaderField("location")));
				upload.setRequestMethod("PUT");
				upload.setDoOutput(true);
				IOUtils.write(new FileInputStream(f), upload.getOutputStream());
				GoogleAnalytics.getInstance().pageview(pre + "/upload", Integer.toString(r));
			case HttpURLConnection.HTTP_ACCEPTED:
				throw new PendingException(1000 * 60 * 3 + 30);
			default:
				GoogleAnalytics.getInstance().pageview(pre + "/failure", Integer.toString(r));
				throw new IOException("HTTP " + r);
			}
		} finally {
			f.delete();
		}
	}

	public static TransformSpec submit(final Logger log, final Map<String, byte[]> classes) {
		for (; ; ) {
			final String hash = hash(classes), id = hash.substring(0, 6);
			log.warning("Downloading update (" + id + ") \u2014 please wait");
			try {
				return get(classes, hash);
			} catch (final IOException e) {
				log.severe("Error: " + e.getMessage());
			} catch (final LoaderUtils.PendingException p) {
				final int d = p.delay / 1000;
				log.warning("Your update (" + id + ") is being processed, trying again in " + (d < 60 ? d + " seconds" : (int) Math.ceil(d / 60) + " minutes"));
				try {
					Thread.sleep(p.delay);
				} catch (final InterruptedException ignored) {
					break;
				}
				continue;
			}
			break;
		}

		return null;
	}

	private static void writePack(final Map<String, byte[]> classes, final OutputStream out) throws IOException {
		final int MAGIC = 0xC1A5700F, END_OF_FILE = 0x1;
		writeInt(MAGIC, out);

		for (final byte[] data : classes.values()) {
			writeInt(data.length, out);
			out.write(data);
		}

		writeInt(END_OF_FILE, out);
	}

	private static void writeInt(final int v, final OutputStream o) throws IOException {
		final int m = 0xff;
		o.write(v >>> 24);
		o.write(v >>> 16 & m);
		o.write(v >>> 8 & m);
		o.write(v & m);
	}

	public static final class PendingException extends Exception {
		public final int delay;

		public PendingException(final int delay) {
			this.delay = delay;
		}
	}
}
