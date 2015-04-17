package org.powerbot.bot.rt6.loader;

import org.objectweb.asm.tree.ClassNode;

public interface Transform {
	void accept(ClassNode node);
}
