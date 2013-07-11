package org.powerbot.bot.nloader;

import java.applet.Applet;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class AppletTransform implements Transform {
	private final String super_;
	private String identified;

	public AppletTransform() {
		this.super_ = Applet.class.getName().replace('.', '/');
		this.identified = null;
	}

	@Override
	public void accept(ClassNode node) {
		String super_ = node.superName;
		if (super_ == null || !super_.equals(this.super_)) {
			return;
		}
		identified = node.name;
		node.interfaces.add(InjectedInterface.class.getName().replace('.', '/'));
		node.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "accessor", "L" + Bridge.class.getName().replace('.', '/') + ";", null, null);
		MethodVisitor mv = node.visitMethod(
				Opcodes.ACC_PUBLIC,
				"setBridge", "(L" + Bridge.class.getName().replace('.', '/') + ";)V",
				null, null
		);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitFieldInsn(Opcodes.PUTSTATIC, node.name, "accessor", "L" + Bridge.class.getName().replace('.', '/') + ";");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	public String getIdentified() {
		return identified;
	}
}
