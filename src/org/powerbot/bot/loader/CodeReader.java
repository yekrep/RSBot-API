package org.powerbot.bot.loader;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.powerbot.bot.loader.TransformSpec.readString;

class CodeReader {
	interface Opcodes {
		int INSN = 1;
		int INT_INSN = 2;
		int VAR_INSN = 3;
		int TYPE_INSN = 4;
		int FIELD_INSN = 5;
		int METHOD_INSN = 6;
		int JUMP_INSN = 7;
		int LDC_INSN = 8;
		int IINC_INSN = 9;
		int TABLESWITCH_INSN = 10;
		int LOOKUPSWITCH_INSN = 11;
		int MULTIANEWARRAY_INSN = 12;
		int TRY_CATCH_BLOCK = 13;
		int LOCAL_VARIABLE = 14;
		int LABEL = 15;
	}

	private CodeReader() {
	}

	public static void accept(final byte[] code, final MethodVisitor v) {
		DataInputStream in = null;
		try {
			accept(in = new DataInputStream(new ByteArrayInputStream(code)), v);
		} catch (final IOException ignored) {
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (final IOException ignored) {
				}
			}
		}
	}

	public static void accept(final DataInputStream code, final MethodVisitor v) throws IOException {
		int len = code.readUnsignedShort();
		final Label[] labels = new Label[code.readUnsignedByte()];
		for (int i = 0, l = labels.length; i < l; ++i) {
			labels[i] = new Label();
		}
		while (len-- > 0) {
			final Label dflt;
			final Label[] lbls;
			int n, ptr = 0;
			switch (code.readUnsignedByte()) {
			case Opcodes.INSN:
				v.visitInsn(code.readUnsignedByte());
				break;
			case Opcodes.INT_INSN:
				v.visitIntInsn(code.readUnsignedByte(), code.readUnsignedShort());
				break;
			case Opcodes.VAR_INSN:
				v.visitVarInsn(code.readUnsignedByte(), code.readUnsignedByte());
				break;
			case Opcodes.TYPE_INSN:
				v.visitTypeInsn(code.readUnsignedByte(), readString(code));
				break;
			case Opcodes.FIELD_INSN:
				v.visitFieldInsn(code.readUnsignedByte(), readString(code), readString(code), readString(code));
				break;
			case Opcodes.METHOD_INSN:
				v.visitMethodInsn(code.readUnsignedByte(), readString(code), readString(code), readString(code));
				break;
			case Opcodes.JUMP_INSN:
				v.visitJumpInsn(code.readUnsignedByte(), labels[code.readUnsignedByte()]);
				break;
			case Opcodes.LDC_INSN:
				final int type = code.readUnsignedByte();
				switch (type) {
				case 1:
					v.visitLdcInsn(code.readInt());
					break;
				case 2:
					v.visitLdcInsn(Float.parseFloat(readString(code)));
					break;
				case 3:
					v.visitLdcInsn(code.readLong());
					break;
				case 4:
					v.visitLdcInsn(Double.parseDouble(readString(code)));
					break;
				case 5:
					v.visitLdcInsn(readString(code));
					break;
				case 6:
					v.visitLdcInsn(Type.getType(readString(code)));
					break;
				}
				break;
			case Opcodes.IINC_INSN:
				v.visitIincInsn(code.readUnsignedByte(), code.readByte());
				break;
			case Opcodes.TABLESWITCH_INSN:
				final int min = code.readUnsignedShort();
				final int max = code.readUnsignedShort();
				dflt = labels[code.readUnsignedByte()];
				n = code.readUnsignedByte();
				lbls = new Label[n];
				while (ptr < n) {
					lbls[ptr++] = labels[code.readUnsignedByte()];
				}
				v.visitTableSwitchInsn(min, max, dflt, lbls);
				break;
			case Opcodes.LOOKUPSWITCH_INSN:
				dflt = labels[code.readUnsignedByte()];
				n = code.readUnsignedByte();
				final int[] keys = new int[n];
				while (ptr < n) {
					keys[ptr++] = code.readUnsignedShort();
				}
				n = code.readUnsignedByte();
				ptr = 0;
				lbls = new Label[n];
				while (ptr < n) {
					lbls[ptr++] = labels[code.readUnsignedByte()];
				}
				v.visitLookupSwitchInsn(dflt, keys, lbls);
				break;
			case Opcodes.MULTIANEWARRAY_INSN:
				v.visitMultiANewArrayInsn(readString(code), code.readUnsignedByte());
				break;
			case Opcodes.TRY_CATCH_BLOCK:
				v.visitTryCatchBlock(labels[code.readUnsignedByte()], labels[code.readUnsignedByte()], labels[code.readUnsignedByte()], readString(code));
				break;
			case Opcodes.LOCAL_VARIABLE:
				v.visitLocalVariable(readString(code), readString(code), readString(code), labels[code.readUnsignedByte()], labels[code.readUnsignedByte()], code.readUnsignedByte());
				break;
			case Opcodes.LABEL:
				v.visitLabel(labels[code.readUnsignedByte()]);
				break;
			}
		}
	}
}
