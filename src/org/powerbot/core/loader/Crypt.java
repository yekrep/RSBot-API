package org.powerbot.core.loader;

/**
 * @author Timer
 */
public class Crypt {
	private static final int[] KEYS = {
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0x3e, 0x3e, -1, 0x3f,
			-1, 0x3f, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b, 0x3c,
			0x3d, -1, -1, -1, -1, -1, -1, -1, 0x0, 0x1, 0x2, 0x3, 0x4, 0x5,
			0x6, 0x7, 0x8, 0x9, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf, 0x10, 0x11,
			0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, -1, -1, -1, -1,
			-1, -1, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f, 0x20, 0x21, 0x22,
			0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x2b, 0x2c,
			0x2d, 0x2e, 0x2f, 0x30, 0x31, 0x32, 0x33, -1, -1, -1, -1, -1,
	};

	public static byte[] decode(final String data) {
		final int assembly_length = assembledLengthOf(data);
		return assembly_length == 0 ? new byte[0] : assemble(assembly_length, data);
	}

	private static int assembledLengthOf(final String data) {
		final int data_length = data.length();
		if (data_length == 0) {
			return 0;
		}

		final int padding_length = -4 & data_length + 3;
		int assembled_length = padding_length / 4 * 3;
		if (data_length <= padding_length - 2 || keyAt(data.charAt(padding_length - 2)) == -1) {
			assembled_length -= 2;
		} else if (data_length <= padding_length - 1 || keyAt(data.charAt(padding_length - 1)) == -1) {
			--assembled_length;
		}

		return assembled_length;
	}

	private static int keyAt(final char index) {
		return index >= 0 && index < KEYS.length ? KEYS[index] : -1;
	}

	private static byte[] assemble(final int result_length, final String data) {
		final byte[] bytes = new byte[result_length];

		final int data_length = data.length();
		int step = 0, index = 0;
		while (true) {
			if (data_length > step) {
				final int header = keyAt(data.charAt(step));

				final int key_1 = data_length > step + 1 ? keyAt(data.charAt(step + 1)) : -1;
				final int key_2 = step + 2 < data_length ? keyAt(data.charAt(step + 2)) : -1;
				final int key_3 = data_length > step + 3 ? keyAt(data.charAt(step + 3)) : -1;

				bytes[index++] = (byte) (key_1 >>> 4 | header << 2);
				if (key_2 != -1) {
					bytes[index++] = (byte) (key_1 << 4 & 0xf0 | key_2 >>> 2);
					if (key_3 != -1) {
						bytes[index++] = (byte) (0xc0 & key_2 << 6 | key_3);
						step += 4;
						continue;
					}
				}
			}

			break;
		}

		return bytes;
	}
}
