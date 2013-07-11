package org.powerbot.bot.nloader;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ListClassesTransform implements Transform {
	private AppletTransform parent;

	public ListClassesTransform(AppletTransform parent) {
		this.parent = parent;
	}

	@Override
	public void accept(ClassNode node) {
		final String methodName = "put";
		final String desc = "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
		final int[] ops = {
				Opcodes.ALOAD, Opcodes.ALOAD,
				Opcodes.INVOKEVIRTUAL,
				Opcodes.POP
		};
		for (MethodNode method : node.methods) {
			if (!method.desc.contains("[B")) {
				continue;
			}
			InsnSearcher searcher = new InsnSearcher(method);
			while (searcher.getNext(ops) != null) {
				AbstractInsnNode abstractInsnNode = searcher.getPrevious();
				MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
				if (methodInsnNode.name.equals(methodName) &&
						methodInsnNode.desc.equals(desc)) {
					method.instructions.insert(searcher.current(), createCallback(searcher.getPrevious().getPrevious()));
				}
			}
		}
	}

	private InsnList createCallback(AbstractInsnNode byteLoad) {
		InsnList insnList = new InsnList();
		if (!(byteLoad instanceof VarInsnNode)) {
			throw new RuntimeException();
		}
		int var = ((VarInsnNode) byteLoad).var;
		insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, parent.getIdentified(), "accessor", "L" + Bridge.class.getName().replace('.', '/') + ";"));
		insnList.add(new VarInsnNode(Opcodes.ALOAD, var));
		insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Bridge.class.getName().replace('.', '/'), "notifyClass", "(Ljava/lang/String;)V"));
		return insnList;
	}
}
