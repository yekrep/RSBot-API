package org.powerbot.asm.adapter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AddGetterAdapter extends ClassVisitor implements Opcodes {
	public static class Field {
		public int getter_access;
		public String getter_name;
		public String getter_desc;
		public String owner;
		public String name;
		public String desc;
	}

	private boolean virtual;
	private Field[] fields;
	private String owner;

	public AddGetterAdapter(ClassVisitor delegate, boolean virtual, Field[] fields) {
		super(Opcodes.ASM4, delegate);
		this.virtual = virtual;
		this.fields = fields;
	}

	@Override
	public void visit(
			int version,
			int access,
			String name,
			String signature,
			String superName,
			String[] interfaces) {
		owner = name;
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public void visitEnd() {
		for (Field f : fields) {
			visitGetter(f.getter_access, f.getter_name, f.getter_desc, virtual ? null : f.owner, f.name, f.desc);
		}
		super.visitEnd();
	}

	private void visitGetter(
			int getter_access,
			String getter_name,
			String getter_desc,
			String owner,
			String name,
			String desc) {
		MethodVisitor mv = super.visitMethod(getter_access, getter_name, getter_desc, null, null);
		mv.visitCode();
		if (owner == null) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, this.owner, name, desc);
		} else {
			mv.visitFieldInsn(GETSTATIC, owner, name, desc);
		}
		int op = getReturnOpcode(desc);
		mv.visitInsn(op);
		mv.visitMaxs(op == LRETURN || op == DRETURN ? 2 : 1, (getter_access & ACC_STATIC) == 0 ? 1 : 0);
		mv.visitEnd();
	}

	private int getReturnOpcode(String desc) {
		desc = desc.substring(desc.indexOf(")") + 1);
		if (desc.length() > 1) {
			return ARETURN;
		}
		char c = desc.charAt(0);
		switch (c) {
			case 'I':
			case 'Z':
			case 'B':
			case 'S':
			case 'C':
				return IRETURN;
			case 'J':
				return LRETURN;
			case 'F':
				return FRETURN;
			case 'D':
				return DRETURN;
		}
		throw new RuntimeException("bad_return");
	}
}
