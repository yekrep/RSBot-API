package org.powerbot.bot.loader;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.powerbot.bot.loader.transform.TransformSpec;

public abstract class AbstractBridge implements Bridge {
	public final List<String> entries;
	public final Map<String, byte[]> loaded;
	private final TransformSpec transformSpec;
	private ClassLoader loader;

	public AbstractBridge(final TransformSpec transformSpec) {
		this.entries = Collections.synchronizedList(new LinkedList<String>());
		this.loaded = new ConcurrentHashMap<String, byte[]>();
		this.transformSpec = transformSpec;
	}

	@Override
	public void classLoader(final ClassLoader loader) {
		this.loader = loader;
	}

	@Override
	public byte[] classDefined(final byte[] bytes) {
		if (transformSpec != null) {
			return transformSpec.process(bytes);
		}

		final ClassNode node = new ClassNode();
		final ClassReader reader = new ClassReader(bytes);
		reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
		loaded.put(node.name, bytes);
		return bytes;
	}

	@Override
	public void entry(String name) {
		if (transformSpec != null) {
			return;
		}
		name = name.replace('/', '.');
		if (!entries.contains(name)) {
			entries.add(name);
		}
	}

	@Override
	public void end() {
		if (transformSpec != null || loader == null) {
			return;
		}

		for (final String entry : entries) {
			try {
				loader.loadClass(entry);
			} catch (ClassNotFoundException ignored) {
			}
		}
	}

	public TransformSpec getTransformSpec() {
		return transformSpec;
	}
}
