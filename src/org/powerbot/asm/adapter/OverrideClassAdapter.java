package org.powerbot.asm.adapter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author Qauters
 */
public class OverrideClassAdapter extends ClassVisitor {
	private String old_clazz;
	private String new_clazz;

	public OverrideClassAdapter(ClassVisitor delegate, String old_clazz, String new_clazz) {
		super(Opcodes.ASM4, delegate);
		this.old_clazz = old_clazz;
		this.new_clazz = new_clazz;
	}

	@Override
	public MethodVisitor visitMethod(
			int access,
			String name,
			String desc,
			String signature,
			String[] exceptions) {
		return new ClassAdapter(super.visitMethod(access, name, desc, signature, exceptions), old_clazz, new_clazz);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if (desc.equals("L" + old_clazz + ";")) {
			desc = "L" + new_clazz + ";";
		}
		return super.visitField(access, name, desc, signature, value);
	}

	static class ClassAdapter extends MethodVisitor {
		private String old_clazz;
		private String new_clazz;

		ClassAdapter(
				MethodVisitor delegate,
				String old_clazz,
				String new_clazz) {
			super(Opcodes.ASM4, delegate);
			this.old_clazz = old_clazz;
			this.new_clazz = new_clazz;
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
			if (desc.contains(old_clazz)) {
				desc = desc.replace("L" + old_clazz + ";", "L" + new_clazz + ";");
			}
			super.visitFieldInsn(opcode, owner, name, desc);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc) {
			if (owner.equals(old_clazz)) {
				owner = new_clazz;
				desc = desc.replace("L" + old_clazz + ";", "L" + new_clazz + ";");
			}
			super.visitMethodInsn(opcode, owner, name, desc);
		}
	}
}