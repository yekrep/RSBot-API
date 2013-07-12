package org.powerbot.bot.nloader;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class AbstractBridge implements Bridge {
	public final List<String> entries;
	public Map<String, byte[]> loaded;

	public AbstractBridge() {
		this.entries = Collections.synchronizedList(new LinkedList<String>());
		this.loaded = null;
	}

	@Override
	public byte[] classDefined(byte[] bytes) {
		ClassNode node = new ClassNode();
		ClassReader reader = new ClassReader(bytes);
		reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
		if (loaded != null) {
			loaded.put(node.name, bytes);
		}
		return bytes;
	}

	@Override
	public void entry(String name) {
		name = name.replace('/', '.');//not necessary but to be safe
		if (!entries.contains(name)) {
			entries.add(name);
		}
	}
}
