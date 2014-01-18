package org.powerbot.misc;

import java.awt.Dimension;
import java.awt.Window;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.powerbot.util.Ini;

/**
 * @author Paris
 */
public class WindowCache implements Runnable, Closeable {
	private final Window window;
	private final CryptFile cache;

	public WindowCache(final Window window) {
		this.window = window;
		final String s;
		cache = new CryptFile(s = window.getClass().getName() + "-cache.1.ini", false, getClass());
		System.out.println(s);
	}

	@Override
	public void run() {
		InputStream in = null;
		try {
			in = cache.getInputStream();
			final Ini.Member t = new Ini().read(in).get();
			final Dimension d = window.getSize();
			window.setSize(new Dimension(t.getInt("w", d.width), t.getInt("h", d.height)));
		} catch (final IOException ignored) {
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (final IOException ignored) {
				}
			}
		}
	}

	@Override
	public void close() {
		OutputStream out = null;
		try {
			out = cache.getOutputStream();
			new Ini().get().put("w", window.getWidth()).put("h", window.getHeight()).parent().write(out);
		} catch (final IOException ignored) {
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (final IOException ignored) {
				}
			}
		}
	}
}
