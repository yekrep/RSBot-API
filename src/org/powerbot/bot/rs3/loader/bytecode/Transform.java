package org.powerbot.bot.rs3.loader.bytecode;

import org.objectweb.asm.tree.ClassNode;

public interface Transform {
	public void accept(ClassNode node);
}
