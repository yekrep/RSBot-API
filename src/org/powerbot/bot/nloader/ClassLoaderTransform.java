package org.powerbot.bot.nloader;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

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
		System.out.println(node.name + " " + node.superName);
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
		insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
		insnList.add(new TypeInsnNode(Opcodes.NEW, "java/lang/StringBuilder"));
		insnList.add(new InsnNode(Opcodes.DUP));
		insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V"));
		insnList.add(new LdcInsnNode("Class bytes defined: "));
		insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
		insnList.add(new VarInsnNode(Opcodes.ALOAD, ((VarInsnNode) byteLoad).var));
		insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Arrays", "toString", "([B)Ljava/lang/String;"));
		insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
		insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;"));
		insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));
		return insnList;
	}
}