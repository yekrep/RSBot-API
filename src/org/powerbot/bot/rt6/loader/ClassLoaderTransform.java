package org.powerbot.bot.rt6.loader;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ClassLoaderTransform implements Transform {
	private final String super_;
	private final AppletTransform parent;

	public ClassLoaderTransform(final AppletTransform parent) {
		this.super_ = ClassLoader.class.getName().replace('.', '/');
		this.parent = parent;
	}

	@Override
	public void accept(final ClassNode node) {
		final String super_ = node.superName;
		if (super_ == null || !super_.equals(this.super_)) {
			return;
		}
		final int[] ops = {
				Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ILOAD, Opcodes.ILOAD, Opcodes.ALOAD,
				Opcodes.INVOKEVIRTUAL
		};
		final String methodName = "defineClass";
		final String desc = "(Ljava/lang/String;[BIILjava/security/ProtectionDomain;)Ljava/lang/Class;";
		for (final MethodNode method : node.methods) {
			/*
			* Invoke the classLoader callback when a new class loader is created.
			* This is required to acquire the class loader.
			 */
			if (method.name.equals("<init>")) {
				final InsnList insnList = new InsnList();
				insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, parent.getIdentified(), "accessor", "L" + Bridge.class.getName().replace('.', '/') + ";"));
				insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
				insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Bridge.class.getName().replace('.', '/'), "classLoader", "(Ljava/lang/ClassLoader;)V"));
				method.instructions.insertBefore(method.instructions.getLast(), insnList);
				continue;
			}

			final InsnSearcher searcher = new InsnSearcher(method);
			while (searcher.getNext(ops) != null) {
				final AbstractInsnNode abstractInsnNode = searcher.current();
				final MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
				if (methodInsnNode.name.equals(methodName) &&
						methodInsnNode.desc.equals(desc)) {
					/*
					* Found a define class invoke.
					* Rewind to before the first instruction.
					* Store position.
					 */
					for (final int ignored : ops) {
						searcher.getPrevious();
					}
					final AbstractInsnNode pos = searcher.current();
					/*
					* Fast-forward to the byte array load.
					 */
					final AbstractInsnNode load = searcher.getNext().getNext();
					final AbstractInsnNode off = load.getNext();
					final AbstractInsnNode len = off.getNext();
					/*
					* Change the byte array before the call.
					* defineClass(name, bytes, pos, len, domain)
					* --->
					* bytes = classDefined(bytes);
					* defineClass(name, bytes, pos, bytes.length, domain)
					 */
					final InsnList insnList = new InsnList();
					final int var = ((VarInsnNode) load).var;
					insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, parent.getIdentified(), "accessor", "L" + Bridge.class.getName().replace('.', '/') + ";"));
					insnList.add(new VarInsnNode(Opcodes.ALOAD, var));
					insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Bridge.class.getName().replace('.', '/'), "classDefined", "([B)[B"));
					insnList.add(new VarInsnNode(Opcodes.ASTORE, var));
					method.instructions.insert(pos, insnList);

					insnList.clear();
					method.instructions.remove(len);
					insnList.add(new VarInsnNode(Opcodes.ALOAD, var));
					insnList.add(new InsnNode(Opcodes.ARRAYLENGTH));
					method.instructions.insert(off, insnList);
				}
			}
		}
	}
}