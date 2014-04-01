package org.powerbot.bot.rt6.loader;

import java.applet.Applet;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class AppletTransform implements Transform {
	private final String super_;
	private String identified;

	public AppletTransform() {
		this.super_ = Applet.class.getName().replace('.', '/');
		this.identified = null;
	}

	@Override
	public void accept(final ClassNode node) {
		final String super_ = node.superName;
		if (super_ == null || !super_.equals(this.super_)) {
			return;
		}
		identified = node.name;
		/*
		* Add application interface.
		* This is for casting the loaded applet to what we need.
		*/
		node.interfaces.add(Application.class.getName().replace('.', '/'));
		/*
		* Create static accessor field for the bridge.
		 */
		node.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "accessor", "L" + Bridge.class.getName().replace('.', '/') + ";", null, null);
		/*
		* Create set bridge method.
		* Write basic code to set field.
		 */
		final MethodVisitor mv = node.visitMethod(
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

		final int[] ops = {
				Opcodes.ALOAD,
				Opcodes.ALOAD, Opcodes.ACONST_NULL, Opcodes.CHECKCAST, Opcodes.INVOKEVIRTUAL,
				Opcodes.PUTFIELD
		};
		final String methodOwner = "java/lang/reflect/Constructor";
		final String methodName = "newInstance";
		final String desc = "([Ljava/lang/Object;)Ljava/lang/Object;";
		for (final MethodNode method : node.methods) {
			final InsnSearcher searcher = new InsnSearcher(method);
			while (searcher.getNext(ops) != null) {
				final FieldInsnNode fieldInsnNode = (FieldInsnNode) searcher.current();
				final MethodInsnNode methodInsnNode = (MethodInsnNode) fieldInsnNode.getPrevious();
				if (methodInsnNode.owner.equals(methodOwner) &&
						methodInsnNode.name.equals(methodName) &&
						methodInsnNode.desc.equals(desc)) {
					searcher.getPrevious(Opcodes.ALOAD);
					searcher.getPrevious(Opcodes.ALOAD);
					final int var = ((VarInsnNode) searcher.current()).var;
					final InsnList insnList = new InsnList();
					insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, identified, "accessor", "L" + Bridge.class.getName().replace('.', '/') + ";"));
					insnList.add(new VarInsnNode(Opcodes.ALOAD, var));
					insnList.add(new FieldInsnNode(Opcodes.GETFIELD, fieldInsnNode.owner, fieldInsnNode.name, fieldInsnNode.desc));
					insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Bridge.class.getName().replace('.', '/'), "instance", "(Ljava/lang/Object;)V"));
					method.instructions.insert(fieldInsnNode, insnList);
				}
			}
		}
	}

	public String getIdentified() {
		return identified;
	}
}
