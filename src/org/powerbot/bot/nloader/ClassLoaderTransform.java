package org.powerbot.bot.nloader;

import org.objectweb.asm.tree.ClassNode;

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
	}
}
