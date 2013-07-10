package org.powerbot.bot.nloader;

import org.objectweb.asm.tree.ClassNode;

public class ClassLoaderTransform implements Transform {
	@Override
	public void accept(ClassNode node) {
		//TODO: implement bytecode-modification
		System.out.println(node.name + " (super " + node.superName + ")");
	}
}
