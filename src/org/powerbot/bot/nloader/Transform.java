package org.powerbot.bot.nloader;

import org.objectweb.asm.tree.ClassNode;

public interface Transform {
	public void accept(ClassNode node);
}
