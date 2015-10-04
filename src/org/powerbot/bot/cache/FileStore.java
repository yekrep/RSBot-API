package org.powerbot.bot.cache;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileStore {
	private static final int IDX_BLOCK_LEN = 6;
	private static final int HEADER_LEN = 8;
	private static final int EXPANDED_HEADER_LEN = 10;
	private static final int BLOCK_LEN = 512;
	private static final int EXPANDED_BLOCK_LEN = 510;
	private static final int TOTAL_BLOCK_LEN = HEADER_LEN + BLOCK_LEN;
	private static final ByteBuffer tempBuffer = ByteBuffer.allocateDirect(TOTAL_BLOCK_LEN);

	private final int index;
	private final FileChannel dataChannel;
	private final FileChannel indexChannel;
	private final int maxSize;

	public FileStore(final int index, final FileChannel dataChannel, final FileChannel indexChannel, final int maxSize) {
		this.index = index;
		this.dataChannel = dataChannel;
		this.indexChannel = indexChannel;
		this.maxSize = maxSize;
	}

	public int getFileCount() {
		try {
			return (int) (indexChannel.size() / IDX_BLOCK_LEN);
		} catch (final Exception ignored) {
		}
		return 0;
	}

	public ByteBuffer get(final int file) {
		try {
			if (file * IDX_BLOCK_LEN + IDX_BLOCK_LEN > indexChannel.size()) {
				return null;
			}

			tempBuffer.position(0).limit(IDX_BLOCK_LEN);
			indexChannel.read(tempBuffer, file * IDX_BLOCK_LEN);
			tempBuffer.flip();
			final int size = getMediumInt(tempBuffer);
			int block = getMediumInt(tempBuffer);

			if (size < 0 || size > maxSize) {
				return null;
			}
			if (block <= 0 || block > dataChannel.size() / TOTAL_BLOCK_LEN) {
				return null;
			}

			final ByteBuffer fileBuffer = ByteBuffer.allocate(size);
			int remaining = size;
			int chunk = 0;
			final int blockLen = file <= 0xffff ? BLOCK_LEN : EXPANDED_BLOCK_LEN;
			final int headerLen = file <= 0xffff ? HEADER_LEN : EXPANDED_HEADER_LEN;
			while (remaining > 0) {
				if (block == 0) {
					return null;
				}

				final int blockSize = remaining > blockLen ? blockLen : remaining;
				tempBuffer.position(0).limit(blockSize + headerLen);
				dataChannel.read(tempBuffer, (long) block * TOTAL_BLOCK_LEN);
				tempBuffer.flip();

				final int currentFile, currentChunk, nextBlock, currentIndex;

				if (file <= 65535) {
					currentFile = tempBuffer.getShort() & 0xffff;
					currentChunk = tempBuffer.getShort() & 0xffff;
					nextBlock = getMediumInt(tempBuffer);
					currentIndex = tempBuffer.get() & 0xff;
				} else {
					currentFile = tempBuffer.getInt();
					currentChunk = tempBuffer.getShort() & 0xffff;
					nextBlock = getMediumInt(tempBuffer);
					currentIndex = tempBuffer.get() & 0xff;
				}

				if (file != currentFile || chunk != currentChunk || index != currentIndex) {
					return null;
				}
				if (nextBlock < 0 || nextBlock > dataChannel.size() / TOTAL_BLOCK_LEN) {
					return null;
				}

				fileBuffer.put(tempBuffer);
				remaining -= blockSize;
				block = nextBlock;
				chunk++;
			}

			fileBuffer.flip();
			return fileBuffer;
		} catch (final IOException ignored) {
		}
		return null;
	}

	private static int getMediumInt(final ByteBuffer buffer) {
		return ((buffer.get() & 0xff) << 16) | ((buffer.get() & 0xff) << 8) | (buffer.get() & 0xff);
	}
}
