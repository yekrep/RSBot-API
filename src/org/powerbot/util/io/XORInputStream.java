package org.powerbot.util.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.Cipher;

/**
 * @author Paris
 */
public final class XORInputStream extends FilterInputStream {
	private final byte[] key;
	private final int opmode, l;
	private int n, d;
	public static final int DELTA = 0x9e3779b9;

	public XORInputStream(final InputStream in, final byte[] key, final int opmode) {
		super(in);
		this.key = key;
		if (!(opmode == Cipher.DECRYPT_MODE || opmode == Cipher.ENCRYPT_MODE)) {
			throw new IllegalArgumentException();
		}
		this.opmode = opmode;
		l = this.key.length;
		n = 0;
		d = DELTA;
	}

	private void rotate(final byte[] b, final int off, final int len) {
		for (int i = off; i < off + len; i++) {
			final int z = n++ % l, x = key[z];
			this.d += key[l - z - 1];
			final int d = this.d & 0x7f;
			b[i] = (byte) (opmode == Cipher.DECRYPT_MODE ? (b[i] + d) % 0xff ^ x : (b[i] ^ x) - d % 0xff);
		}
	}

	@Override
	public int read(final byte[] b, final int off, final int len) throws IOException {
		final int result = super.read(b, off, len);
		rotate(b, off, len);
		return result;
	}
}
