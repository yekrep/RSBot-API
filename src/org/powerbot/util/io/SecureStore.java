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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
			synchronized (entries) {
				entries.put(entry.name, entry);
			}
			raf.skipBytes(getBlockSize(entry.length));
		}
		raf.close();
	}

	public List<TarEntry> listEntries() {
		final List<TarEntry> list = new ArrayList<TarEntry>();
		synchronized (entries) {
			for (final TarEntry entry : entries.values()) {
				list.add(entry);
			}
		}
		return list;
	}

	public TarEntry get(final String name) {
		final TarEntry entry;
		synchronized (entries) {
			entry = entries.containsKey(name) ? entries.get(name) : null;
		}
		return entry;
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

	private synchronized void remove(final TarEntry cache) throws IOException, GeneralSecurityException {
		if (cache == null) {
			return;
		}
		synchronized (entries) {
			entries.remove(cache);
		}
		final RandomAccessFile raf = new RandomAccessFile(store, "rw");
		final long z = cache.position;
		raf.seek(z);
		raf.skipBytes(TarEntry.BLOCKSIZE + getBlockSize(cache.length));
		final long s = raf.length() - raf.getFilePointer();
		if (s == 0) {
			raf.setLength(z);
			return;
		}
		final byte[] trailing = new byte[(int) s];
		raf.read(trailing);
		raf.seek(z);
		raf.write(trailing);
		raf.setLength(z + trailing.length);
		raf.seek(z);
		final byte[] header = new byte[TarEntry.BLOCKSIZE];
		while (raf.read(header) != -1) {
			final long position = raf.getFilePointer() - header.length;
			final InputStream cis = getCipherInputStream(new ByteArrayInputStream(header), Cipher.DECRYPT_MODE);
			final TarEntry entry = TarEntry.read(cis);
			synchronized (entries) {
				entries.get(entry.name).position = position;
			}
			raf.skipBytes(getBlockSize(entry.length));
		}
	}

	public synchronized void write(final String name, final byte[] data) throws IOException, GeneralSecurityException {
		final TarEntry cache = get(name);
		final int[] l = {getBlockSize(data.length), cache == null ? -1 : getBlockSize(cache.length)};
		if (l[0] > l[1]) {
			write(name, new ByteArrayInputStream(data));
			return;
		}
		final RandomAccessFile raf = new RandomAccessFile(store, "rw");
		raf.seek(cache.position + TarEntry.BLOCKSIZE);
		final InputStream is = getCipherInputStream(new ByteArrayInputStream(data), Cipher.ENCRYPT_MODE);
		final byte[] encrypted = new byte[data.length];
		is.read(encrypted);
		is.close();
		raf.write(encrypted);
		final int z = l[1] - data.length;
		if (z != 0) {
			final byte[] empty = new byte[z];
			new SecureRandom().nextBytes(empty);
			raf.write(empty);
		}
		raf.close();
		cache.length = data.length;
	}

	public synchronized void write(final String name, InputStream is) throws IOException, GeneralSecurityException {
		final TarEntry cache = get(name);
		remove(cache);
		final RandomAccessFile raf = new RandomAccessFile(store, "rw");
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
			final int p = l < TarEntry.BLOCKSIZE ? TarEntry.BLOCKSIZE - l : getBlockSize(l) - l;
			raf.write(empty, 0, p);
			raf.seek(z);
			final TarEntry entry = new TarEntry();
			entry.name = name;
			entry.length = l;
			entry.position = z;
			raf.write(cryptBlock(Arrays.copyOf(entry.getBytes(), TarEntry.BLOCKSIZE), Cipher.ENCRYPT_MODE));
			synchronized (entries) {
				if (entries.containsKey(entry.name)) {
					entries.get(entry.name).position = z;
				} else {
					entries.put(entry.name, entry);
				}
			}
		}
		raf.close();
	}

	public void delete(final String name) throws IOException, GeneralSecurityException {
		write(name, null);
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

	private int getBlockSize(final long len) {
		return (int) Math.ceil((double) len / TarEntry.BLOCKSIZE) * TarEntry.BLOCKSIZE;
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
