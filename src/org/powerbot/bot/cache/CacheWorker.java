package org.powerbot.bot.cache;

import java.io.File;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class CacheWorker {
	private final CacheFileSystem system;
	public FileStore[] stores;
	private ReferenceTable[] tables;
	private SoftReference[][] blocks;

	public CacheWorker(final boolean os) {
		system = new CacheFileSystem(getDirectory(os));
		init();
	}

	private void init() {
		final FileStore reference = new FileStore(255, system.getDataChannel(), system.getChannel(255), 2000000);
		stores = new FileStore[reference.getFileCount()];
		for (int i = 0; i < stores.length; i++) {
			stores[i] = new FileStore(i, system.getDataChannel(), system.getChannel(i), 10000000);
		}

		tables = new ReferenceTable[reference.getFileCount()];
		for (int i = 0; i < tables.length; i++) {
			final ByteBuffer data;
			try {
				data = reference.get(i);
			} catch (final Exception ignored) {
				continue;
			}
			if (data == null) {
				continue;
			}
			tables[i] = new ReferenceTable(i, data, null);
		}

		blocks = new SoftReference[tables.length][];
		for (int i = 0; i < blocks.length; i++) {
			if (tables[i] == null) {
				blocks[i] = new SoftReference[0];
				continue;
			}
			blocks[i] = new SoftReference[tables[i].getEntryCount() + 1];
		}
	}

	public Block getBlock(final int tree_index, final int block) {
		if (tree_index < 0 || tree_index >= tables.length) {
			return null;
		}
		if (block > -1 && block < blocks[tree_index].length) {
			@SuppressWarnings("unchecked")
			final SoftReference<Block> r = blocks[tree_index][block];
			final Block b;
			if (r != null && (b = r.get()) != null) {
				return b;
			}
		}
		if (tables[tree_index].getEntry(block) == null) {
			return null;
		}
		final ByteBuffer b = stores[tree_index].get(block);
		try {
			final Block b2 = new Block(tables[tree_index].getEntry(block), new FileContainer(b).unpack());
			blocks[tree_index][block] = new SoftReference<Block>(b2);
			return b2;
		} catch (final NullPointerException ignored) {
			return null;
		}
	}

	private File getDirectory(final boolean os) {
		String root = System.getProperty("user.home");
		if (root == null) {
			root = System.getenv(System.getProperty("os.name").toLowerCase().startsWith("win") ? "USERPROFILE" : "HOME");
		}
		if (root == null) {
			root = "~" + File.separatorChar;
		} else if (!root.endsWith(String.valueOf(File.separatorChar))) {
			root = root + File.separatorChar;
		}
		final String cache = "jagexcache";
		for (int i = 0; i < 3; ++i) {
			final String test = root + cache + (i > 0 ? i + "" : "") + File.separatorChar + (os ? "oldschool" : "runescape") + File.separatorChar + "LIVE" + File.separatorChar;
			final File f = new File(test);
			if (f.exists() && f.isDirectory()) {
				return f;
			}
		}
		Logger.getLogger("Cache Worker").warning("Cache is not built. Please restart the bot once you login");
		return new File(root + cache + File.separatorChar + (os ? "oldschool" : "runescape") + File.separatorChar + "LIVE" + File.separatorChar);
	}
}
