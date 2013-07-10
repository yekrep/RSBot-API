package org.powerbot.bot.nloader;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

/**
 * @author Timer
 */
public class InsnSearcher {
	private final AbstractInsnNode first;
	private AbstractInsnNode curr;

	public InsnSearcher(InsnList list) {
		this(list.getFirst());
	}

	public InsnSearcher(AbstractInsnNode first) {
		this.first = first;
		this.curr = first;
	}

	public AbstractInsnNode current() {
		return curr;
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
	}

	public AbstractInsnNode getNext(int opcode) {
		AbstractInsnNode node;
		for (; ; ) {
			node = getNext();
			if (node == null || node.getOpcode() == opcode) {
				break;
			}
		}
		return node;
	}

	public AbstractInsnNode getPrevious(int opcode) {
		AbstractInsnNode node;
		for (; ; ) {
			node = getPrevious();
			if (node == null || node.getOpcode() == opcode) {
				break;
			}
		}
		return node;
	}
}
