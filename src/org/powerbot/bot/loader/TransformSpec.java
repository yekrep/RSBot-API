package org.powerbot.bot.loader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class TransformSpec implements Transformer {
	public final Map<String, ClassVisitor> adapters;
	private final Map<String, ClassWriter> writers;
	public final Map<String, String> attributes;
	public final Map<Integer, Integer> constants;
	public final Map<Integer, Integer> multipliers;
	private final Scanner scanner;
	private String name;
	private int version;

	public static interface Headers {
		int ATTRIBUTE = 1;
		int GET_STATIC = 2;
		int GET_FIELD = 3;
		int ADD_FIELD = 4;
		int ADD_METHOD = 5;
		int ADD_INTERFACE = 6;
		int SET_SUPER = 7;
		int SET_SIGNATURE = 8;
		int INSERT_CODE = 9;
		int OVERRIDE_CLASS = 10;
		int CONSTANT = 11;
		int MULTIPLIER = 12;
		int END_OF_FILE = 13;
	}

	private static final int MAGIC = 0xFADFAD;

	public TransformSpec(final InputStream data) {
		scanner = new Scanner(data);
		adapters = new HashMap<String, ClassVisitor>();
		writers = new HashMap<String, ClassWriter>();
		attributes = new HashMap<String, String>();
		constants = new HashMap<Integer, Integer>();
		multipliers = new HashMap<Integer, Integer>();
	}

	public String getName() {
		return name;
	}

	public int getVersion() {
		return version;
	}

	public void adapt() {
		if (scanner.readInt() != TransformSpec.MAGIC) {
			throw new RuntimeException("invalid patch format");
		}
		name = scanner.readString();
		version = scanner.readShort();
		read:
		while (true) {
			final String clazz;
			int count, ptr = 0;
			final int op = scanner.readByte();
			switch (op) {
			case Headers.ATTRIBUTE:
				final String key = scanner.readString();
				final String value = scanner.readString();
				attributes.put(key, new StringBuilder(value).reverse().toString());
				break;
			case Headers.GET_STATIC:
			case Headers.GET_FIELD:
				clazz = scanner.readString();
				count = scanner.readShort();
				final AddGetterAdapter.Field[] fieldsGet = new AddGetterAdapter.Field[count];
				while (ptr < count) {
					final AddGetterAdapter.Field f = new AddGetterAdapter.Field();
					f.getter_access = scanner.readInt();
					f.getter_name = scanner.readString();
					f.getter_desc = scanner.readString();
					f.owner = scanner.readString();
					f.name = scanner.readString();
					f.desc = scanner.readString();
					f.overflow = scanner.readByte();
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
				adapters.put(clazz, new AddGetterAdapter(delegate(clazz), op == Headers.GET_FIELD, fieldsGet));
				break;
			case Headers.ADD_FIELD:
				clazz = scanner.readString();
				count = scanner.readShort();
				final AddFieldAdapter.Field[] fieldsAdd = new AddFieldAdapter.Field[count];
				while (ptr < count) {
					final AddFieldAdapter.Field f = new AddFieldAdapter.Field();
					f.access = scanner.readInt();
					f.name = scanner.readString();
					f.desc = scanner.readString();
					fieldsAdd[ptr++] = f;
				}
				adapters.put(clazz, new AddFieldAdapter(delegate(clazz), fieldsAdd));
				break;
			case Headers.ADD_METHOD:
				clazz = scanner.readString();
				count = scanner.readShort();
				final AddMethodAdapter.Method[] methods = new AddMethodAdapter.Method[count];
				while (ptr < count) {
					final AddMethodAdapter.Method m = new AddMethodAdapter.Method();
					m.access = scanner.readInt();
					m.name = scanner.readString();
					m.desc = scanner.readString();
					final byte[] code = new byte[scanner.readInt()];
					scanner.readSegment(code, code.length, 0);
					m.code = code;
					m.max_locals = scanner.readByte();
					m.max_stack = scanner.readByte();
					methods[ptr++] = m;
				}
				adapters.put(clazz, new AddMethodAdapter(delegate(clazz), methods));
				break;
			case Headers.ADD_INTERFACE:
				clazz = scanner.readString();
				final String inter = scanner.readString();
				adapters.put(clazz, new AddInterfaceAdapter(delegate(clazz), inter));
				break;
			case Headers.SET_SUPER:
				clazz = scanner.readString();
				final String superName = scanner.readString();
				adapters.put(clazz, new SetSuperAdapter(delegate(clazz), superName));
				break;
			case Headers.SET_SIGNATURE:
				clazz = scanner.readString();
				count = scanner.readShort();
				final SetSignatureAdapter.Signature[] signatures = new SetSignatureAdapter.Signature[count];
				while (ptr < count) {
					final SetSignatureAdapter.Signature s = new SetSignatureAdapter.Signature();
					s.name = scanner.readString();
					s.desc = scanner.readString();
					s.new_access = scanner.readInt();
					s.new_name = scanner.readString();
					s.new_desc = scanner.readString();
					signatures[ptr++] = s;
				}
				adapters.put(clazz, new SetSignatureAdapter(delegate(clazz), signatures));
				break;
			case Headers.INSERT_CODE:
				clazz = scanner.readString();
				final String name = scanner.readString();
				final String desc = scanner.readString();
				count = scanner.readByte();
				final Map<Integer, byte[]> fragments = new HashMap<Integer, byte[]>();
				while (count-- > 0) {
					final int off = scanner.readShort();
					final byte[] code = new byte[scanner.readInt()];
					scanner.readSegment(code, code.length, 0);
					fragments.put(off, code);
				}
				adapters.put(clazz, new InsertCodeAdapter(delegate(clazz), name, desc, fragments, scanner.readByte(), scanner.readByte()));
				break;
			case Headers.OVERRIDE_CLASS:
				final String old_clazz = scanner.readString();
				final String new_clazz = scanner.readString();
				count = scanner.readByte();
				while (count-- > 0) {
					final String current_clazz = scanner.readString();
					adapters.put(current_clazz, new OverrideClassAdapter(delegate(current_clazz), old_clazz, new_clazz));
				}
				break;
			case Headers.CONSTANT:
				constants.put(scanner.readShort(), scanner.readShort());
				break;
			case Headers.MULTIPLIER:
				multipliers.put(scanner.readShort(), scanner.readInt());
				break;
			case Headers.END_OF_FILE:
				break read;
			}
		}
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
