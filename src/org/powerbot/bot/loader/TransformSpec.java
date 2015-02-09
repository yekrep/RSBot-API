package org.powerbot.bot.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.powerbot.util.IOUtils;

public class TransformSpec implements Transformer {
	public final Map<String, ClassVisitor> adapters;
	private final Map<String, ClassWriter> writers;
	public final Map<String, String> attributes;
	public final Map<Integer, Integer> constants;
	public final Map<Integer, Integer> multipliers;
	public final Map<String, String> remap;

	public static interface Headers {
		public static final int MAGIC = 0xFADFAD;
		public static final int ATTRIBUTE = 1;
		public static final int GET_STATIC = 2;
		public static final int GET_FIELD = 3;
		public static final int ADD_FIELD = 4;
		public static final int ADD_METHOD = 5;
		public static final int ADD_INTERFACE = 6;
		public static final int SET_SUPER = 7;
		public static final int SET_SIGNATURE = 8;
		public static final int INSERT_CODE = 9;
		public static final int OVERRIDE_CLASS = 10;
		public static final int CONSTANT = 11;
		public static final int MULTIPLIER = 12;
		public static final int END_OF_FILE = 13;
	}

	private TransformSpec() {
		adapters = new HashMap<String, ClassVisitor>();
		writers = new HashMap<String, ClassWriter>();
		attributes = new HashMap<String, String>();
		constants = new HashMap<Integer, Integer>();
		multipliers = new HashMap<Integer, Integer>();
		remap = new HashMap<String, String>();
	}

	public static TransformSpec parse(final InputStream in) throws IOException {
		DataInputStream scanner = null;

		try {
			scanner = new DataInputStream(new ByteArrayInputStream(IOUtils.read(in)));
			final TransformSpec tspec = new TransformSpec();

			if (scanner.readInt() != Headers.MAGIC) {
				throw new IOException("invalid patch format");
			}

			readString(scanner); // name
			scanner.readUnsignedShort(); // version
			int op;

			while ((op = scanner.readUnsignedByte()) != Headers.END_OF_FILE) {
				final String clazz;
				int count, ptr = 0;
				switch (op) {
				case Headers.ATTRIBUTE:
					final String key = readString(scanner);
					final String value = readString(scanner);
					tspec.attributes.put(key, new StringBuilder(value).reverse().toString());
					break;
				case Headers.GET_STATIC:
				case Headers.GET_FIELD:
					clazz = readString(scanner);
					count = scanner.readUnsignedShort();
					final AddGetterAdapter.Field[] fieldsGet = new AddGetterAdapter.Field[count];
					while (ptr < count) {
						final AddGetterAdapter.Field f = new AddGetterAdapter.Field();
						f.getter_access = scanner.readInt();
						f.getter_name = readString(scanner);
						f.getter_desc = readString(scanner);
						f.owner = readString(scanner);
						f.name = readString(scanner);
						f.desc = readString(scanner);
						f.overflow = scanner.readUnsignedByte();
						switch (f.overflow) {
						case 1:
							f.overflow_val = (long) scanner.readInt();
							break;
						case 2:
							f.overflow_val = scanner.readLong();
							break;
						default:
							f.overflow_val = 0;
							break;
						}
						fieldsGet[ptr++] = f;
					}
					tspec.adapters.put(clazz, new AddGetterAdapter(tspec.delegate(clazz), op == Headers.GET_FIELD, fieldsGet));
					break;
				case Headers.ADD_FIELD:
					clazz = readString(scanner);
					count = scanner.readUnsignedShort();
					final AddFieldAdapter.Field[] fieldsAdd = new AddFieldAdapter.Field[count];
					while (ptr < count) {
						final AddFieldAdapter.Field f = new AddFieldAdapter.Field();
						f.access = scanner.readInt();
						f.name = readString(scanner);
						f.desc = readString(scanner);
						fieldsAdd[ptr++] = f;
					}
					tspec.adapters.put(clazz, new AddFieldAdapter(tspec.delegate(clazz), fieldsAdd));
					break;
				case Headers.ADD_METHOD:
					clazz = readString(scanner);
					count = scanner.readUnsignedShort();
					final AddMethodAdapter.Method[] methods = new AddMethodAdapter.Method[count];
					while (ptr < count) {
						final AddMethodAdapter.Method m = new AddMethodAdapter.Method();
						m.access = scanner.readInt();
						m.name = readString(scanner);
						m.desc = readString(scanner);
						final byte[] code = new byte[scanner.readInt()];
						scanner.readFully(code);
						m.code = code;
						m.max_locals = scanner.readUnsignedByte();
						m.max_stack = scanner.readUnsignedByte();
						methods[ptr++] = m;
					}
					tspec.adapters.put(clazz, new AddMethodAdapter(tspec.delegate(clazz), methods));
					break;
				case Headers.ADD_INTERFACE:
					clazz = readString(scanner);
					final String inter = readString(scanner);
					tspec.adapters.put(clazz, new AddInterfaceAdapter(tspec.delegate(clazz), inter));
					break;
				case Headers.SET_SUPER:
					clazz = readString(scanner);
					final String superName = readString(scanner);
					tspec.adapters.put(clazz, new SetSuperAdapter(tspec.delegate(clazz), superName));
					break;
				case Headers.SET_SIGNATURE:
					clazz = readString(scanner);
					count = scanner.readUnsignedShort();
					final SetSignatureAdapter.Signature[] signatures = new SetSignatureAdapter.Signature[count];
					while (ptr < count) {
						final SetSignatureAdapter.Signature s = new SetSignatureAdapter.Signature();
						s.name = readString(scanner);
						s.desc = readString(scanner);
						s.new_access = scanner.readInt();
						s.new_name = readString(scanner);
						s.new_desc = readString(scanner);
						signatures[ptr++] = s;
					}
					tspec.adapters.put(clazz, new SetSignatureAdapter(tspec.delegate(clazz), signatures));
					break;
				case Headers.INSERT_CODE:
					clazz = readString(scanner);
					final String name = readString(scanner);
					final String desc = readString(scanner);
					count = scanner.readUnsignedByte();
					final Map<Integer, byte[]> fragments = new HashMap<Integer, byte[]>();
					while (count-- > 0) {
						final int off = scanner.readUnsignedShort();
						final byte[] code = new byte[scanner.readInt()];
						scanner.readFully(code);
						fragments.put(off, code);
					}
					tspec.adapters.put(clazz, new InsertCodeAdapter(tspec.delegate(clazz), name, desc, fragments, scanner.readUnsignedByte(), scanner.readUnsignedByte()));
					break;
				case Headers.OVERRIDE_CLASS:
					final String old_clazz = readString(scanner);
					final String new_clazz = readString(scanner);
					count = scanner.readUnsignedByte();
					while (count-- > 0) {
						final String current_clazz = readString(scanner);
						tspec.adapters.put(current_clazz, new OverrideClassAdapter(tspec.delegate(current_clazz), old_clazz, new_clazz));
					}
					break;
				case Headers.CONSTANT:
					tspec.constants.put(scanner.readUnsignedShort(), scanner.readUnsignedShort());
					break;
				case Headers.MULTIPLIER:
					tspec.multipliers.put(scanner.readUnsignedShort(), scanner.readInt());
					break;
				}
			}

			return tspec;
		} finally {
			if (scanner != null) {
				try {
					scanner.close();
				} catch (final IOException ignored) {
				}
			}
		}
	}

	public static String readString(final DataInputStream in) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		int b;
		while ((b = in.read()) != 0xA && b != -1) {
			out.write(b);
		}
		final String s = new String(out.toByteArray());

		return s.replace("org/powerbot/game/client", org.powerbot.bot.rt6.client.Client.class.getPackage().getName().replace('.', '/')).
				replace("org/powerbot/os/client", org.powerbot.bot.rt4.client.Client.class.getPackage().getName().replace('.', '/'));
	}

	public byte[] transform(final byte[] data) {
		final ClassNode node = new ClassNode();
		final ClassReader reader = new ClassReader(data);
		reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
		final ClassVisitor adapter = adapters.get(node.name);
		if (adapter != null) {
			reader.accept(adapter, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
			return writers.get(node.name).toByteArray();
		}
		return data;
	}

	public ClassVisitor delegate(final String clazz) {
		final ClassVisitor delegate = adapters.get(clazz);
		if (delegate == null) {
			final ClassWriter writer = new ClassWriter(0);
			writers.put(clazz, writer);
			return writer;
		}
		return delegate;
	}
}
