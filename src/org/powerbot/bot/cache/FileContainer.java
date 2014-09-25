package org.powerbot.bot.cache;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

public class FileContainer {
	private final int crc;
	private byte[] buffer;
	private int compression;
	private int size;

	public FileContainer(final ByteBuffer buffer) {
		final CRC32 crc32 = new CRC32();
		crc32.update(buffer.array());
		crc = (int) crc32.getValue();
		init(buffer);
	}

	private void init(final ByteBuffer buffer) {
		compression = buffer.get() & 0xff;
		final int compressedSize = buffer.getInt();
		if (compression == 0) {
			size = compressedSize;
		} else {
			size = buffer.getInt();
		}
		this.buffer = new byte[compressedSize];
		buffer.get(this.buffer);
	}

	public byte[] unpack() {
		if (compression == 0) {
			return buffer;
		}
		final byte[] result = new byte[size];
		try {
			final ByteArrayInputStream in = new ByteArrayInputStream(buffer);
			final DataInputStream stream = new DataInputStream(
					compression == 1 ? new BZip2CompressorInputStream(in) : new GZIPInputStream(in)
			);
			stream.readFully(result);
			stream.close();
			return result;
		} catch (final IOException ignored) {
		}
		return null;
	}

	public int getCRC() {
		return crc;
	}
}
