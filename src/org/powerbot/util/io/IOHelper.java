package org.powerbot.util.io;

import org.powerbot.util.StringUtil;

import java.io.*;
import java.net.URL;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.GZIPInputStream;

/**
 * @author Paris
 */
public class IOHelper {
	public static final int BUFFER_SIZE = 4096;

	public static byte[] read(InputStream is) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			byte[] temp = new byte[BUFFER_SIZE];
			int read;
			while ((read = is.read(temp)) != -1) {
				buffer.write(temp, 0, read);
			}
		} catch (IOException ignored) {
			try {
				buffer.close();
			} catch (IOException ignored2) {
			}
			buffer = null;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException ignored) {
			}
		}
		return buffer == null ? null : buffer.toByteArray();
	}

	public static byte[] read(URL in) {
		try {
			return read(in.openStream());
		} catch (IOException ignored) {
			return null;
		}
	}

	public static byte[] read(File in) {
		try {
			return read(new FileInputStream(in));
		} catch (FileNotFoundException ignored) {
			return null;
		}
	}

	public static String readString(URL in) {
		return StringUtil.newStringUtf8(read(in));
	}

	public static String readString(File in) {
		return StringUtil.newStringUtf8(read(in));
	}

	public static void write(InputStream in, OutputStream out) {
		try {
			byte[] buf = new byte[BUFFER_SIZE];
			int len;
			while ((len = in.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
		} catch (IOException ignored) {
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ignored) {
			}
		}
	}

	public static void write(InputStream in, File out) {
		try {
			write(in, new FileOutputStream(out));
		} catch (FileNotFoundException ignored) {
		}
	}

	public static void write(String s, File out) {
		ByteArrayInputStream in = new ByteArrayInputStream(StringUtil.getBytesUtf8(s));
		write(in, out);
	}

	public static long crc32(InputStream in) throws IOException {
		CheckedInputStream cis = new CheckedInputStream(in, new CRC32());
		byte[] buf = new byte[BUFFER_SIZE];
		while (cis.read(buf) != -1) {
		}
		return cis.getChecksum().getValue();
	}

	public static long crc32(byte[] data) throws IOException {
		return crc32(new ByteArrayInputStream(data));
	}

	public static long crc32(File path) throws IOException {
		return crc32(new FileInputStream(path));
	}

	public static byte[] ungzip(byte[] data) {
		if (data.length < 2) {
			return data;
		}

		int header = (data[0] | data[1] << 8) ^ 0xffff0000;
		if (header != GZIPInputStream.GZIP_MAGIC) {
			return data;
		}

		try {
			ByteArrayInputStream b = new ByteArrayInputStream(data);
			GZIPInputStream gzin = new GZIPInputStream(b);
			ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
			for (int c = gzin.read(); c != -1; c = gzin.read()) {
				out.write(c);
			}
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return data;
		}
	}

	public static boolean isZip(File file) {
		String name = file.getName().toLowerCase();
		if (name.endsWith(".jar") || name.endsWith(".zip")) {
			return true;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] m = new byte[4];
			fis.read(m);
			fis.close();
			return (m[0] << 24 | m[1] << 16 | m[2] << 8 | m[3]) == 0x504b0304;
		} catch (IOException ignored) {
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ignored) {
				}
			}
		}
		return false;
	}
}
