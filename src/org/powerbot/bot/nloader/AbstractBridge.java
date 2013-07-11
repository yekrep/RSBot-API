package org.powerbot.bot.nloader;

import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class AbstractBridge implements Bridge {
	public final List<String> classes;

	public AbstractBridge() {
		this.classes = new LinkedList<>();
	}

	@Override
	public byte[] onDefine(byte[] bytes) {
		ClassNode node = new ClassNode();
		ClassReader reader = new ClassReader(bytes);
		reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
		return bytes;
	}

	@Override
	public void notifyClass(String name) {
		name = name.replace('/', '.');//not necessary but to be safe
		if (!classes.contains(name)) {
			classes.add(name);
		}
	}
}
