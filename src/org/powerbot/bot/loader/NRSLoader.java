package org.powerbot.bot.loader;

import java.applet.Applet;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.powerbot.Configuration;
import org.powerbot.bot.loader.transform.TransformSpec;
import org.powerbot.misc.Tracker;
import org.powerbot.util.HttpUtils;
import org.powerbot.util.StringUtils;

public class NRSLoader implements Runnable {
	private final GameLoader gameLoader;
	private final ClassLoader classLoader;
	private Runnable callback;
	private Applet applet;
	private AbstractBridge bridge;
	private Object client;
	private String packHash;

	public NRSLoader(final GameLoader gameLoader, final ClassLoader classLoader) {
		this.gameLoader = gameLoader;
		this.classLoader = classLoader;
	}

	public void setCallback(final Runnable callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		Class<?> code;
		try {
			code = classLoader.loadClass("Rs2Applet");
		} catch (final ClassNotFoundException e) {
			code = null;
		}
		if (code == null || !(Applet.class.isAssignableFrom(code))) {
			return;
		}
		try {
			final Constructor<?> constructor = code.getConstructor((Class[]) null);
			this.applet = (Applet) constructor.newInstance((Object[]) null);
		} catch (final Exception ignored) {
			this.applet = null;
		}
		if (applet == null || !(Application.class.isAssignableFrom(code))) {
			return;
		}
		final byte[] pack = gameLoader.getResources().get("inner.pack.gz");
		if (pack == null) {
			return;
		}
		final MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-1");
			packHash = StringUtils.byteArrayToHexString(digest.digest(pack));
		} catch (final NoSuchAlgorithmException ignored) {
			packHash = null;
		}
		if (packHash == null) {
			return;
		}
		TransformSpec spec;
		try {
			spec = getSpec(packHash);
		} catch (final IOException ignored) {
			spec = null;
		}
		if (spec != null) {
			spec.adapt();
		}

		((Application) applet).setBridge(bridge = new AbstractBridge(spec) {
			@Override
			public void instance(final Object client) {
				NRSLoader.this.client = client;
			}
		});
		callback.run();
	}

	public GameLoader getGameLoader() {
		return gameLoader;
	}

	public Applet getApplet() {
		return applet;
	}

	public Object getClient() {
		return client;
	}

	public String getPackHash() {
		return packHash;
	}

	public AbstractBridge getBridge() {
		return bridge;
	}

	private TransformSpec getSpec(final String packHash) throws IOException {
		final String pre = "loader/spec/" + packHash;
		final int r;

		final byte[] b = new byte[16];
		final String keyAlgo = "ARCFOUR", cipherAlgo = "RC4", hashAlgo = "SHA1";

		final MessageDigest md;
		try {
			md = MessageDigest.getInstance(hashAlgo);
		} catch (final NoSuchAlgorithmException e) {
			throw new IOException(e);
		}

		md.update(StringUtils.getBytesUtf8(packHash));
		System.arraycopy(md.digest(), 0, b, 0, b.length);
		final SecretKey key = new SecretKeySpec(b, 0, b.length, keyAlgo);

		final HttpURLConnection con = HttpUtils.getHttpConnection(new URL(String.format(Configuration.URLs.CLIENTPATCH, packHash)));
		con.setInstanceFollowRedirects(false);
		con.connect();
		r = con.getResponseCode();
		Tracker.getInstance().trackPage(pre, Integer.toString(r));
		if (r == HttpURLConnection.HTTP_OK) {
			final Cipher c;
			try {
				c = Cipher.getInstance(cipherAlgo);
				c.init(Cipher.DECRYPT_MODE, key);
			} catch (final GeneralSecurityException e) {
				throw new IOException(e);
			}
			try {
				return new TransformSpec(new CipherInputStream(HttpUtils.getInputStream(con), c));
			} catch (final NullPointerException e) {
				throw new IOException(e);
			}
		}

		throw new IOException(new IllegalStateException());
	}

	public void upload(final String packHash) throws IOException, PendingException {
		final int delay = 1000 * 60 * 3 + 30;
		final String pre = "loader/spec/" + packHash;
		int r;

		final byte[] b = new byte[16];
		final String keyAlgo = "ARCFOUR", cipherAlgo = "RC4", hashAlgo = "SHA1";

		final MessageDigest md;
		try {
			md = MessageDigest.getInstance(hashAlgo);
		} catch (final NoSuchAlgorithmException ignored) {
			return;
		}

		md.update(StringUtils.getBytesUtf8(packHash));
		System.arraycopy(md.digest(), 0, b, 0, b.length);
		final SecretKey key = new SecretKeySpec(b, 0, b.length, keyAlgo);

		final HttpURLConnection bucket = HttpUtils.getHttpConnection(new URL(String.format(Configuration.URLs.CLIENTBUCKET, packHash)));
		bucket.addRequestProperty(String.format("x-%s-cv", Configuration.NAME.toLowerCase()), "101");
		bucket.setInstanceFollowRedirects(false);
		bucket.connect();
		r = bucket.getResponseCode();
		Tracker.getInstance().trackPage(pre + "/bucket", Integer.toString(r));
		switch (bucket.getResponseCode()) {
		case HttpURLConnection.HTTP_SEE_OTHER:
			final String dest = bucket.getHeaderField("Location");
			final HttpURLConnection put = HttpUtils.getHttpConnection(new URL(dest));
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
			writePack(getBridge().loaded, out);
			out.flush();
			out.close();
			r = put.getResponseCode();
			put.disconnect();
			Tracker.getInstance().trackPage(pre + "/bucket/upload", Integer.toString(r));
			if (r == HttpURLConnection.HTTP_OK) {
				final HttpURLConnection bucket_notify = HttpUtils.getHttpConnection(new URL(String.format(Configuration.URLs.CLIENTBUCKET, packHash)));
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
			Tracker.getInstance().trackPage(pre + "/bucket/failure", "");
			throw new IOException("bad request");
		}
	}

	private void writePack(final Map<String, byte[]> classes, final OutputStream out) throws IOException {
		final int MAGIC = 0xC1A5700F, END_OF_FILE = 0x1;
		writeInt(MAGIC, out);

		for (final byte[] data : classes.values()) {
			writeInt(data.length, out);
			out.write(data);
		}

		writeInt(END_OF_FILE, out);
	}

	private void writeInt(final int v, final OutputStream o) throws IOException {
		final int m = 0xff;
		o.write(v >>> 24);
		o.write(v >>> 16 & m);
		o.write(v >>> 8 & m);
		o.write(v & m);
	}

	public final class PendingException extends Exception {
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
