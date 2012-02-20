package org.powerbot.game.loader;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.GZIPInputStream;

public class PackEncryption {
	public static Map<String, byte[]> extract(byte[] secretKeySpecKey, byte[] ivParameterSpecKey, byte[] loader) {
		try {
			byte[] inner_pack = null;
			JarInputStream jarInputStream = new JarInputStream(new ByteArrayInputStream(loader));
			JarEntry entry;
			while (null != (entry = jarInputStream.getNextJarEntry())) {
				String entryName = entry.getName();
				if (!entryName.equals("inner.pack.gz")) {
					continue;
				}
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[2048];
				int read;
				while (jarInputStream.available() > 0) {
					read = jarInputStream.read(buffer, 0, buffer.length);
					if (read < 0) {
						break;
					}
					out.write(buffer, 0, read);
				}
				inner_pack = out.toByteArray();
				break;
			}
			if (inner_pack == null) {
				return null;
			}

			Map<String, byte[]> classes = new HashMap<String, byte[]>();
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeySpecKey, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(2, secretKeySpec, new IvParameterSpec(ivParameterSpecKey));
			byte[] unscrambled_inner_pack = cipher.doFinal(inner_pack);

			Pack200.Unpacker unpacker = Pack200.newUnpacker();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(0x500000);
			JarOutputStream jos = new JarOutputStream(byteArrayOutputStream);
			GZIPInputStream gzipIS = new GZIPInputStream(new ByteArrayInputStream(unscrambled_inner_pack));
			unpacker.unpack(gzipIS, jos);

			jarInputStream = new JarInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
			while (null != (entry = jarInputStream.getNextJarEntry())) {
				String entryName = entry.getName();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[2048];
				int read;
				while (jarInputStream.available() > 0) {
					read = jarInputStream.read(buffer, 0, buffer.length);
					if (read < 0) {
						break;
					}
					out.write(buffer, 0, read);
				}
				if (entryName.endsWith(".class")) {
					entryName = entryName.replace('/', '.');
					String name = entryName.substring(0, entryName.length() - 6);
					classes.put(name, out.toByteArray());
				}
			}
			return classes;
		} catch (Exception ignored) {
		}
		return null;
	}

	public static byte[] toByte(String key) {
		int keyLength = key.length();
		if (keyLength == 0) {
			return new byte[0];
		} else {
			int unscrambledLength;
			int lengthMod = -4 & keyLength + 3;
			unscrambledLength = lengthMod / 4 * 3;
			if (keyLength <= lengthMod - 2 || charIndex(key.charAt(lengthMod - 2)) == -1) {
				unscrambledLength -= 2;
			} else if (keyLength <= lengthMod - 1 || -1 == charIndex(key.charAt(lengthMod - 1))) {
				--unscrambledLength;
			}

			byte[] keyBytes = new byte[unscrambledLength];
			unscramble(keyBytes, 0, key);
			return keyBytes;
		}
	}

	private static int charIndex(char character) {
		return character >= 0 && character < charSet.length ? charSet[character] : -1;
	}

	private static int unscramble(byte[] bytes, int offset, String key) {
		int start = offset;
		int keyLength = key.length();
		int pos = 0;

		int readStart;
		int readOffset;
		while (true) {
			if (keyLength > pos) {
				int currentChar = charIndex(key.charAt(pos));

				int pos_1 = keyLength > (pos + 1) ? charIndex(key.charAt(pos + 1)) : -1;
				int pos_2 = pos + 2 < keyLength ? charIndex(key.charAt(2 + pos)) : -1;
				int pos_3 = keyLength > (pos + 3) ? charIndex(key.charAt(3 + pos)) : -1;
				bytes[offset++] = (byte) (pos_1 >>> 4 | currentChar << 2);
				if (pos_2 != -1) {
					bytes[offset++] = (byte) (pos_1 << 4 & 240 | pos_2 >>> 2);
					if (pos_3 != -1) {
						bytes[offset++] = (byte) (192 & pos_2 << 6 | pos_3);
						pos += 4;
						continue;
					}
				}
			}

			readOffset = offset;
			readStart = start;
			break;
		}

		return readOffset - readStart;
	}

	private static int[] charSet;

	static {
		int index;
		charSet = new int[128];
		for (index = 0; charSet.length > index; ++index) {
			charSet[index] = -1;
		}
		for (index = 65; index <= 90; ++index) {
			charSet[index] = index - 65;
		}
		for (index = 97; 122 >= index; ++index) {
			charSet[index] = index + 26 - 97;
		}
		for (index = 48; index <= 57; ++index) {
			charSet[index] = 4 + index;
		}
		int[] var2 = charSet;
		charSet[43] = 62;
		var2[42] = 62;
		int[] var1 = charSet;
		charSet[47] = 63;
		var1[45] = 63;
	}
}
