package org.powerbot.bot.nloader;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class AbstractBridge implements Bridge {
	public final List<String> classes;
	public final Map<String, byte[]> loaded;

	public AbstractBridge() {
		this.classes = Collections.synchronizedList(new LinkedList<String>());
		this.loaded = new ConcurrentHashMap<>();
	}

	@Override
	public byte[] onDefine(byte[] bytes) {
		ClassNode node = new ClassNode();
		ClassReader reader = new ClassReader(bytes);
		reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
		loaded.put(node.name, bytes);
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
