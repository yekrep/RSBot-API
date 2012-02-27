package org.powerbot.util.io;

import java.io.File;
import java.io.IOException;
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
		raf.close();
		key = md.digest();
	}
}
