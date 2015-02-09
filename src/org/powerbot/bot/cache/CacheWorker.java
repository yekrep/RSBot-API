package org.powerbot.bot.cache;

import java.io.File;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;

public class CacheWorker {
	private final CacheFileSystem system;
	public FileStore[] stores;
	private ReferenceTable[] tables;
	private SoftReference[][] blocks;

	public CacheWorker(final File directory) {
		system = new CacheFileSystem(directory);
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
			final ByteBuffer data = reference.get(i);
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
			//noinspection unchecked
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
		final Block b2 = new Block(tables[tree_index].getEntry(block), new FileContainer(b).unpack());
		blocks[tree_index][block] = new SoftReference<Block>(b2);
		return b2;
	}
}
