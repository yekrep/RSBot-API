package org.powerbot.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class IOUtils {
	private static final int BUFFER_SIZE = 8192;

	public static byte[] read(final InputStream in) {
		if (in == null) {
			return new byte[0];
		}
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			final byte[] buf = new byte[BUFFER_SIZE];
			for (int l; (l = in.read(buf)) != -1; ) {
				out.write(buf, 0, l);
			}
		} catch (final IOException ignored) {
		} finally {
			try {
				in.close();
			} catch (final IOException ignored) {
			}
		}
		return out.toByteArray();
	}

	public static String readString(final InputStream is) {
		return StringUtils.newStringUtf8(read(is));
	}

	public static void write(final InputStream in, final OutputStream out) {
		if (in == null || out == null) {
			return;
		}
		try {
			final byte[] buf = new byte[BUFFER_SIZE];
			for (int l; (l = in.read(buf)) != -1; ) {
				out.write(buf, 0, l);
			}
		} catch (final IOException ignored) {
		} finally {
			try {
				out.close();
			} catch (final IOException ignored) {
			}
			try {
				in.close();
			} catch (final IOException ignored) {
			}
		}
	}

	public static void write(final InputStream in, final File out) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(out);
			write(in, os);
		} catch (final IOException ignored) {
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (final IOException ignored) {
				}
			}
		}
	}

	private static long crc32(final InputStream in) {
		CheckedInputStream cis = null;
		try {
			cis = new CheckedInputStream(in, new CRC32());
			final byte[] buf = new byte[BUFFER_SIZE];
			//noinspection StatementWithEmptyBody
			while (cis.read(buf) != -1) {
			}
			return cis.getChecksum().getValue();
		} catch (final IOException ignored) {
			return -1;
		} finally {
			if (cis != null) {
				try {
					cis.close();
				} catch (final IOException ignored) {
				}
			}
		}
	}

	public static void write(final Map<String, byte[]> entries, final File out) {
		ZipOutputStream zip = null;
		try {
			zip = new ZipOutputStream(new FileOutputStream(out));
			zip.setMethod(ZipOutputStream.STORED);
			zip.setLevel(0);
			for (final Map.Entry<String, byte[]> item : entries.entrySet()) {
				final ZipEntry entry = new ZipEntry(item.getKey() + ".class");
				entry.setMethod(ZipEntry.STORED);
				final byte[] data = item.getValue();
				entry.setSize(data.length);
				entry.setCompressedSize(data.length);
				entry.setCrc(crc32(data));
				zip.putNextEntry(entry);
				zip.write(item.getValue());
				zip.closeEntry();
			}
		} catch (final IOException ignored) {
		} finally {
			if (zip != null) {
				try {
					zip.close();
				} catch (final IOException ignored) {
				}
			}
		}
	}


	public static long crc32(final byte[] data) {
		return crc32(new ByteArrayInputStream(data));
	}
}
