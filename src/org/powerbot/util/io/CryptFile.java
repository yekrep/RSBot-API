package org.powerbot.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Adler32;

import javax.crypto.Cipher;

import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;

/**
 * @author Paris
 */
public final class CryptFile {
	public static final Map<File, Class<?>[]> PERMISSIONS = new HashMap<File, Class<?>[]>();

	private final String name;
	private final File store;
	private final byte[] key;
	private final static String CIPHER_ALGORITHM = "RC4", KEY_ALGORITHM = "RC4";

	public CryptFile(final String id, final Class<?>... parents) {
		final Adler32 c = new Adler32();
		c.update(StringUtil.getBytesUtf8(id));
		final long uid = Configuration.getUID();
		c.update((int) (uid & 0xffffffff));
		c.update((int) (uid >> 32));
		final long l = c.getValue();
		name = String.format("%s.tmp", Long.toHexString(l));
		store = new File(System.getProperty("java.io.tmpdir"), name);
		key = CipherStreams.getSharedKey(StringUtil.getBytesUtf8(name));
		PERMISSIONS.put(store, parents);
	}

	public InputStream getInputStream() throws IOException {
		if (!store.isFile()) {
			throw new FileNotFoundException(store.toString());
		}
		try {
			return CipherStreams.getCipherInputStream(new FileInputStream(store), Cipher.ENCRYPT_MODE, key, CIPHER_ALGORITHM, KEY_ALGORITHM);
		} catch (final GeneralSecurityException e) {
			throw new IOException(e);
		}
	}

	public OutputStream getOutputStream() throws IOException {
		try {
			return CipherStreams.getCipherOutputStream(new FileOutputStream(store), Cipher.DECRYPT_MODE, key, CIPHER_ALGORITHM, KEY_ALGORITHM);
		} catch (final GeneralSecurityException e) {
			throw new IOException(e);
		}
	}
}
