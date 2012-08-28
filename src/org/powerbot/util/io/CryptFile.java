package org.powerbot.util.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
	public static final Map<File, Class<?>[]> PERMISSIONS = new HashMap<File, Class<?>[]>();
	private static final long TIMESTAMP = 1346067355497L;
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
		final HttpURLConnection con = HttpClient.getHttpConnection(url);

		if (exists()) {
			con.setIfModifiedSince(store.lastModified());
		}

		if (con.getResponseCode() != HttpURLConnection.HTTP_NOT_MODIFIED) {
			IOHelper.write(HttpClient.getInputStream(con), getOutputStream());
		}

		con.disconnect();
		return getInputStream();
	}

	public InputStream getInputStream() throws IOException {
		if (!store.isFile()) {
			throw new FileNotFoundException(store.toString());
		}
		if (store.length() == 0L) {
			return new ByteArrayInputStream(new byte[] {});
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
		String hash = null;

		final MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(StringUtil.getBytesUtf8(id));
			for (int i = 0; i < 8; i++) {
				md.update((byte) ((uid >> (i << 3)) & 0xff));
			}
			hash = StringUtil.byteArrayToHexString(md.digest()).substring(0, 6);
		} catch (final NoSuchAlgorithmException ignored) {
			final Adler32 c = new Adler32();
			c.update(StringUtil.getBytesUtf8(id));
			c.update((int) (uid & 0xffffffff));
			c.update((int) (uid >> 32));
			final long l = c.getValue();
			hash = Long.toHexString(l);
		}

		if (hash == null) {
			hash = Integer.toHexString(id.hashCode());
		}

		final String name = hash + ".tmp";
		final File file = new File(System.getProperty("java.io.tmpdir"), name);

		if (file.isFile() && file.lastModified() < TIMESTAMP) {
			file.delete();
		}

		return file;
	}
}
