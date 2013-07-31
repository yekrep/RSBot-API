package org.powerbot.bot.loader.bytecode;

import org.objectweb.asm.tree.ClassNode;

public interface Transform {
	public void accept(ClassNode node);
}
