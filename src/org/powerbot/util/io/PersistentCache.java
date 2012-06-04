package org.powerbot.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.Adler32;

import javax.crypto.Cipher;

import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;

/**
 * @author Paris
 */
public final class PersistentCache extends ConcurrentHashMap<String, String> {
	private static final long serialVersionUID = 1L;
	private static volatile PersistentCache instance;
	private final String name;
	private final File store;
	private final byte[] key;
	private final static String CIPHER_ALGORITHM = "RC4", KEY_ALGORITHM = "RC4";

	private PersistentCache() {
		super();

		final Adler32 c = new Adler32();
		c.update(new byte[] {0x3a, 0x16, 0xd, 0x10, 0x7f});
		final long uid = Configuration.getUID();
		c.update((int) (uid & 0xffffffff));
		c.update((int) (uid >> 32));
		final long l = c.getValue();
		name = String.format("%s.tmp", Long.toHexString(l));
		store = new File(System.getProperty("java.io.tmpdir"), name);
		key = CipherStreams.getSharedKey(StringUtil.getBytesUtf8(name));

		load();
	}

	public static synchronized PersistentCache getInstance() {
		if (instance == null) {
			instance = new PersistentCache();
		}
		return instance;
	}

	private synchronized void load() {
		InputStream fis = null;
		if (store.isFile()) {
			try {
				fis = new FileInputStream(store);
				final InputStream is = CipherStreams.getCipherInputStream(fis, Cipher.ENCRYPT_MODE, key, CIPHER_ALGORITHM, KEY_ALGORITHM);
				clear();
				final Map<String, String> data = IniParser.deserialise(is).get(IniParser.EMPTYSECTION);
				if (data != null) {
					putAll(data);
				}
			} catch (final Exception ignored) {
				ignored.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (final IOException ignored2) {
					}
				}
			}
		}
	}

	public synchronized void save() {
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(store);
			final OutputStream os = CipherStreams.getCipherOutputStream(fos, Cipher.DECRYPT_MODE, key, CIPHER_ALGORITHM, KEY_ALGORITHM);
			final Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
			data.put(IniParser.EMPTYSECTION, this);
			IniParser.serialise(data, os);
		} catch (final Exception ignored) {
			ignored.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (final IOException ignored2) {
				}
			}
		}
	}
}
