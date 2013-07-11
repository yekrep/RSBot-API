package org.powerbot.bot.nloader.bytecode;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.powerbot.bot.nloader.Bridge;

public class ClassLoaderTransform implements Transform {
	private final String super_;
	private AppletTransform parent;

	public ClassLoaderTransform(AppletTransform parent) {
		this.super_ = ClassLoader.class.getName().replace('.', '/');
		this.parent = parent;
	}

	@Override
	public void accept(ClassNode node) {
		String super_ = node.superName;
		if (super_ == null || !super_.equals(this.super_)) {
			return;
		}
		final int[] ops = {
				Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ILOAD, Opcodes.ILOAD, Opcodes.ALOAD,
				Opcodes.INVOKEVIRTUAL
		};
		final String methodName = "defineClass";
		final String desc = "(Ljava/lang/String;[BIILjava/security/ProtectionDomain;)Ljava/lang/Class;";
		for (MethodNode method : node.methods) {
			InsnSearcher searcher = new InsnSearcher(method);
			while (searcher.getNext(ops) != null) {
				AbstractInsnNode abstractInsnNode = searcher.current();
				MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
				if (methodInsnNode.name.equals(methodName) &&
						methodInsnNode.desc.equals(desc)) {
					for (int i = 0; i < ops.length; i++) {
						searcher.getPrevious();
					}
					method.instructions.insert(searcher.current(), createCallback(searcher.getNext().getNext()));
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
		insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Bridge.class.getName().replace('.', '/'), "onDefine", "([B)[B"));
		insnList.add(new VarInsnNode(Opcodes.ASTORE, var));
		return insnList;
	}
}