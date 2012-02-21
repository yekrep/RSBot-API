package org.powerbot.asm.adapter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class SetSuperAdapter extends ClassVisitor {
	private String superName;
	private String newSuperName;

	public SetSuperAdapter(ClassVisitor delegate, String superName) {
		super(Opcodes.ASM4, delegate);
		newSuperName = superName;
	}

	@Override
	public void visit(
			int version,
			int access,
			String name,
			String signature,
			String superName,
			String[] interfaces) {
		this.superName = superName;
		super.visit(version, access, name, signature, newSuperName, interfaces);
	}

	@Override
	public MethodVisitor visitMethod(
			int access,
			String name,
			String desc,
			String signature,
			String[] exceptions) {
		if (name.equals("<init>")) {
			return new MethodVisitor(Opcodes.ASM4, super.visitMethod(access, name, desc, signature, exceptions)) {
				@Override
				public void visitMethodInsn(int opcode, String owner, String name, String desc) {
					if (opcode == Opcodes.INVOKESPECIAL && owner.equals(superName)) {
						owner = newSuperName;
					}
					super.visitMethodInsn(opcode, owner, name, desc);
				}
			};
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
	}
}
