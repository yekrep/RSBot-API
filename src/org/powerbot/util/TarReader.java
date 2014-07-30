package org.powerbot.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

public class TarReader implements Iterator<Map.Entry<String, byte[]>>, Iterable<Map.Entry<String, byte[]>>, Closeable {
	private final InputStream in;
	private boolean closed;
	private final AtomicReference<Map.Entry<String, byte[]>> item;

	public TarReader(final InputStream in) {
		this.in = in;
		closed = false;
		item = new AtomicReference<Map.Entry<String, byte[]>>(null);
	}

	@Override
	public boolean hasNext() {
		if (closed) {
			return false;
		}

		if (item.get() == null) {
			try {
				item.set(getNext());
			} catch (final IOException ignored) {
				closed = true;
				return false;
			}
		}

		return item.get() != null;
	}

	@Override
	public Map.Entry<String, byte[]> next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return item.getAndSet(null);
	}

	private Map.Entry<String, byte[]> getNext() throws IOException {
		int o = 0, l;

		final byte[] header = new byte[l = 512];
		while (o < l) {
			final int x = in.read(header, o, l - o);
			if (x == -1) {
				throw new IOException();
			}
			o += x;
		}

		if (header[0] == 0) {
			in.close();
			return null;
		}

		final String name = newString(header, 0, 100), size = newString(header, 124, 12);
		l = Integer.parseInt(size.trim(), 8);

		final byte[] d = new byte[l];
		o = 0;
		while (o < l) {
			final int x = in.read(d, o, l - o);
			if (x == -1) {
				throw new IOException();
			}
			o += x;
		}

		l = 512 - (l % 512);
		while (l > 0) {
			l -= in.skip(l);
		}

		return new AbstractMap.SimpleImmutableEntry<String, byte[]>(name, d);
	}

	private static String newString(final byte[] b, final int o, final int l) {
		int i;

		for (i = 0; i < l; i++) {
			if (b[o + i] == 0) {
				break;
			}
		}

		return i == 0 ? "" : new String(b, o, i, Charset.forName("US-ASCII"));
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Map.Entry<String, byte[]>> iterator() {
		return this;
	}

	@Override
	public void close() throws IOException {
		in.close();
	}
}
