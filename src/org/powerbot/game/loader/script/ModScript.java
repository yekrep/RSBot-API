package org.powerbot.game.loader.script;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.powerbot.asm.NodeProcessor;
import org.powerbot.asm.adapter.*;
import org.powerbot.lang.AdaptException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ModScript implements NodeProcessor {
	private Map<String, ClassVisitor> adapters;
	private Map<String, ClassWriter> writers;
	public Map<String, String> attributes;
	public Map<Integer, Integer> constants;
	public Map<Integer, Integer> multipliers;
	private Scanner scanner;
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
	}

	private static final int MAGIC = 0xFADFAD;

	public ModScript(byte[] data) {
		this(new ByteArrayInputStream(data));
	}

	public ModScript(InputStream data) {
		this();
		this.scanner = new Scanner(data);
	}

	public ModScript() {
		this.adapters = new HashMap<String, ClassVisitor>();
		this.writers = new HashMap<String, ClassWriter>();
		this.attributes = new HashMap<String, String>();
		this.constants = new HashMap<Integer, Integer>();
		this.multipliers = new HashMap<Integer, Integer>();
	}

	public String getName() {
		return name;
	}

	public int getVersion() {
		return version;
	}

	public void adapt() throws AdaptException {
		if (scanner.readInt() != ModScript.MAGIC) {
			throw new AdaptException("invalid patch format");
		}
		name = scanner.readString();
		version = scanner.readShort();
		int num = scanner.readShort();
		while (num-- > 0) {
			String clazz;
			int count, ptr = 0;
			int op = scanner.readByte();
			switch (op) {
				case Headers.ATTRIBUTE:
					String key = scanner.readString();
					String value = scanner.readString();
					attributes.put(key, new StringBuilder(value).reverse().toString());
					break;
				case Headers.GET_STATIC:
				case Headers.GET_FIELD:
					clazz = scanner.readString();
					count = scanner.readShort();
					AddGetterAdapter.Field[] fieldsGet = new AddGetterAdapter.Field[count];
					while (ptr < count) {
						AddGetterAdapter.Field f = new AddGetterAdapter.Field();
						f.getter_access = scanner.readInt();
						f.getter_name = scanner.readString();
						f.getter_desc = scanner.readString();
						f.owner = scanner.readString();
						f.name = scanner.readString();
						f.desc = scanner.readString();
						fieldsGet[ptr++] = f;
						//System.out.println(f.getter_access + " " + f.getter_name + " " + f.getter_desc + " " + f.owner + " " + f.name + " " + f.desc);
					}
					//adapters.put(clazz, new AddGetterAdapter(delegate(clazz), op == Headers.GET_FIELD, fieldsGet));
					break;
				case Headers.ADD_FIELD:
					clazz = scanner.readString();
					count = scanner.readShort();
					AddFieldAdapter.Field[] fieldsAdd = new AddFieldAdapter.Field[count];
					while (ptr < count) {
						AddFieldAdapter.Field f = new AddFieldAdapter.Field();
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
					AddMethodAdapter.Method[] methods = new AddMethodAdapter.Method[count];
					while (ptr < count) {
						AddMethodAdapter.Method m = new AddMethodAdapter.Method();
						m.access = scanner.readInt();
						m.name = scanner.readString();
						m.desc = scanner.readString();
						byte[] code = new byte[scanner.readInt()];
						scanner.readSegment(code, code.length, 0);
						m.code = code;
						m.max_locals = scanner.readByte();
						m.max_stack = scanner.readByte();
						methods[ptr++] = m;
					}
					//adapters.put(clazz, new AddMethodAdapter(delegate(clazz), methods));
					break;
				case Headers.ADD_INTERFACE:
					clazz = scanner.readString();
					String inter = scanner.readString();
					adapters.put(clazz, new AddInterfaceAdapter(delegate(clazz), inter));
					break;
				case Headers.SET_SUPER:
					clazz = scanner.readString();
					String superName = scanner.readString();
					adapters.put(clazz, new SetSuperAdapter(delegate(clazz), superName));
					break;
				case Headers.SET_SIGNATURE:
					clazz = scanner.readString();
					count = scanner.readShort();
					SetSignatureAdapter.Signature[] signatures = new SetSignatureAdapter.Signature[count];
					while (ptr < count) {
						SetSignatureAdapter.Signature s = new SetSignatureAdapter.Signature();
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
					String name = scanner.readString();
					String desc = scanner.readString();
					count = scanner.readByte();
					Map<Integer, byte[]> fragments = new HashMap<Integer, byte[]>();
					while (count-- > 0) {
						int off = scanner.readShort();
						//System.out.println("off: " + off);
						byte[] code = new byte[scanner.readInt()];
						scanner.readSegment(code, code.length, 0);
						fragments.put(off, code);
					}
					scanner.readByte();
					scanner.readByte();
					//System.out.println(clazz + " " + name + " " + desc);
					//adapters.put(clazz, new InsertCodeAdapter(delegate(clazz), name, desc, fragments, scanner.readByte(), scanner.readByte()));
					break;
				case Headers.OVERRIDE_CLASS:
					String old_clazz = scanner.readString();
					String new_clazz = scanner.readString();
					count = scanner.readByte();
					while (count-- > 0) {
						String current_clazz = scanner.readString();
						//adapters.put(current_clazz, new OverrideClassAdapter(delegate(current_clazz), old_clazz, new_clazz));
					}
					break;
				case Headers.CONSTANT:
					constants.put(scanner.readShort(), scanner.readShort());
					break;
				case Headers.MULTIPLIER:
					multipliers.put(scanner.readShort(), scanner.readInt());
					break;
			}
		}
	}

	public byte[] process(String name, byte[] data) {
		ClassReader reader = new ClassReader(data);
		ClassVisitor adapter = adapters.get(name);
		if (adapter != null) {
			reader.accept(adapter, ClassReader.SKIP_FRAMES);
			return writers.get(name).toByteArray();
		}
		return data;
	}

	private ClassVisitor delegate(String clazz) {
		ClassVisitor delegate = adapters.get(clazz);
		if (delegate == null) {
			ClassWriter writer = new ClassWriter(0);
			writers.put(clazz, writer);
			return writer;
		}
		return delegate;
	}
}
