package org.powerbot.asm.adapter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class AddInterfaceAdapter extends ClassVisitor {
	private String inter;

	public AddInterfaceAdapter(ClassVisitor delegate, String inter) {
		super(Opcodes.ASM4, delegate);
		this.inter = inter;
	}

	@Override
	public void visit(
			int version,
			int access,
			String name,
			String signature,
			String superName,
			String[] interfaces) {
		String[] inters = new String[interfaces.length + 1];
		System.arraycopy(interfaces, 0, inters, 0, interfaces.length);
		inters[interfaces.length] = inter;
		super.visit(version, access, name, signature, superName, inters);
	}
}
