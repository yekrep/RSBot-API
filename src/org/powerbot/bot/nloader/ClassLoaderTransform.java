package org.powerbot.bot.nloader;

import org.objectweb.asm.tree.ClassNode;
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
		for (MethodNode method : node.methods) {
			//TODO find defineClass invoke and inject before it
		}
	}
}
