package org.powerbot.bot.nloader;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassLoaderTransform implements Transform {

	/**
	 * TARGET: defineClass
	 * class extends ClassLoader
	 * aload_0
	 * [
	 * aload (String name)
	 * aload (byte[] b)
	 * iload (int off)
	 * iload (int len)
	 * aload (ProtectionDomain protectionDomain)
	 * ]
	 * invokevirtual class/defineClass(Ljava/lang/String;[BIILjava/security/ProtectionDomain;)Ljava/lang/Class;
	 * pop
	 */
	@Override
	public void accept(ClassNode node) {
		//TODO: implement bytecode-modification
		System.out.println(node.name + " (super " + node.superName + ")");
		if (node.superName != null && node.superName.equals(ClassLoader.class.getName().replace('.', '/'))) {
			System.out.println("Found our class!");
			System.out.println("Methods dump: ");
			for (MethodNode methodNode : node.methods) {
				System.out.println(methodNode.name + methodNode.desc);
				ListIterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
				while (iterator.hasNext()) {
					AbstractInsnNode abstractInsnNode = iterator.next();
					if (abstractInsnNode instanceof MethodInsnNode) {
						MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
						if (methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL &&
								methodInsnNode.name.equals("defineClass")) {
							System.out.println("=====");
							System.out.println(methodInsnNode.name + methodInsnNode.desc);
							System.out.println("=====");
							break;
						}
					}
				}
			}
		}
	}
}
