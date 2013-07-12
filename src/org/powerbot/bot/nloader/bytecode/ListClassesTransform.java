package org.powerbot.bot.nloader.bytecode;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.powerbot.bot.nloader.Bridge;

public class ListClassesTransform implements Transform {
	private AppletTransform parent;

	public ListClassesTransform(AppletTransform parent) {
		this.parent = parent;
	}

	@Override
	public void accept(ClassNode node) {
		String methodName = "put";
		String desc = "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
		int[] ops = {
				Opcodes.ALOAD, Opcodes.ALOAD,
				Opcodes.INVOKEVIRTUAL,
				Opcodes.POP
		};
		for (MethodNode method : node.methods) {
			InsnSearcher searcher = new InsnSearcher(method);
			while (searcher.getNext(ops) != null) {
				AbstractInsnNode abstractInsnNode = searcher.getPrevious();
				MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
				if (methodInsnNode.name.equals(methodName) &&
						methodInsnNode.desc.equals(desc)) {
					AbstractInsnNode second = searcher.getPrevious();
					AbstractInsnNode first = searcher.getPrevious();
					method.instructions.insert(searcher.current(), createCallback(first, second));
				}
			}
		}

		methodName = "getNextJarEntry";
		desc = "()Ljava/util/jar/JarEntry;";
		ops = new int[]{
				Opcodes.INVOKEVIRTUAL,
				Opcodes.DUP,
				Opcodes.ASTORE,
				Opcodes.IFNULL
		};
		for (MethodNode method : node.methods) {
			InsnSearcher searcher = new InsnSearcher(method);
			while (searcher.getNext(ops) != null) {
				AbstractInsnNode abstractInsnNode = searcher.getPrevious(Opcodes.INVOKEVIRTUAL);
				MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
				if (methodInsnNode.name.equals(methodName) &&
						methodInsnNode.desc.equals(desc)) {
					JumpInsnNode jump = (JumpInsnNode) searcher.getNext(Opcodes.IFNULL);
					AbstractInsnNode pos = jump.getPrevious();
					LabelNode label = jump.label;
					method.instructions.remove(jump);
					method.instructions.insert(pos, createEnd(label));
				}
			}
		}
	}

	private InsnList createCallback(AbstractInsnNode name, AbstractInsnNode value) {
		InsnList insnList = new InsnList();
		if (!(name instanceof VarInsnNode) ||
				!(value instanceof VarInsnNode)) {
			throw new RuntimeException();
		}
		int v = ((VarInsnNode) value).var;
		insnList.add(new VarInsnNode(Opcodes.ALOAD, v));
		insnList.add(new TypeInsnNode(Opcodes.INSTANCEOF, "[B"));
		LabelNode label = new LabelNode();
		insnList.add(new JumpInsnNode(Opcodes.IFEQ, label));
		int n = ((VarInsnNode) name).var;
		insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, parent.getIdentified(), "accessor", "L" + Bridge.class.getName().replace('.', '/') + ";"));
		insnList.add(new VarInsnNode(Opcodes.ALOAD, n));
		insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Bridge.class.getName().replace('.', '/'), "entry", "(Ljava/lang/String;)V"));
		insnList.add(label);
		return insnList;
	}

	private InsnList createEnd(LabelNode label) {
		InsnList insnList = new InsnList();
		LabelNode skip = new LabelNode();
		insnList.add(new JumpInsnNode(Opcodes.IFNONNULL, skip));
		insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, parent.getIdentified(), "accessor", "L" + Bridge.class.getName().replace('.', '/') + ";"));
		insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Bridge.class.getName().replace('.', '/'), "end", "()V"));
		insnList.add(new JumpInsnNode(Opcodes.GOTO, label));
		insnList.add(skip);
		return insnList;
	}
}
