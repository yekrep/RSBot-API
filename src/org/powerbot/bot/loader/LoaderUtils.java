package org.powerbot.bot.loader;

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
import org.powerbot.util.StringUtils;

public class LoaderUtils {
	public static String hash(final Map<String, byte[]> map) {
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

	public static ReflectionSpec get(final String gv, final String hash) throws IOException {
		final String pre = "loader/spec/" + hash;
		final int r;

		final byte[] b = new byte[16];
		final String keyAlgo = "ARCFOUR", cipherAlgo = "RC4", hashAlgo = "SHA1";

		final MessageDigest md;
		try {
			md = MessageDigest.getInstance(hashAlgo);
		} catch (final NoSuchAlgorithmException e) {
			throw new IOException(e);
		}

		md.update(StringUtils.getBytesUtf8(hash));
		System.arraycopy(md.digest(), 0, b, 0, b.length);
		final SecretKey key = new SecretKeySpec(b, 0, b.length, keyAlgo);

		final HttpURLConnection con = HttpUtils.openConnection(new URL(String.format(Configuration.URLs.TSPEC, gv, hash)));
		con.setInstanceFollowRedirects(false);
		con.connect();
		r = con.getResponseCode();
		GoogleAnalytics.getInstance().pageview(pre, Integer.toString(r));
		if (r == HttpURLConnection.HTTP_OK) {
			final Cipher c;
			try {
				c = Cipher.getInstance(cipherAlgo);
				c.init(Cipher.DECRYPT_MODE, key);
			} catch (final GeneralSecurityException e) {
				throw new IOException(e);
			}
			try {
				return new ReflectionSpec(new CipherInputStream(HttpUtils.openStream(con), c));
			} catch (final NullPointerException e) {
				throw new IOException(e);
			}
		} else if (r == HttpURLConnection.HTTP_FORBIDDEN || r == HttpURLConnection.HTTP_NOT_FOUND) {
			throw new IOException(new IllegalStateException());
		}

		throw new IOException(new RuntimeException());
	}

	private static HttpURLConnection getBucketConnection(final String gv, final String hash) throws IOException {
		final HttpURLConnection b = HttpUtils.openConnection(new URL(String.format(Configuration.URLs.TSPEC_BUCKETS, hash)));
		b.addRequestProperty(String.format("x-%s-cv", Configuration.NAME.toLowerCase()), "201");
		b.addRequestProperty(String.format("x-%s-gv", Configuration.NAME.toLowerCase()), gv);
		return b;
	}

	public static void upload(final String gv, final String hash, final Map<String, byte[]> classes) throws IOException, PendingException {
		final int delay = 1000 * 60 * 3 + 30;
		final String pre = "loader/spec/" + hash;
		int r;

		final byte[] b = new byte[16];
		final String keyAlgo = "ARCFOUR", cipherAlgo = "RC4", hashAlgo = "SHA1";

		final MessageDigest md;
		try {
			md = MessageDigest.getInstance(hashAlgo);
		} catch (final NoSuchAlgorithmException ignored) {
			return;
		}

		md.update(StringUtils.getBytesUtf8(hash));
		System.arraycopy(md.digest(), 0, b, 0, b.length);
		final SecretKey key = new SecretKeySpec(b, 0, b.length, keyAlgo);

		final HttpURLConnection bucket = getBucketConnection(gv, hash);
		bucket.setInstanceFollowRedirects(false);
		bucket.connect();
		r = bucket.getResponseCode();
		GoogleAnalytics.getInstance().pageview(pre + "/bucket", Integer.toString(r));
		switch (bucket.getResponseCode()) {
		case HttpURLConnection.HTTP_SEE_OTHER:
			final String dest = bucket.getHeaderField("Location");
			final HttpURLConnection put = HttpUtils.openConnection(new URL(dest));
			put.setRequestMethod("PUT");
			put.setDoOutput(true);
			final Cipher c;
			try {
				c = Cipher.getInstance(cipherAlgo);
				c.init(Cipher.ENCRYPT_MODE, key);
			} catch (final GeneralSecurityException e) {
				throw new IOException(e);
			}
			final OutputStream out = new CipherOutputStream(new GZIPOutputStream(put.getOutputStream()), c);
			writePack(classes, out);
			out.flush();
			out.close();
			r = put.getResponseCode();
			put.disconnect();
			GoogleAnalytics.getInstance().pageview(pre + "/bucket/upload", Integer.toString(r));
			if (r == HttpURLConnection.HTTP_OK) {
				final HttpURLConnection bucket_notify = getBucketConnection(gv, hash);
				bucket_notify.setRequestMethod("PUT");
				bucket_notify.connect();
				final int r_notify = bucket_notify.getResponseCode();
				bucket_notify.disconnect();
				if (r_notify == HttpURLConnection.HTTP_OK || r_notify == HttpURLConnection.HTTP_ACCEPTED) {
					throw new PendingException(delay * 2);
				} else {
					throw new IOException("failed to upload");
				}
			} else {
				throw new IOException("could not start upload");
			}
		case HttpURLConnection.HTTP_ACCEPTED:
			throw new PendingException(delay);
		case HttpURLConnection.HTTP_BAD_REQUEST:
			GoogleAnalytics.getInstance().pageview(pre + "/bucket/failure", "");
			throw new IOException("bad request");
		}
	}

	public static void submit(final Logger log, final String gv, final String hash, final Map<String, byte[]> classes) {
		for (; ; ) {
			log.warning("Downloading update \u2014 please wait");
			try {
				LoaderUtils.upload(gv, hash, classes);
				break;
			} catch (final IOException ignored) {
			} catch (final LoaderUtils.PendingException p) {
				final int d = p.getDelay() / 1000;
				log.warning("Your update (" + hash.substring(0, 6) + ") is being processed, trying again in " + (d < 60 ? d + " seconds" : (int) Math.ceil(d / 60) + " minutes"));
				try {
					Thread.sleep(p.getDelay());
				} catch (final InterruptedException ignored) {
					break;
				}
			}
		}
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
		private static final long serialVersionUID = -6937383190630297216L;
		private final int delay;

		public PendingException(final int delay) {
			this.delay = delay;
		}

		public int getDelay() {
			return delay;
		}
	}
}
