package org.powerbot.asm.adapter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.powerbot.game.loader.script.CodeReader;

import java.util.Map;

public class InsertCodeAdapter extends ClassVisitor {
	private String method_name;
	private String method_desc;
	private Map<Integer, byte[]> fragments;
	private int max_locals;
	private int max_stack;

	public InsertCodeAdapter(
			ClassVisitor delegate,
			String method_name,
			String method_desc,
			Map<Integer, byte[]> fragments,
			int max_locals,
			int max_stack) {
		super(Opcodes.ASM4, delegate);
		this.method_name = method_name;
		this.method_desc = method_desc;
		this.fragments = fragments;
		this.max_locals = max_locals;
		this.max_stack = max_stack;
	}

	@Override
	public MethodVisitor visitMethod(
			int access,
			String name,
			String desc,
			String signature,
			String[] exceptions) {
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		if (name.equals(method_name) && desc.equals(method_desc)) {
			return new FragmentVisitor(mv, fragments, max_locals, max_stack);
		}
		return mv;
	}

	private static class FragmentVisitor extends MethodVisitor {
		private FragmentVisitor(
				MethodVisitor delegate,
				Map<Integer, byte[]> fragments,
				int max_locals,
				int max_stack) {
			super(Opcodes.ASM4, delegate);
			this.fragments = fragments;
			this.max_locals = max_locals;
			this.max_stack = max_stack;
		}

		private Map<Integer, byte[]> fragments;
		private int max_locals;
		private int max_stack;

		private int idx = 0;

		@Override
		public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		}

		@Override
		public void visitInsn(int opcode) {
			checkFragments();
			super.visitInsn(opcode);
		}

		@Override
		public void visitIntInsn(int opcode, int operand) {
			checkFragments();
			super.visitIntInsn(opcode, operand);
		}

		@Override
		public void visitVarInsn(int opcode, int var) {
			checkFragments();
			super.visitVarInsn(opcode, var);
		}

		@Override
		public void visitTypeInsn(int opcode, String type) {
			checkFragments();
			super.visitTypeInsn(opcode, type);
		}

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			checkFragments();
			super.visitFieldInsn(opcode, owner, name, desc);
		}

		@Override
		public void visitMethodInsn(int opcode, String owner, String name, String desc) {
			checkFragments();
			super.visitMethodInsn(opcode, owner, name, desc);
		}

		@Override
		public void visitJumpInsn(int opcode, Label label) {
			checkFragments();
			super.visitJumpInsn(opcode, label);
		}

		@Override
		public void visitLabel(Label label) {
			checkFragments();
			super.visitLabel(label);
		}

		@Override
		public void visitLdcInsn(Object cst) {
			checkFragments();
			super.visitLdcInsn(cst);
		}

		@Override
		public void visitIincInsn(int var, int increment) {
			checkFragments();
			super.visitIincInsn(var, increment);
		}

		@Override
		public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
			checkFragments();
			super.visitTableSwitchInsn(min, max, dflt, labels);
		}

		@Override
		public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
			checkFragments();
			super.visitLookupSwitchInsn(dflt, keys, labels);
		}

		@Override
		public void visitMultiANewArrayInsn(String desc, int dims) {
			checkFragments();
			super.visitMultiANewArrayInsn(desc, dims);
		}

		@Override
		public void visitLineNumber(int line, Label start) {
		}

		@Override
		public void visitMaxs(int maxStack, int maxLocals) {
			if (max_stack == -1) {
				super.visitMaxs(maxStack, maxLocals);
			} else {
				super.visitMaxs(max_stack, max_locals);
			}
		}

		private void checkFragments() {
			if (fragments.containsKey(++idx)) {
				new CodeReader(fragments.get(idx)).accept(mv);
			}
		}
	}
}