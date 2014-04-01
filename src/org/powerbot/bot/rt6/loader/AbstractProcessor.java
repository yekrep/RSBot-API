package org.powerbot.bot.rt6.loader;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.powerbot.bot.loader.Transformer;

public class AbstractProcessor implements Transformer {
	private final Transform[] transforms;

	public AbstractProcessor(final Transform... transforms) {
		this.transforms = transforms;
	}

	@Override
	public byte[] transform(final byte[] b) {
		final ClassNode node = getNode(b);
		for (final Transform transform : transforms) {
			transform.accept(node);
		}
		return getCode(node);
	}

	private ClassNode getNode(final byte[] bytes) {
		final ClassNode node = new ClassNode();
		final ClassReader reader = new ClassReader(bytes);
		reader.accept(node, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
		return node;
	}

	private byte[] getCode(final ClassNode node) {
		final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		node.accept(writer);
		return writer.toByteArray();
	}
}
