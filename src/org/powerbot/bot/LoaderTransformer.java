package org.powerbot.bot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class LoaderTransformer implements ClassFileTransformer {
	public static final Object LOCK = new Object();
	public static final ConcurrentHashMap<String, String> params = new ConcurrentHashMap<String, String>();
	public static final Hashtable<String, byte[]> classes = new Hashtable<String, byte[]>();

	private static final int[] TABLE = new int[128];

	static {
		for (int i = 0; i < TABLE.length; i++) {
			if (i > 64 && i < 91) {
				TABLE[i] = i - 65;
			} else if (i > 96 && i < 123) {
				TABLE[i] = i - 71;
			} else if (i > 47 && i < 58) {
				TABLE[i] = i + 4;
			} else {
				TABLE[i] = -1;
			}
		}
		TABLE[43] = 62;
		TABLE[42] = 62;
		TABLE[47] = 63;
		TABLE[45] = 63;
	}

	@Override
	public byte[] transform(
			final ClassLoader loader,
			final String className,
			final Class<?> classBeingRedefined,
			final ProtectionDomain protectionDomain,
			byte[] classfileBuffer
	) throws IllegalClassFormatException {
		if (!className.startsWith("app/")) {
			return classfileBuffer;
		}
		final ClassNode n = new ClassNode();
		final ClassReader r = new ClassReader(classfileBuffer);
		r.accept(n, ClassReader.SKIP_DEBUG);

		for (final MethodNode m : n.methods) {
			if ((m.access & Opcodes.ACC_STATIC) != Opcodes.ACC_STATIC) {
				continue;
			}
			if (!m.desc.contains("File")) {
				continue;
			}
			final InsnSearcher s = new InsnSearcher(m);
			while (s.getNext(new int[]{Opcodes.ALOAD, Opcodes.BIPUSH, Opcodes.INVOKEVIRTUAL, Opcodes.ASTORE}) != null) {
				final IntInsnNode bp = (IntInsnNode) s.getPrevious(Opcodes.BIPUSH);
				if (bp.operand != 6) {
					continue;
				}
				while (s.getNext(new int[]{Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.INVOKEVIRTUAL}) != null) {
					final MethodInsnNode min = (MethodInsnNode) s.current();
					if (!min.name.equals("put") || !min.owner.equals("java/util/Hashtable")) {
						continue;
					}
					AbstractInsnNode t;
					m.instructions.insert(min, t = new VarInsnNode(Opcodes.ALOAD, ((VarInsnNode) min.getPrevious().getPrevious()).var));
					m.instructions.insert(t, t = new VarInsnNode(Opcodes.ALOAD, ((VarInsnNode) min.getPrevious()).var));
					m.instructions.insert(t, new MethodInsnNode(
							Opcodes.INVOKESTATIC, LoaderTransformer.class.getName().replace('.', '/'),
							"param", "(Ljava/lang/String;Ljava/lang/String;)V", false
					));
					final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
					n.accept(cw);
					classfileBuffer = cw.toByteArray();
					break;
				}
				break;
			}
		}

		for (final FieldNode f : n.fields) {
			if (f.desc.equals("Lsun/security/pkcs/PKCS7;")) {
				for (final MethodNode m : n.methods) {
					if (!m.name.equals("<init>") || !m.desc.equals("([B)V")) {
						continue;
					}
					final VarInsnNode al;
					m.instructions.insert(al = new VarInsnNode(Opcodes.ALOAD, 1));
					m.instructions.insert(al, new MethodInsnNode(
							Opcodes.INVOKESTATIC, LoaderTransformer.class.getName().replace('.', '/'),
							"accept", "([B)V", false
					));
					final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
					n.accept(cw);
					classfileBuffer = cw.toByteArray();
				}
			}
		}
		return classfileBuffer;
	}

	public static void param(final String s1, final String s2) {
		params.put(s1, s2);
	}

	public static void accept(final byte[] arr) {
		final Hashtable<String, byte[]> classes = new Hashtable<String, byte[]>();
		byte[] inner_pack = null;

		final ZipInputStream i = new ZipInputStream(new ByteArrayInputStream(arr));
		ZipEntry entry;
		try {
			while ((entry = i.getNextEntry()) != null) {
				final String name = entry.getName();
				if (name.endsWith(".class")) {
					classes.put(name.substring(0, name.length() - 6), read(i));
				} else if (name.equals("inner.pack.gz")) {
					inner_pack = read(i);
				}
			}
		} catch (final IOException ignored) {
			System.out.println("Failed to intercept game.");
			System.exit(1);
		}

		if (inner_pack != null) {
			try {
				classes.clear();

				final String se = params.get("0"), iv = params.get("-1");
				if (se == null || iv == null || se.isEmpty() || iv.isEmpty()) {
					System.out.println("Failed to get decryption keys ...");
					System.exit(1);
				}
				final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(valueOf(se), "AES"), new IvParameterSpec(valueOf(iv)));
				final byte[] unscrambled_inner_pack = cipher.doFinal(inner_pack);
				final Pack200.Unpacker unpacker = Pack200.newUnpacker();
				final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(0x500000);
				final JarOutputStream jos = new JarOutputStream(byteArrayOutputStream);
				final GZIPInputStream gzipIS = new GZIPInputStream(new ByteArrayInputStream(unscrambled_inner_pack));
				unpacker.unpack(gzipIS, jos);
				final JarInputStream jarInputStream = new JarInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
				while ((entry = jarInputStream.getNextJarEntry()) != null) {
					String entryName = entry.getName();
					if (entryName.endsWith(".class")) {
						final byte[] read = read(jarInputStream);
						entryName = entryName.replace('/', '.');
						final String name = entryName.substring(0, entryName.length() - 6);
						classes.put(name, read);
					}
				}
			} catch (final Exception ignored) {
				System.exit(1);
				System.out.println("Failed to get RS3 classes ...");
			}
		}

		synchronized (LOCK) {
			LoaderTransformer.classes.clear();
			LoaderTransformer.classes.putAll(classes);
		}
	}

	private static byte[] read(final ZipInputStream inputStream) throws IOException {
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

	private static byte[] valueOf(final String parameter) {
		final int parameterLength = parameter.length();
		if (0 == parameterLength) {
			return new byte[0];
		}
		final int index = 0xFFFFFFFC & parameterLength + 3;
		int bufferSize = 3 * (index / 4);
		if (parameterLength <= index - 2) {
			bufferSize -= 2;
		} else {
			char c = parameter.charAt(index - 2);
			if (c >= TABLE.length) {
				bufferSize -= 2;
			} else {
				int operand = TABLE[c];
				if (operand == -1) {
					bufferSize -= 2;
				} else if (parameterLength <= index - 1) {
					bufferSize--;
				} else {
					c = parameter.charAt(index - 1);
					if (TABLE.length <= c) {
						bufferSize--;
					} else {
						operand = TABLE[c];
						if (operand == -1) {
							bufferSize--;
						}
					}
				}
			}
		}
		final byte[] buffer = new byte[bufferSize];
		int readBytes = 0;
		final int parameterLength1 = parameter.length();
		int index1 = 0;
		while (parameterLength1 > index1) {
			final int[] ops = {-1, -1, -1, -1};
			for (int subIndex = 0; (subIndex + index1 < parameterLength1) && (subIndex < 4); subIndex++) {
				final char c = parameter.charAt(subIndex + index1);
				if (c < TABLE.length) {
					ops[subIndex] = TABLE[c];
				}
			}
			buffer[(readBytes++)] = (byte) (ops[0] << 2 | ops[1] >>> 4);
			if (ops[2] == -1) {
				index1 += 2;
			} else {
				buffer[(readBytes++)] = (byte) (ops[2] >>> 2 | 0xF0 & ops[1] << 4);
				if (ops[3] == -1) {
					index1 += 3;
				} else {
					buffer[(readBytes++)] = (byte) (ops[3] | ops[2] << 6 & 0xC0);
					index1 += 4;
				}
			}
		}
		return buffer;
	}
}
