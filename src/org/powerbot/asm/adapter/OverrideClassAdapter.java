package org.powerbot.asm.adapter;

import org.objectweb.asm.*;

/**
 * @author Huan
 */
public class OverrideClassAdapter extends ClassVisitor {
	private final String old_clazz;
	private final String new_clazz;

	public OverrideClassAdapter(ClassVisitor delegate, String old_clazz, String new_clazz) {
		super(Opcodes.ASM4, delegate);
		this.old_clazz = old_clazz;
		this.new_clazz = new_clazz;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (desc.contains("L" + old_clazz + ";")) {
			desc = desc.replace("L" + old_clazz + ";", "L" + new_clazz + ";");
		}
		return new MethodAdapter(super.visitMethod(access, name, desc, signature, exceptions), old_clazz, new_clazz);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if (desc.equals("L" + old_clazz + ";")) {
			desc = "L" + new_clazz + ";";
		}

		return super.visitField(access, name, desc, signature, value);
	}

	static class MethodAdapter extends MethodVisitor {
		private final String old_clazz;
		private final String new_clazz;

		MethodAdapter(
				MethodVisitor delegate,
				String old_clazz,
				String new_clazz) {
			super(Opcodes.ASM4, delegate);
			this.old_clazz = old_clazz;
			this.new_clazz = new_clazz;
		}

		@Override
		public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		}

		@Override
		public void visitTypeInsn(int opcode, String type) {
			if (type.equals(old_clazz)) {
				type = new_clazz;
			}
			super.visitTypeInsn(opcode, type);
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			if (desc.contains("L" + old_clazz + ";")) {
				desc = desc.replace("L" + old_clazz + ";", "L" + new_clazz + ";");
			}
			super.visitFieldInsn(opcode, owner, name, desc);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc) {
			if (owner.equals(old_clazz)) {
				owner = new_clazz;
			}
			if (desc.contains("L" + old_clazz + ";")) {
				desc = desc.replace("L" + old_clazz + ";", "L" + new_clazz + ";");
			}
			super.visitMethodInsn(opcode, owner, name, desc);
		}

		@Override
		public void visitLineNumber(int line, Label start) {
		}
	}
}