package org.powerbot.util.io;

import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Paris
 */
public final class SecureStore {
	private final static Logger log = Logger.getLogger(SecureStore.class.getName());
	private final static SecureStore instance = new SecureStore();
	private final static int MAGIC = 0x00525354, VERSION = 1001, BLOCKSIZE = 512, MAXBLOCKS = 2048;
	private final static String CIPHER_ALGORITHM = "RC4", KEY_ALGORITHM = "RC4";
	private final File store;
	private long offset;
	private byte[] key;

	private SecureStore() {
		store = new File(Configuration.STORE);
		if (!store.exists()) {
			try {
				create();
			} catch (final IOException ignored) {
			}
		}
		try {
			read();
		} catch (final IOException ignored) {
			log.severe("Store corrupt, attempting to recreate");
			try {
				create();
			} catch (final IOException ignored2) {
			}
		}
	}

	public static SecureStore getInstance() {
		return instance;
	}

	public String getPrivateKey() {
		return StringUtil.byteArrayToHexString(key);
	}

	private synchronized void create() throws IOException {
		final RandomAccessFile raf = new RandomAccessFile(store, "rw");
		raf.writeInt(MAGIC);
		raf.writeInt(VERSION);
		final SecureRandom s = new SecureRandom();
		final int blocks = MAXBLOCKS + s.nextInt(MAXBLOCKS / 2);
		raf.writeInt(blocks);
		for (int i = 0; i < blocks; i++) {
			final byte[] payload = new byte[BLOCKSIZE];
			for (int j = 0; j < 2; j++) {
				s.nextBytes(payload);
				raf.write(payload);
			}
		}
		raf.close();
	}

	private synchronized void read() throws IOException {
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
		offset = raf.getFilePointer();
		raf.close();
		key = md.digest();
	}

	public synchronized TarEntry get(final String name) throws IOException, GeneralSecurityException {
		final RandomAccessFile raf = new RandomAccessFile(store, "r");
		raf.seek(offset);
		final byte[] header = new byte[TarEntry.BLOCKSIZE];
		while (raf.read(header) != -1) {
			final CipherInputStream cis = new CipherInputStream(new ByteArrayInputStream(header), getCipher(Cipher.DECRYPT_MODE));
			final TarEntry entry = TarEntry.read(cis);
			int l = (int) Math.ceil((double) entry.length / TarEntry.BLOCKSIZE) * TarEntry.BLOCKSIZE;
			if (name.equals(entry.name)) {
				raf.close();
				return entry;
			} else {
				raf.skipBytes(l);
			}
		}
		raf.close();
		return null;
	}

	public synchronized InputStream read(final String name) throws IOException, GeneralSecurityException {
		final RandomAccessFile raf = new RandomAccessFile(store, "r");
		raf.seek(offset);
		final byte[] header = new byte[TarEntry.BLOCKSIZE];
		while (raf.read(header) != -1) {
			final CipherInputStream cis = new CipherInputStream(new ByteArrayInputStream(header), getCipher(Cipher.DECRYPT_MODE));
			final TarEntry entry = TarEntry.read(cis);
			int l = (int) Math.ceil((double) entry.length / TarEntry.BLOCKSIZE) * TarEntry.BLOCKSIZE;
			if (name.equals(entry.name)) {
				final byte[] data = new byte[(int) entry.length];
				raf.read(data);
				raf.close();
				return new CipherInputStream(new ByteArrayInputStream(data), getCipher(Cipher.DECRYPT_MODE));
			} else {
				raf.skipBytes(l);
			}
		}
		raf.close();
		return null;
	}

	public synchronized void write(final String name, InputStream is) throws IOException, GeneralSecurityException {
		final RandomAccessFile raf = new RandomAccessFile(store, "rw");
		raf.seek(offset);
		final byte[] header = new byte[TarEntry.BLOCKSIZE];
		while (raf.read(header) != -1) {
			if (header[0] == 0) {
				continue;
			}
			final CipherInputStream cis = new CipherInputStream(new ByteArrayInputStream(header), getCipher(Cipher.DECRYPT_MODE));
			final TarEntry entry = TarEntry.read(cis);
			int l = (int) Math.ceil((double) entry.length / TarEntry.BLOCKSIZE) * TarEntry.BLOCKSIZE;
			if (name.equals(entry.name)) {
				final long z = raf.getFilePointer();
				raf.skipBytes(l);
				final byte[] trailing = new byte[(int) (raf.length() - raf.getFilePointer())];
				if (trailing.length != 0) {
					raf.read(trailing);
				}
				raf.seek(z - header.length);
				if (trailing.length != 0) {
					raf.write(trailing);
				}
				raf.setLength(z - header.length + trailing.length);
			} else {
				raf.skipBytes(l);
			}
		}
		if (is != null) {
			is = new CipherInputStream(is, getCipher(Cipher.ENCRYPT_MODE));
			final byte[] empty = new byte[TarEntry.BLOCKSIZE];
			final long z = raf.getFilePointer();
			raf.write(empty);
			int l = 0, b;
			final byte[] data = new byte[IOHelper.BUFFER_SIZE];
			while ((b = is.read(data)) != -1) {
				raf.write(data, 0, b);
				l += b;
			}
			final int p = l < TarEntry.BLOCKSIZE ? TarEntry.BLOCKSIZE - l : (int) Math.ceil((double) l / TarEntry.BLOCKSIZE) * TarEntry.BLOCKSIZE - l;
			raf.write(empty, 0, p);
			raf.seek(z);
			final TarEntry entry = new TarEntry();
			entry.name = name;
			entry.length = l;
			final Cipher c = getCipher(Cipher.ENCRYPT_MODE);
			final byte[] raw = entry.getBytes(), crypt = new byte[raw.length];
			c.update(raw, 0, raw.length, crypt, 0);
			c.doFinal();
			raf.write(crypt);
		}
		raf.close();
	}

	private Cipher getCipher(final int opmode) throws GeneralSecurityException {
		final Cipher c = Cipher.getInstance(CIPHER_ALGORITHM);
		final byte[] key = Arrays.copyOf(this.key, 16);
		final SecretKeySpec sks = new SecretKeySpec(key, KEY_ALGORITHM);
		c.init(opmode, sks);
		return c;
	}
}
