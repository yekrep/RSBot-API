package org.powerbot.os.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class IOUtils {
	public static final int BUFFER_SIZE = 4096;

	public static byte[] read(final InputStream is) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			final byte[] temp = new byte[BUFFER_SIZE];
			int read;
			while ((read = is.read(temp)) != -1) {
				buffer.write(temp, 0, read);
			}
		} catch (final IOException ignored) {
			try {
				buffer.close();
			} catch (final IOException ignored2) {
			}
			buffer = null;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (final IOException ignored) {
			}
		}
		return buffer == null ? null : buffer.toByteArray();
	}

	public static byte[] read(final URL in) {
		InputStream is = null;
		try {
			is = in.openStream();
			return read(is);
		} catch (final IOException ignored) {
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (final IOException ignored) {
				}
			}
		}
	}

	public static byte[] read(final File in) {
		InputStream is = null;
		try {
			is = new FileInputStream(in);
			return read(is);
		} catch (final IOException ignored) {
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (final IOException ignored) {
				}
			}
		}
	}

	public static String readString(final InputStream is) {
		return StringUtils.newStringUtf8(read(is));
	}

	public static String readString(final URL in) {
		return StringUtils.newStringUtf8(read(in));
	}

	public static void write(final InputStream in, final OutputStream out) {
		try {
			final byte[] buf = new byte[BUFFER_SIZE];
			int len;
			while ((len = in.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
		} catch (final IOException ignored) {
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
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
}
