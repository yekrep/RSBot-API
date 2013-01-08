package org.powerbot.core.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.GZIPInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.powerbot.util.StringUtil;
import org.powerbot.util.io.IOHelper;

/**
 * @author Timer
 */
public class Deflator {
	private static final boolean CACHE = false;
	public static byte[] inner_pack_hash;

	public static Map<String, byte[]> extract(final byte[] secretKeySpecKey, final byte[] ivParameterSpecKey, final byte[] loader) {
		try {
			byte[] inner_pack = null;
			JarInputStream jarInputStream = new JarInputStream(new ByteArrayInputStream(loader));
			JarEntry entry;
			while ((entry = jarInputStream.getNextJarEntry()) != null && inner_pack == null) {
				if (entry.getName().equals("inner.pack.gz")) {
					inner_pack = read(jarInputStream);
					break;
				}
			}
			if (inner_pack == null) {
				return null;
			}
			final MessageDigest digest = MessageDigest.getInstance("SHA-1");
			inner_pack_hash = digest.digest(inner_pack);

			final Map<String, byte[]> classes = new HashMap<>();
			final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeySpecKey, "AES");
			final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivParameterSpecKey));
			final byte[] unscrambled_inner_pack = cipher.doFinal(inner_pack);

			final Pack200.Unpacker unpacker = Pack200.newUnpacker();
			final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(0x500000);
			final JarOutputStream jos = new JarOutputStream(byteArrayOutputStream);
			final GZIPInputStream gzipIS = new GZIPInputStream(new ByteArrayInputStream(unscrambled_inner_pack));
			unpacker.unpack(gzipIS, jos);

			jarInputStream = new JarInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
			while ((entry = jarInputStream.getNextJarEntry()) != null) {
				String entryName = entry.getName();
				if (entryName.endsWith(".class")) {
					final byte[] read = read(jarInputStream);
					entryName = entryName.replace('/', '.');
					final String name = entryName.substring(0, entryName.length() - 6);
					classes.put(name, read);
				}
			}
			if (CACHE) {
				IOHelper.write(classes, new File(StringUtil.byteArrayToHexString(inner_pack_hash).substring(0, 6) + ".jar"));
			}
			return classes;
		} catch (final Exception ignored) {
		}
		return null;
	}

	private static byte[] read(final JarInputStream inputStream) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte[] buffer = new byte[2048];
		int read;
		while (inputStream.available() > 0) {
			read = inputStream.read(buffer, 0, buffer.length);
			if (read < 0) {
				break;
			}
			out.write(buffer, 0, read);
		}
		return out.toByteArray();
	}
}
