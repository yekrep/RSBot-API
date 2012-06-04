package org.powerbot.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Paris
 */
public final class PersistentCache extends ConcurrentHashMap<String, String> {
	private static final long serialVersionUID = 1L;
	private static volatile PersistentCache instance;
	private final CryptFile store;

	private PersistentCache() {
		super();
		store = new CryptFile("perscache", PersistentCache.class);
		load();
	}

	public static synchronized PersistentCache getInstance() {
		if (instance == null) {
			instance = new PersistentCache();
		}
		return instance;
	}

	private synchronized void load() {
		clear();
		InputStream is = null;
		try {
			is = store.getInputStream();
			final Map<String, String> data = IniParser.deserialise(is).get(IniParser.EMPTYSECTION);
			if (data != null) {
				putAll(data);
			}
		} catch (final IOException ignored) {
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (final IOException ignored2) {
				}
			}
		}
	}

	public synchronized void save() {
		OutputStream os = null;
		try {
			os = store.getOutputStream();
			final Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
			data.put(IniParser.EMPTYSECTION, this);
			IniParser.serialise(data, os);
		} catch (final Exception ignored) {
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (final IOException ignored2) {
				}
			}
		}
	}
}
