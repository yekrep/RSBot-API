package org.powerbot.util.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.crypto.Cipher;

/**
 * @author Paris
 */
public final class XOROutputStream extends FilterOutputStream {
	private final byte[] key;
	private final int opmode, l;
	private int n, d;

	public XOROutputStream(final OutputStream out, final byte[] key, final int opmode) {
		super(out);
		this.key = key;
		if (!(opmode == Cipher.DECRYPT_MODE || opmode == Cipher.ENCRYPT_MODE)) {
			throw new IllegalArgumentException();
		}
		this.opmode = opmode;
		l = this.key.length;
		n = 0;
		d = XORInputStream.DELTA;
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
	public void write(final byte[] b, final int off, final int len) throws IOException {
		rotate(b, off, len);
		super.out.write(b, off, len);
	}
}
