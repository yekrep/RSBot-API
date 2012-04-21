package org.powerbot.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;

/**
 * @author Paris
 */
public final class SecureStore {
	private final static Logger log = Logger.getLogger(SecureStore.class.getName());
	private final static SecureStore instance = new SecureStore();
	private final static int MAGIC = 0x00525354, VERSION = 1005, BLOCKSIZE = 512, MAXBLOCKS = 2048;
	private final static String CIPHER_ALGORITHM = "XOR", KEY_ALGORITHM = "RC4";
	private final File store;
	private final Map<String, TarEntry> entries;
	private byte[] key;

	private SecureStore() {
		store = new File(Configuration.STORE);
		entries = new HashMap<String, TarEntry>();
		if (!exists()) {
			log.warning("Creating new secure store");
			try {
				create();
			} catch (final IOException ignored) {
			}
		}
	}

	public static SecureStore getInstance() {
		return instance;
	}

	public String getPrivateKey() {
		return StringUtil.byteArrayToHexString(key);
	}

	private boolean exists() {
		if (!store.exists()) {
			return false;
		}
		try {
			read();
			return true;
		} catch (final IOException ignored) {
		} catch (final GeneralSecurityException ignored) {
		}
		return false;
	}

	private synchronized void create() throws IOException {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (final NoSuchAlgorithmException ignored) {
		}
		final RandomAccessFile raf = new RandomAccessFile(store, "rw");
		raf.setLength(0);
		raf.writeInt(MAGIC);
		raf.writeInt(VERSION);
		final SecureRandom s = new SecureRandom();
		final int blocks = MAXBLOCKS + s.nextInt(MAXBLOCKS / 2);
		raf.writeInt(blocks);
		for (int i = 0; i < blocks; i++) {
			final byte[] payload = new byte[BLOCKSIZE];
			s.nextBytes(payload);
			md.update(payload);

			raf.write(payload);
			s.nextBytes(payload);
			raf.write(payload);
		}
		raf.getFilePointer();
		raf.close();
		key = md.digest();
	}

	private synchronized void read() throws IOException, GeneralSecurityException {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (final NoSuchAlgorithmException ignored) {
		}
		final RandomAccessFile raf = new RandomAccessFile(store, "r");
		if (raf.readInt() != MAGIC || raf.readInt() != VERSION) {
			throw new IOException();
		}
		final int blocks = raf.readInt();
		for (int i = 0; i < blocks; i++) {
			final byte[] payload = new byte[BLOCKSIZE];
			raf.read(payload);
			md.update(payload);
			raf.skipBytes(payload.length);
		}
		raf.getFilePointer();
		key = md.digest();
		final byte[] header = new byte[TarEntry.BLOCKSIZE];
		while (raf.read(header) != -1) {
			final long position = raf.getFilePointer() - header.length;
			final InputStream cis = getCipherInputStream(new ByteArrayInputStream(header), Cipher.DECRYPT_MODE);
			final TarEntry entry = TarEntry.read(cis);
			entry.position = position;
			entries.put(entry.name, entry);
			final int l = (int) Math.ceil((double) entry.length / TarEntry.BLOCKSIZE) * TarEntry.BLOCKSIZE;
			raf.skipBytes(l);
		}
		raf.close();
	}

	public Collection<TarEntry> listEntries() {
		synchronized (entries) {
			return entries.values();
		}
	}

	public TarEntry get(final String name) {
		synchronized (entries) {
			return entries.containsKey(name) ? entries.get(name) : null;
		}
	}

	public synchronized InputStream read(final String name) throws IOException, GeneralSecurityException {
		final TarEntry entry = get(name);
		if (entry == null) {
			return null;
		}
		final RandomAccessFile raf = new RandomAccessFile(store, "r");
		raf.seek(entry.position + TarEntry.BLOCKSIZE);
		final byte[] data = new byte[(int) entry.length];
		raf.read(data);
		raf.close();
		return getCipherInputStream(new ByteArrayInputStream(data), Cipher.DECRYPT_MODE);
	}

	public synchronized void write(final String name, InputStream is) throws IOException, GeneralSecurityException {
		final RandomAccessFile raf = new RandomAccessFile(store, "rw");
		final TarEntry cache = get(name);
		if (cache != null) {
			raf.seek(cache.position);
			final int l = (int) Math.ceil((double) cache.length / TarEntry.BLOCKSIZE) * TarEntry.BLOCKSIZE;
			raf.skipBytes(TarEntry.BLOCKSIZE + l);
			final byte[] trailing = new byte[(int) (raf.length() - raf.getFilePointer())];
			if (trailing.length != 0) {
				raf.read(trailing);
			}
			raf.seek(cache.position);
			if (trailing.length != 0) {
				raf.write(trailing);
			}
			raf.setLength(cache.position + trailing.length);
		}
		raf.seek(raf.length());
		if (is != null && is.available() > 0) {
			is = getCipherInputStream(is, Cipher.ENCRYPT_MODE);
			final byte[] empty = new byte[TarEntry.BLOCKSIZE];
			new SecureRandom().nextBytes(empty);
			final long z = raf.getFilePointer();
			raf.write(empty);
			int l = 0, b;
			final byte[] data = new byte[IOHelper.BUFFER_SIZE];
			while ((b = is.read(data)) != -1) {
				raf.write(data, 0, b);
				l += b;
			}
			is.close();
			final int p = l < TarEntry.BLOCKSIZE ? TarEntry.BLOCKSIZE - l : (int) Math.ceil((double) l / TarEntry.BLOCKSIZE) * TarEntry.BLOCKSIZE - l;
			raf.write(empty, 0, p);
			raf.seek(z);
			final TarEntry entry = new TarEntry();
			entry.name = name;
			entry.length = l;
			entry.position = z;
			raf.write(cryptBlock(Arrays.copyOf(entry.getBytes(), TarEntry.BLOCKSIZE), Cipher.ENCRYPT_MODE));
			entries.put(entry.name, entry);
		} else if (cache != null) {
			entries.remove(name);
		}
		raf.close();
	}

	private byte[] cryptBlock(final byte[] in, final int opmode) throws GeneralSecurityException, IOException {
		final InputStream is = getCipherInputStream(new ByteArrayInputStream(in), opmode);
		final byte[] out = new byte[in.length];
		is.read(out);
		is.close();
		return out;
	}

	private FilterInputStream getCipherInputStream(final InputStream is, final int opmode) throws GeneralSecurityException {
		if (CIPHER_ALGORITHM.equals("XOR")) {
			return new XORInputStream(is, this.key, opmode);
		} else {
			final Cipher c = Cipher.getInstance(CIPHER_ALGORITHM);
			final byte[] key = Arrays.copyOf(this.key, 16);
			final SecretKeySpec sks = new SecretKeySpec(key, KEY_ALGORITHM);
			c.init(opmode, sks);
			return new CipherInputStream(is, c);
		}
	}

	public void download(final String name, final URL url) throws IOException, GeneralSecurityException {
		final TarEntry entry = get(name);
		if (entry != null) {
			if (entry.modified <= HttpClient.getLastModified(url)) {
				return;
			}
		}
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOHelper.write(HttpClient.openStream(url), bos);
		write(name, new ByteArrayInputStream(bos.toByteArray()));
	}
}
