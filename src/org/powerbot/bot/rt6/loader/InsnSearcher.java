package org.powerbot.bot.rt6.loader;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

/**
 */
class InsnSearcher {
	private final AbstractInsnNode first;
	private AbstractInsnNode curr;

	public InsnSearcher(final MethodNode node) {
		this(node.instructions);
	}

	private InsnSearcher(final InsnList list) {
		this(list.getFirst());
	}

	private InsnSearcher(final AbstractInsnNode first) {
		this.first = first;
		this.curr = first;
	}

	public AbstractInsnNode current() {
		return curr;
	}

	void set(final AbstractInsnNode curr) {
		this.curr = curr;
	}

	public void reset() {
		curr = first;
	}

	public AbstractInsnNode getNext() {
		curr = curr.getNext();
		while (curr != null && curr.getOpcode() == -1) {
			curr = curr.getNext();
		}
		return curr;
	}

	public AbstractInsnNode getPrevious() {
		curr = curr.getPrevious();
		while (curr != null && curr.getOpcode() == -1) {
			curr = curr.getPrevious();
		}
		return curr;
	}

	public AbstractInsnNode getNext(final int opcode) {
		AbstractInsnNode node;
		for (; ; ) {
			node = getNext();
			if (node == null || node.getOpcode() == opcode) {
				break;
			}
		}
		return node;
	}

	public AbstractInsnNode getPrevious(final int opcode) {
		AbstractInsnNode node;
		for (; ; ) {
			node = getPrevious();
			if (node == null || node.getOpcode() == opcode) {
				break;
			}
		}
		return node;
	}

	public AbstractInsnNode getNext(final int[] opcodes) {
		if (opcodes.length < 1) {
			return null;
		}
		AbstractInsnNode curr;
		AbstractInsnNode node;
		for (; ; ) {
			node = getNext();
			if (node == null) {
				break;
			}
			if (node.getOpcode() == opcodes[0]) {
				curr = current();
				AbstractInsnNode secondary;
				for (int i = 1; i < opcodes.length; i++) {
					secondary = getNext();
					if (secondary == null || (secondary.getOpcode() != opcodes[i] && opcodes[i] != -1)) {
						break;
					}
					if (i == opcodes.length - 1) {
						return secondary;
					}
				}
				set(curr);
			}
		}
		return node;
	}
}
