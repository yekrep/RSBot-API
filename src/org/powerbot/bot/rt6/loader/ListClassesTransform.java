package org.powerbot.bot.rt6.loader;

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

public class ListClassesTransform implements Transform {
	private final AppletTransform parent;

	public ListClassesTransform(final AppletTransform parent) {
		this.parent = parent;
	}

	@Override
	public void accept(final ClassNode node) {
		String methodName = "put";
		String desc = "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
		int[] ops = {
				Opcodes.ALOAD, Opcodes.ALOAD,
				Opcodes.INVOKEVIRTUAL,
				Opcodes.POP
		};
		for (final MethodNode method : node.methods) {
			final InsnSearcher searcher = new InsnSearcher(method);
			while (searcher.getNext(ops) != null) {
				final AbstractInsnNode abstractInsnNode = searcher.getPrevious();
				final MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
				if (methodInsnNode.name.equals(methodName) &&
						methodInsnNode.desc.equals(desc)) {
					/*
					* Found a put method invoke.
					* Inject a notify of the key.
					 */
					final InsnList insnList = new InsnList();
					final int v = ((VarInsnNode) searcher.getPrevious()).var;
					/*
					* Verify the value is of a byte array (the correct put).
					 */
					insnList.add(new VarInsnNode(Opcodes.ALOAD, v));
					insnList.add(new TypeInsnNode(Opcodes.INSTANCEOF, "[B"));
					final LabelNode label = new LabelNode();
					insnList.add(new JumpInsnNode(Opcodes.IFEQ, label));
					final int n = ((VarInsnNode) searcher.getPrevious()).var;
					/*
					* Notify of a new entry in the map if the value is of a byte array.
					 */
					insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, parent.getIdentified(), "accessor", "L" + Bridge.class.getName().replace('.', '/') + ";"));
					insnList.add(new VarInsnNode(Opcodes.ALOAD, n));
					insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Bridge.class.getName().replace('.', '/'), "entry", "(Ljava/lang/String;)V"));
					insnList.add(label);
					method.instructions.insert(searcher.current(), insnList);
				}
			}
		}

		methodName = "getNextJarEntry";
		desc = "()Ljava/util/jar/JarEntry;";
		ops = new int[]{
				Opcodes.INVOKEVIRTUAL,
				Opcodes.DUP,
				Opcodes.ASTORE,
		};
		for (final MethodNode method : node.methods) {
			final InsnSearcher searcher = new InsnSearcher(method);
			while (searcher.getNext(ops) != null) {
				final int branch = searcher.getNext().getOpcode();
				if (branch != Opcodes.IFNULL &&
						branch != Opcodes.IF_ACMPEQ) {
					continue;
				}

				final AbstractInsnNode abstractInsnNode = searcher.getPrevious(Opcodes.INVOKEVIRTUAL);
				final MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
				if (methodInsnNode.name.equals(methodName) &&
						methodInsnNode.desc.equals(desc)) {
					/*
					* Found the getNextJarEntry invoke.
					* Retrieve the jump position if the entry is null (no more entries).
					 */
					final JumpInsnNode jump = (JumpInsnNode) searcher.getNext(branch);
					final AbstractInsnNode pos = jump.getPrevious();
					final LabelNode label = jump.label;
					/*
					* Remove the jump to the label if the entry is null.
					 */
					method.instructions.remove(jump);
					final InsnList insnList = new InsnList();
					final LabelNode skip = new LabelNode();
					/*
					* Now restore functionality, if entry is not null, continue on as normal.
					* If entry is null, GOTO the IFNULL label.
					* This allows us to add more intermediate code (notify EOJ).
					 */
					insnList.add(new JumpInsnNode(branch == Opcodes.IFNULL ? Opcodes.IFNONNULL : Opcodes.IF_ACMPNE, skip));
					insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, parent.getIdentified(), "accessor", "L" + Bridge.class.getName().replace('.', '/') + ";"));
					insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Bridge.class.getName().replace('.', '/'), "end", "()V"));
					insnList.add(new JumpInsnNode(Opcodes.GOTO, label));
					insnList.add(skip);
					method.instructions.insert(pos, insnList);
				}
			}
		}
	}
}
