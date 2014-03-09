package org.powerbot.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

public class TarReader implements Iterator<Map.Entry<String, byte[]>>, Iterable<Map.Entry<String, byte[]>> {
	private final InputStream in;
	private boolean closed;
	private AtomicReference<Map.Entry<String, byte[]>> item;

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
		final byte[] name = new byte[100];
		in.read(name);
		if (name[0] == 0) {
			in.close();
			return null;
		}

		in.skip(24);
		final byte[] size = new byte[12];
		in.read(size);

		int l = Integer.parseInt(newString(size).trim(), 8);
		in.skip(512 - 136);
		final byte[] d = new byte[l];
		l = 0;
		while (l < d.length) {
			l += in.read(d, l, d.length - l);
		}

		l = 512 - (l % 512);
		if (l > 0) {
			in.skip(l);
		}

		return new AbstractMap.SimpleImmutableEntry<String, byte[]>(newString(name), d);
	}

	private static String newString(final byte[] b) {
		int l = 0;

		for (; l < b.length; l++) {
			if (b[l] == 0) {
				break;
			}
		}

		return l == 0 ? "" : new String(b, 0, l, Charset.forName("US-ASCII"));
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Map.Entry<String, byte[]>> iterator() {
		return this;
	}
}
