package org.powerbot.bot.nloader;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class AbstractBridge implements Bridge {
	public final List<String> entries;
	public final Map<String, byte[]> loaded;
	private ClassLoader loader;

	public AbstractBridge() {
		this.entries = Collections.synchronizedList(new LinkedList<String>());
		this.loaded = new ConcurrentHashMap<>();
	}

	@Override
	public void classLoader(ClassLoader loader) {
		this.loader = loader;
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
		name = name.replace('.', '/');
		if (!entries.contains(name)) {
			entries.add(name);
		}
	}

	@Override
	public void end() {
		//TODO: all entries have been loaded
	}
}
