package org.powerbot.util.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Paris
 */
public final class XORInputStream extends FilterInputStream {
	private final byte[] key;
	private final int l;
	private int n;

	public XORInputStream(final InputStream in, final byte[] key) {
		super(in);
		this.key = key;
		l = this.key.length;
		n = 0;
	}

	private void rotate(final byte[] b, final int off, final int len) {
		for (int i = off; i < off + len; i++) {
			b[i] = (byte) (b[i] ^ (key[n++ % l] & 0xff));
		}
	}

	@Override
	public int read() throws IOException {
		return read(new byte[1], 0, 1);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		final int result = super.read(b, off, len);
		rotate(b, off, len);
		return result;
	}
}
