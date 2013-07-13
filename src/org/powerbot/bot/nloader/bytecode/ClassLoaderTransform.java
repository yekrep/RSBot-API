package org.powerbot.bot.nloader.bytecode;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.powerbot.bot.nloader.Bridge;

public class ClassLoaderTransform implements Transform {
	private final String super_;
	private AppletTransform parent;

	public ClassLoaderTransform(AppletTransform parent) {
		this.super_ = ClassLoader.class.getName().replace('.', '/');
		this.parent = parent;
	}

	@Override
	public void accept(ClassNode node) {
		String super_ = node.superName;
		if (super_ == null || !super_.equals(this.super_)) {
			return;
		}
		final int[] ops = {
				Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ILOAD, Opcodes.ILOAD, Opcodes.ALOAD,
				Opcodes.INVOKEVIRTUAL
		};
		final String methodName = "defineClass";
		final String desc = "(Ljava/lang/String;[BIILjava/security/ProtectionDomain;)Ljava/lang/Class;";
		for (MethodNode method : node.methods) {
			/*
			* Invoke the classLoader callback when a new class loader is created.
			* This is required to acquire the class loader.
			 */
			if (method.name.equals("<init>")) {
				InsnList insnList = new InsnList();
				insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, parent.getIdentified(), "accessor", "L" + Bridge.class.getName().replace('.', '/') + ";"));
				insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
				insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Bridge.class.getName().replace('.', '/'), "classLoader", "(Ljava/lang/ClassLoader;)V"));
				method.instructions.insertBefore(method.instructions.getLast(), insnList);
				continue;
			}

			InsnSearcher searcher = new InsnSearcher(method);
			while (searcher.getNext(ops) != null) {
				AbstractInsnNode abstractInsnNode = searcher.current();
				MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
				if (methodInsnNode.name.equals(methodName) &&
						methodInsnNode.desc.equals(desc)) {
					/*
					* Found a define class invoke.
					* Rewind to before the first instruction.
					* Store position.
					 */
					for (int i = 0; i < ops.length; i++) {
						searcher.getPrevious();
					}
					AbstractInsnNode pos = searcher.current();
					/*
					* Fast-forward to the byte array load.
					 */
					AbstractInsnNode load = searcher.getNext().getNext();
					/*
					* Change the byte array before the call.
					* defineClass(name, bytes, pos, len, domain)
					* --->
					* bytes = classDefined(bytes);
					* defineClass(name, bytes, pos, len, domain)
					 */
					InsnList insnList = new InsnList();
					int var = ((VarInsnNode) load).var;
					insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, parent.getIdentified(), "accessor", "L" + Bridge.class.getName().replace('.', '/') + ";"));
					insnList.add(new VarInsnNode(Opcodes.ALOAD, var));
					insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Bridge.class.getName().replace('.', '/'), "classDefined", "([B)[B"));
					insnList.add(new VarInsnNode(Opcodes.ASTORE, var));
					method.instructions.insert(pos, insnList);
				}
			}
		}
	}
}