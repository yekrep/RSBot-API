package org.powerbot.bot.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class CacheFileSystem {
	private static final int FILE_CACHE_COUNT_MAX = 256;

	private static final String FILE_CACHE_PREFIX = "main_file_cache";
	private static final String FILE_CACHE_STORAGE = FILE_CACHE_PREFIX + ".dat2";
	private static final String FILE_CACHE_TREE_PREFIX = FILE_CACHE_PREFIX + ".idx";

	private final File directory;
	private final FileChannel[] sparse_channels;

	public CacheFileSystem(final File directory) {
		this.directory = directory;
		this.sparse_channels = new FileChannel[FILE_CACHE_COUNT_MAX + 1];
	}

	public FileChannel getDataChannel() {
		return getChannel(new File(directory, FILE_CACHE_STORAGE), FILE_CACHE_COUNT_MAX);
	}

	public FileChannel getChannel(final int index) {
		return getChannel(new File(directory, FILE_CACHE_TREE_PREFIX + index), index);
	}

	public FileChannel getChannel(final File f, final int index) {
		if (sparse_channels[index] != null && sparse_channels[index].isOpen()) {
			return sparse_channels[index];
		}
		try {
			final RandomAccessFile r = new RandomAccessFile(f, "r");
			final FileChannel c = r.getChannel();
			return sparse_channels[index] = c;
		} catch (final FileNotFoundException ignored) {
		}
		return sparse_channels[index] = null;
	}
}