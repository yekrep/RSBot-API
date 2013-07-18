package org.powerbot.bot.nloader;

import java.applet.Applet;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.powerbot.Configuration;
import org.powerbot.bot.Bot;
import org.powerbot.bot.loader.transform.TransformSpec;
import org.powerbot.client.Client;
import org.powerbot.util.StringUtil;
import org.powerbot.util.Tracker;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;

public class NRSLoader implements Runnable {
	private final Bot bot;
	private final GameLoader gameLoader;
	private final ClassLoader classLoader;
	private Runnable callback;
	private Applet applet;
	private AbstractBridge bridge;

	public NRSLoader(Bot bot, GameLoader gameLoader, ClassLoader classLoader) {
		this.bot = bot;
		this.gameLoader = gameLoader;
		this.classLoader = classLoader;
	}

	public void setCallback(Runnable callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		Class<?> code;
		try {
			code = classLoader.loadClass("Rs2Applet");
		} catch (ClassNotFoundException e) {
			code = null;
		}
		if (code == null || !(Applet.class.isAssignableFrom(code))) {
			return;
		}
		try {
			Constructor<?> constructor = code.getConstructor((Class[]) null);
			this.applet = (Applet) constructor.newInstance((Object[]) null);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
			this.applet = null;
		}
		if (applet == null || !(Application.class.isAssignableFrom(code))) {
			return;
		}
		byte[] pack = gameLoader.getResources().get("inner.pack.gz");
		if (pack == null) {
			return;
		}
		String hash;
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-1");
			hash = StringUtil.byteArrayToHexString(digest.digest(pack));
		} catch (NoSuchAlgorithmException ignored) {
			hash = null;
		}
		if (hash == null) {
			return;
		}
		TransformSpec spec;
		try {
			spec = getSpec(hash);
		} catch (IOException ignored) {
			spec = null;
		}

		((Application) applet).setBridge(bridge = new AbstractBridge(spec) {
			@Override
			public void instance(Object object) {
				bot.setClient((Client) object);
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

	public AbstractBridge getBridge() {
		return bridge;
	}

	private TransformSpec getSpec(String packHash) throws IOException {
		final String pre = "loader/spec/" + packHash;
		int r;

		final byte[] b = new byte[16];
		final String keyAlgo = "ARCFOUR", cipherAlgo = "RC4", hashAlgo = "SHA1";

		final MessageDigest md;
		try {
			md = MessageDigest.getInstance(hashAlgo);
		} catch (final NoSuchAlgorithmException ignored) {
			return null;
		}

		md.update(StringUtil.getBytesUtf8(packHash));
		System.arraycopy(md.digest(), 0, b, 0, b.length);
		final SecretKey key = new SecretKeySpec(b, 0, b.length, keyAlgo);

		final HttpURLConnection con = HttpClient.getHttpConnection(new URL(String.format(Configuration.URLs.CLIENTPATCH, packHash)));
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
			return new TransformSpec(IOHelper.read(new CipherInputStream(HttpClient.getInputStream(con), c)));
		}
		return null;
	}
}
