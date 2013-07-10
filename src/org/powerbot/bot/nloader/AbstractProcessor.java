package org.powerbot.bot.nloader;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class AbstractProcessor implements Processor {
	private Transform[] transforms;

	public AbstractProcessor(Transform... transforms) {
		this.transforms = transforms;
	}

	@Override
	public byte[] transform(byte[] b) {
		ClassNode node = getNode(b);
		for (Transform transform : transforms) {
			transform.accept(node);
		}
		return getCode(node);
	}

	private ClassNode getNode(byte[] bytes) {
		ClassNode node = new ClassNode();
		ClassReader reader = new ClassReader(bytes);
		reader.accept(node, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
		return node;
	}

	private byte[] getCode(ClassNode node) {
		ClassWriter writer = new ClassWriter(0);
		node.accept(writer);
		return writer.toByteArray();
	}
}
