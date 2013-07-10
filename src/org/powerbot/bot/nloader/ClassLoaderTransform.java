package org.powerbot.bot.nloader;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassLoaderTransform implements Transform {
	private final String super_;

	public ClassLoaderTransform() {
		this.super_ = ClassLoader.class.getName().replace('.', '/');
	}

	@Override
	public void accept(ClassNode node) {
		String super_ = node.superName;
		if (super_ == null || !super_.equals(this.super_)) {
			return;
		}
		final int[] ops = {
				Opcodes.AALOAD, Opcodes.AALOAD, Opcodes.ILOAD, Opcodes.ILOAD, Opcodes.AALOAD,
				Opcodes.INVOKEVIRTUAL
		};
		for (MethodNode method : node.methods) {
			InsnSearcher searcher = new InsnSearcher(method);
			while (searcher.getNext(ops) != null) {
				AbstractInsnNode abstractInsnNode = searcher.current();
				MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
				if (methodInsnNode.name.equals("defineClass")) {
					System.out.println("Found defineClass" + methodInsnNode.desc);
				}
			}
			//TODO find defineClass invoke and inject before it
		}
	}
}
