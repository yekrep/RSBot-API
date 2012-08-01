package org.powerbot.util.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.zip.ZipInputStream;

import org.powerbot.service.scripts.ScriptClassLoader;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.util.StringUtil;

/**
 * @author Paris
 */
public final class ScriptCacheManager {
	private static ScriptCacheManager instance;
	private final static int MAX_ENTRIES = 10;
	private final static String DELIMITER = "\n";
	private final Class<?>[] permissions;
	private final CryptFile manifest;
	private final Queue<String> list;

	private ScriptCacheManager() {
		manifest = new CryptFile("scripts-list.txt", ScriptCacheManager.class);
		list = new LinkedList<String>();
		permissions = new Class<?>[] { ScriptCacheManager.class, ScriptClassLoader.class };

		final InputStream in;
		try {
			in = manifest.getInputStream();
		} catch (final IOException ignored) {
			return;
		}
		final String data = IOHelper.readString(in);
		if (data != null && !data.isEmpty()) {
			final String[] items = data.split(DELIMITER);
			for (final String item : items) {
				list.add(item);
			}
		}
	}

	public synchronized static ScriptCacheManager getInstance() {
		if (instance == null) {
			instance = new ScriptCacheManager();
		}
		return instance;
	}

	public ScriptClassLoader load(final ScriptDefinition def) throws IOException {
		if (def.local) {
			return new ScriptClassLoader(def.source);
		}
		final CryptFile cache = new CryptFile(getID(def), permissions);
		cache.download(def.source);
		push(def);
		return new ScriptClassLoader(new ZipInputStream(cache.getInputStream()));
	}

	private void push(final ScriptDefinition def) {
		final String id = getID(def);
		if (!list.contains(id)) {
			list.add(id);
		}
		while (list.size() > MAX_ENTRIES) {
			new CryptFile(list.poll(), permissions).delete();
		}
		flush();
	}

	private void flush() {
		if (list.isEmpty()) {
			manifest.delete();
		} else {
			final StringBuilder s = new StringBuilder();
			final Iterator<String> e = list.iterator();
			while (e.hasNext()) {
				s.append(DELIMITER);
				s.append(e.next());
			}
			s.delete(0, DELIMITER.length());
			try {
				IOHelper.write(new ByteArrayInputStream(StringUtil.getBytesUtf8(s.toString())), manifest.getOutputStream());
			} catch (final IOException ignored) {
			}
		}
	}

	private String getID(final ScriptDefinition def) {
		return "script/" + def.getID();
	}
}
