package org.powerbot.util.io;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.Adler32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;

/**
 * @author Paris
 */
public final class CryptFile {
	public static final Map<File, Class<?>[]> PERMISSIONS = new ConcurrentHashMap<>();
	private static final long VECTOR = 0x9e3779b9, TIMESTAMP = 1346067355497L, GCTIME = 1000 * 60 * 60 * 24 * 3;
	private static final String KEY_ALGO = "ARCFOUR", CIPHER_ALGO = "RC4";
	private final SecretKey key;
	private final File root, store;

	public CryptFile(final String name, final Class<?>... parents) {
		this(name, false);
	}

	public CryptFile(final String name, final boolean temp, final Class<?>... parents) {
		root = temp ? Configuration.TEMP : Configuration.HOME;
		store = getSecureFile(name);
		PERMISSIONS.put(store, parents);

		int k = (int) ((Configuration.getUID() ^ VECTOR) & Integer.MAX_VALUE);
		final byte[] b = new byte[4];
		for (int i = 0; i < b.length; i++, k >>>= 8) {
			b[i] = (byte) (k & 0xff);
		}
		key = new SecretKeySpec(b, 0, b.length, KEY_ALGO);
	}

	public boolean exists() {
		return store.isFile() && store.length() != 0L;
	}

	public void delete() {
		store.delete();
	}

	public InputStream download(final URL url) throws IOException {
		return download(HttpClient.getHttpConnection(url));
	}

	public InputStream download(final HttpURLConnection con) throws IOException {
		if (exists()) {
			try {
				con.setIfModifiedSince(store.lastModified());
			} catch (final IllegalStateException ignored) {
			}
		}

		if (con.getResponseCode() != HttpURLConnection.HTTP_NOT_MODIFIED &&
				(exists() ? con.getLastModified() > store.lastModified() : true)) {
			IOHelper.write(HttpClient.getInputStream(con), getOutputStream());
		}

		con.disconnect();
		return getInputStream();
	}

	public InputStream getInputStream() throws IOException {
		if (!store.isFile()) {
			throw new FileNotFoundException(store.toString());
		}
		Files.getFileAttributeView(store.toPath(), BasicFileAttributeView.class).setTimes(null, FileTime.fromMillis(System.currentTimeMillis()), null);
		if (store.length() == 0L) {
			return new ByteArrayInputStream(new byte[]{});
		}
		final Cipher c;
		try {
			c = Cipher.getInstance(CIPHER_ALGO);
			c.init(Cipher.ENCRYPT_MODE, key);
		} catch (final GeneralSecurityException e) {
			throw new IOException(e);
		}
		return new CipherInputStream(new FileInputStream(store), c);
	}

	public OutputStream getOutputStream() throws IOException {
		final Cipher c;
		try {
			c = Cipher.getInstance(CIPHER_ALGO);
			c.init(Cipher.DECRYPT_MODE, key);
		} catch (final GeneralSecurityException e) {
			throw new IOException(e);
		}
		return new CipherOutputStream(new FileOutputStream(store), c);
	}

	private File getSecureFile(final String name) {
		final long uid = Configuration.getUID();
		String hash;

		final MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(StringUtil.getBytesUtf8(name));
			for (int i = 0; i < 8; i++) {
				md.update((byte) ((uid >> (i << 3)) & 0xff));
			}
			hash = StringUtil.newStringUtf8(Base64.encode(md.digest()));
			hash = "etilqs_" + hash.replaceAll("[^A-Za-z0-0]", "").substring(0, 15);
		} catch (final NoSuchAlgorithmException ignored) {
			final Adler32 c = new Adler32();
			c.update(StringUtil.getBytesUtf8(name));
			c.update((int) (uid & Integer.MAX_VALUE));
			c.update((int) (uid >> 32));
			hash = Long.toHexString(c.getValue());
		}

		final File file = new File(root, hash);

		if (file.isFile() && file.lastModified() < TIMESTAMP) {
			file.delete();
		}

		return file;
	}

	private static void gc() {
		final File root = Configuration.TEMP;

		if (!root.isDirectory()) {
			return;
		}

		for (final File file : root.listFiles()) {
			try {
				final long atime = Files.readAttributes(file.toPath(), BasicFileAttributes.class).lastAccessTime().toMillis();
				if (atime < System.currentTimeMillis() - GCTIME) {
					file.delete();
				}
			} catch (final IOException ignored) {
			}
		}
	}
}
