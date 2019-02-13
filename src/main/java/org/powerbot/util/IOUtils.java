package org.powerbot.util;

import org.powerbot.script.StringUtils;

import java.io.*;
import java.util.Map;
import java.util.zip.*;

public class IOUtils {
	private static final int BUFFER_SIZE = 8192;

	public static byte[] read(final InputStream in) {
		try (final InputStream is = in) {
			return readFully(is);
		} catch (final IOException ignored) {
		}
		return new byte[0];
	}

	public static byte[] readFully(final InputStream in) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte[] buf = new byte[BUFFER_SIZE];
		for (int l; (l = in.read(buf)) != -1; ) {
			out.write(buf, 0, l);
		}
		return out.toByteArray();
	}

	public static String readString(final InputStream is) {
		return StringUtils.newStringUtf8(read(is));
	}

	public static void write(final InputStream in, final OutputStream out) throws IOException {
		final byte[] buf = new byte[BUFFER_SIZE];
		for (int l; (l = in.read(buf)) != -1; ) {
			out.write(buf, 0, l);
		}
	}

	public static void write(final InputStream in, final File out) {
		try (final OutputStream os = new FileOutputStream(out)) {
			write(in, os);
		} catch (final IOException ignored) {
		}
	}

	private static long checksum(final InputStream in, final Checksum c) {
		try (final CheckedInputStream cis = new CheckedInputStream(in, c)) {
			final byte[] buf = new byte[BUFFER_SIZE];
			while (cis.read(buf) != -1) {
			}
			return cis.getChecksum().getValue();
		} catch (final IOException ignored) {
			return -1;
		}
	}

	public static void write(final Map<String, byte[]> entries, final File out) {
		try (final ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(out))) {
			zip.setMethod(ZipOutputStream.STORED);
			zip.setLevel(0);
			for (final Map.Entry<String, byte[]> item : entries.entrySet()) {
				final ZipEntry entry = new ZipEntry(item.getKey() + ".class");
				entry.setMethod(ZipEntry.STORED);
				final byte[] data = item.getValue();
				entry.setSize(data.length);
				entry.setCompressedSize(data.length);
				entry.setCrc(checksum(new ByteArrayInputStream(data), new CRC32()));
				zip.putNextEntry(entry);
				zip.write(item.getValue());
				zip.closeEntry();
			}
		} catch (final IOException ignored) {
		}
	}
}
