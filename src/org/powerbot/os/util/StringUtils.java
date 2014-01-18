package org.powerbot.os.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author Paris
 */
public class StringUtils {

	public static String urlEncode(final String text) {
		if (text == null) {
			return null;
		}
		try {
			return URLEncoder.encode(text, "UTF-8");
		} catch (final UnsupportedEncodingException ignored) {
			return text;
		}
	}

	public static String urlDecode(final String text) {
		try {
			return URLDecoder.decode(text, "UTF-8");
		} catch (final Exception ignored) {
			return text;
		}
	}

	public static byte[] getBytesUtf8(final String string) {
		try {
			return string.getBytes("UTF-8");
		} catch (final UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	public static String newStringUtf8(final byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		try {
			return new String(bytes, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	public static String byteArrayToHexString(final byte[] b) {
		final StringBuilder s = new StringBuilder(b.length * 2);
		for (final byte aB : b) {
			s.append(Integer.toString((aB & 0xff) + 0x100, 16).substring(1));
		}
		return s.toString();
	}

	public static byte[] hexStringToByteArray(final String s) {
		final byte[] data = new byte[s.length() / 2];
		for (int i = 0; i < s.length(); i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}
