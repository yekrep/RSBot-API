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
	public static final Map<File, Class<?>[]> PERMISSIONS = new HashMap<>();
	private static final long TIMESTAMP = 1346067355497L, GCTIME = 1000 * 60 * 60 * 24 * 3;
	private static volatile SecretKey key;

	private final File store;

	public CryptFile(final String id, final Class<?>... parents) {
		store = getSecureFile(id);
		PERMISSIONS.put(store, parents);
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
		Cipher c;
		try {
			c = getCipher(Cipher.DECRYPT_MODE);
		} catch (final GeneralSecurityException ignored) {
			ignored.printStackTrace();
			throw new IOException();
		}
		return new CipherInputStream(new FileInputStream(store), c);
	}

	public OutputStream getOutputStream() throws IOException {
		Cipher c;
		try {
			c = getCipher(Cipher.ENCRYPT_MODE);
		} catch (final GeneralSecurityException ignored) {
			throw new IOException();
		}
		return new CipherOutputStream(new FileOutputStream(store), c);
	}

	private static synchronized Cipher getCipher(final int opmode) throws GeneralSecurityException {
		final String KEY_ALGO = "ARCFOUR", CIPHER_ALGO = "RC4";
		final Cipher c = Cipher.getInstance(CIPHER_ALGO);

		if (key != null) {
			c.init(opmode, key);
			return c;
		}

		final File keyfile = getSecureFile("secret.key");
		final int p = 4096;

		if (keyfile.isFile() && keyfile.length() != 0L) {
			InputStream in = null;
			try {
				in = new InflaterInputStream(new FileInputStream(keyfile), new Inflater(true));
				in.skip(p);
				byte[] buf = new byte[4];
				in.read(buf, 0, buf.length);
				final int l = buf[0] | (buf[1] << 8) | (buf[2] << 16) | (buf[3] << 24);
				buf = new byte[l];
				in.read(buf, 0, l);
				c.init(opmode, key = new SecretKeySpec(buf, 0, buf.length, KEY_ALGO));
			} catch (final IOException ignored) {
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (final IOException ignored) {
					}
				}
			}
		} else {
			DeflaterOutputStream out = null;
			try {
				out = new DeflaterOutputStream(new FileOutputStream(keyfile), new Deflater(1, true));
				final SecureRandom r = new SecureRandom();
				byte[] buf = new byte[p];
				r.nextBytes(buf);
				out.write(buf);
				final KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGO);
				final SecretKey k = kg.generateKey();
				c.init(opmode, key = k);
				buf = k.getEncoded();
				for (int i = 0; i < 4; i++) {
					out.write((buf.length >> (i << 3)) & 0xff);
				}
				out.write(buf);
				buf = new byte[128 + r.nextInt(8096)];
				r.nextBytes(buf);
				out.write(buf);
			} catch (final IOException ignored) {
			} finally {
				if (out != null) {
					try {
						out.finish();
						out.close();
					} catch (final IOException ignored) {
					}
				}
			}
		}

		return c;
	}

	private static File getSecureFile(final String id) {
		final long uid = Configuration.getUID();
		String hash;

		final String[] parts = id.split("/", 2);
		final String name = parts[parts.length == 2 ? 1 : 0], dir = parts.length == 2 ? parts[0] : "";

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
			hash = getCrc32(name, uid);
		}

		if (hash == null) {
			hash = Integer.toHexString(name.hashCode());
		}

		final File file;

		if (dir.length() != 0) {
			gc(dir, uid);
			final File parent = getSecureDirName(dir, uid);
			if (!parent.isDirectory()) {
				parent.mkdirs();
			}
			file = new File(parent, hash);
		} else {
			file = new File(getRoot(), hash);
		}

		if (file.isFile() && file.lastModified() < TIMESTAMP) {
			file.delete();
		}

		return file;
	}

	private static File getSecureDirName(final String dir, final long uid) {
		return new File(getRoot(), getCrc32(dir, uid));
	}

	private static File getRoot() {
		return new File(System.getProperty("java.io.tmpdir"));
	}

	private static String getCrc32(final String s, final long uid) {
		final Adler32 c = new Adler32();
		c.update(StringUtil.getBytesUtf8(s));
		c.update((int) (uid & 0xffffffff));
		c.update((int) (uid >> 32));
		final long l = c.getValue();
		return Long.toHexString(l);
	}

	private static void gc(final String dir, final long uid) {
		final File file = getSecureDirName(dir, uid);

		if (!file.isDirectory()) {
			return;
		}

		for (final File f : file.listFiles()) {
			try {
				final long a = Files.readAttributes(f.toPath(), BasicFileAttributes.class).lastAccessTime().toMillis();
				if (a < System.currentTimeMillis() - GCTIME) {
					f.delete();
				}
			} catch (final IOException ignored) {
			}
		}
	}
}
