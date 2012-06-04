package org.powerbot.game.bot.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.zip.Adler32;

import javax.crypto.Cipher;

import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.CipherStreams;
import org.powerbot.util.io.IOHelper;

/**
 * @author Timer
 */
public class UIDManager {
	private static String newline = System.getProperty("line.separator");
	private static String separator = "#";
	private final String name;
	private final File store;
	private final byte[] key;
	private final static String CIPHER_ALGORITHM = "RC4", KEY_ALGORITHM = "RC4";

	private HashMap<String, byte[]> uids = new HashMap<String, byte[]>();
	private String lastUsed = "";

	public UIDManager() {
		final Adler32 c = new Adler32();
		c.update(new byte[] {0x1b, 0x22, 0xe, 0x19, 0x49});
		final long uid = Configuration.getUID();
		c.update((int) (uid & 0xffffffff));
		c.update((int) (uid >> 32));
		final long l = c.getValue();
		name = String.format("%s.tmp", Long.toHexString(l));
		store = new File(System.getProperty("java.io.tmpdir"), name);
		key = CipherStreams.getSharedKey(StringUtil.getBytesUtf8(name));

		InputStream fis = null;
		if (store.isFile()) {
			try {
				fis = new FileInputStream(store);
				final InputStream is = CipherStreams.getCipherInputStream(fis, Cipher.ENCRYPT_MODE, key, CIPHER_ALGORITHM, KEY_ALGORITHM);
				for (final String line : IOHelper.readString(is).split("\n")) {
					if (!line.isEmpty()) {
						final String[] data = line.split(separator, 2);
						if (data.length == 2) {
							uids.put(data[0], data[1].getBytes());
						}
					}
				}
			} catch (final Exception ignored) {
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

	public String getLastUsed() {
		return lastUsed;
	}

	public byte[] getUID(String name) {
		if (name.equals("")) {
			name = "DEFAULT";
		}
		lastUsed = name;

		final byte[] data = uids.get(name);
		if (data == null) {
			return new byte[0];
		}
		return data;
	}

	public void setUID(String name, final byte[] uid) {
		if (name.equals("")) {
			name = "DEFAULT";
		}

		uids.put(name, uid);
	}

	public void save() throws IOException {
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(store);
			final OutputStream os = CipherStreams.getCipherOutputStream(fos, Cipher.DECRYPT_MODE, key, CIPHER_ALGORITHM, KEY_ALGORITHM);
			for (final String key : uids.keySet()) {
				os.write(key.getBytes());
				os.write(separator.getBytes());
				os.write(uids.get(key));
				os.write(newline.getBytes());
			}
		} catch (final Exception ignored) {
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
