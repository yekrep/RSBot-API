package org.powerbot.util.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Logger;

import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;

/**
 * @author Paris
 */
public final class SecureStore {
	private final static Logger log = Logger.getLogger(SecureStore.class.getName());
	private final static SecureStore instance = new SecureStore();
	private final static int MAGIC = 0x00525354, VERSION = 1, BLOCKSIZE = 512, MAXBLOCKS = 2048;
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
	
	private void create() throws IOException {
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

	private void read() throws IOException {
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

	public InputStream read(final String name) throws IOException {
		final RandomAccessFile raf = new RandomAccessFile(store, "r");
		raf.seek(offset);
		final byte[] header = new byte[TarEntry.BLOCKSIZE];
		while (raf.read(header) != -1) {
			final TarEntry entry = TarEntry.read(header);
			int l = (int) Math.ceil((double) entry.length / TarEntry.BLOCKSIZE) * TarEntry.BLOCKSIZE;
			if (name.equals(entry.name)) {
				final byte[] data = new byte[(int) entry.length];
				raf.read(data);
				raf.close();
				return new ByteArrayInputStream(data);
			} else {
				raf.skipBytes(l);
			}
		}
		raf.close();
		return null;
	}

	public void write(final String name, final InputStream is) throws IOException {
		final RandomAccessFile raf = new RandomAccessFile(store, "rw");
		raf.seek(offset);
		final byte[] header = new byte[TarEntry.BLOCKSIZE];
		while (raf.read(header) != -1) {
			if (header[0] == 0) {
				continue;
			}
			final TarEntry entry = TarEntry.read(header);
			int l = (int) Math.ceil((double) entry.length / TarEntry.BLOCKSIZE) * TarEntry.BLOCKSIZE;
			if (name.equals(entry.name)) {
				// TODO: delete (replace) existing file
			} else {
				raf.skipBytes(l);
			}
		}
		if (is != null) {
			final byte[] empty = new byte[TarEntry.BLOCKSIZE];
			final long z = raf.getFilePointer();
			raf.write(empty);
			int l = 0, b;
			while ((b = is.read()) != -1) {
				raf.write(b);
				l++;
			}
			final int p = l < TarEntry.BLOCKSIZE ? TarEntry.BLOCKSIZE - l : l % TarEntry.BLOCKSIZE;
			for (int i = 0; i < p; i++) {
				raf.write(0);
			}
			raf.seek(z);
			final TarEntry entry = new TarEntry();
			entry.name = name;
			entry.length = l;
			raf.write(entry.getBytes());
		}
		raf.close();
	}
}
