package org.powerbot.bot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TransformSpec {
	public final Map<String, Map<String, Reflector.Field>> fields;
	public final Map<String, String> attributes;
	public final Map<Integer, Integer> constants;
	public final Map<Integer, Integer> multipliers;
	private final Scanner scanner;
	private String name;
	private int version;

	public static interface Headers {
		int ATTRIBUTE = 1;
		int GET_STATIC = 2;
		int GET_FIELD = 3;
		int ADD_FIELD = 4;
		int ADD_METHOD = 5;
		int ADD_INTERFACE = 6;
		int SET_SUPER = 7;
		int SET_SIGNATURE = 8;
		int INSERT_CODE = 9;
		int OVERRIDE_CLASS = 10;
		int CONSTANT = 11;
		int MULTIPLIER = 12;
		int END_OF_FILE = 13;
	}

	private static final int MAGIC = 0xFADFAD;

	public TransformSpec(final InputStream data) {
		scanner = new Scanner(data);
		fields = new HashMap<String, Map<String, Reflector.Field>>();
		attributes = new HashMap<String, String>();
		constants = new HashMap<Integer, Integer>();
		multipliers = new HashMap<Integer, Integer>();
	}

	public String getName() {
		return name;
	}

	public int getVersion() {
		return version;
	}

	public void parse() {
		if (scanner.readInt() != TransformSpec.MAGIC) {
			throw new RuntimeException("invalid patch format");
		}
		name = scanner.readString();
		version = scanner.readShort();
		read:
		while (true) {
			final int op = scanner.readByte();
			switch (op) {
			case Headers.ATTRIBUTE: {
				final String k = scanner.readString(), v = new StringBuilder(scanner.readString()).reverse().toString();
				attributes.put(k, v);
				break;
			}
			case Headers.GET_STATIC:
			case Headers.GET_FIELD: {
				final String c = scanner.readString();
				final Map<String, Reflector.Field> map;
				if (fields.containsKey(c)) {
					map = fields.get(c);
				} else {
					fields.put(c, map = new HashMap<String, Reflector.Field>());
				}
				int n = scanner.readShort();
				while (n-- > 0) {
					final int ga = scanner.readInt();
					final String gn = scanner.readString(), gd = scanner.readString();
					final String o = scanner.readString(), f = scanner.readString(), d = scanner.readString();
					final byte overflow = (byte) scanner.readByte();//1 = int, 2 = long, default = 0
					final long value;
					switch (overflow) {
					case 1: {
						value = scanner.readInt();
						break;
					}
					case 2: {
						value = scanner.readLong();
						break;
					}
					default: {
						value = 0;
						break;
					}
					}
					if (map.containsKey(gn)) {
						throw new RuntimeException("we don't support overloading yet...");
					}
					map.put(gn, new Reflector.Field(o, f, op == Headers.GET_FIELD, overflow, value));
				}
				break;
			}
			case Headers.ADD_FIELD: {
				final String c = scanner.readString();
				int n = scanner.readShort();
				while (n-- > 0) {
					final int a = scanner.readInt();
					final String f = scanner.readString(), d = scanner.readString();
				}
				break;
			}
			case Headers.ADD_METHOD: {
				final String c = scanner.readString();
				int n = scanner.readShort();
				while (n-- > 0) {
					final int a = scanner.readInt();
					final String m = scanner.readString(), d = scanner.readString();
					final byte[] arr = new byte[scanner.readInt()];
					scanner.readSegment(arr, arr.length, 0);
					final int l = scanner.readByte(), s = scanner.readByte();
				}
				break;
			}
			case Headers.ADD_INTERFACE: {
				final String c = scanner.readString(), s = scanner.readString();
				break;
			}
			case Headers.SET_SUPER: {
				final String c = scanner.readString(), s = scanner.readString();
				break;
			}
			case Headers.SET_SIGNATURE: {
				final String c = scanner.readString();
				int n = scanner.readShort();
				while (n-- > 0) {
					final String m = scanner.readString(), d = scanner.readString();
					final int a = scanner.readInt();
					final String m2 = scanner.readString(), d2 = scanner.readString();
				}
				break;
			}
			case Headers.INSERT_CODE: {
				final String c = scanner.readString();
				final String m = scanner.readString(), d = scanner.readString();
				int n = scanner.readByte();
				while (n-- > 0) {
					final int o = scanner.readShort();
					final byte[] arr = new byte[scanner.readInt()];
					scanner.readSegment(arr, arr.length, 0);
				}
				break;
			}
			case Headers.OVERRIDE_CLASS: {
				final String c1 = scanner.readString(), c2 = scanner.readString();
				int n = scanner.readByte();
				while (n-- > 0) {
					final String c = scanner.readString();
				}
			}
			case Headers.CONSTANT: {
				constants.put(scanner.readShort(), scanner.readShort());
				break;
			}
			case Headers.MULTIPLIER: {
				multipliers.put(scanner.readShort(), scanner.readInt());
				break;
			}
			case Headers.END_OF_FILE: {
				break read;
			}
			}
		}
	}

	private static class Scanner {
		private final static int EOL = 0xA;
		private final InputStream in;

		public Scanner(final byte[] data) {
			this(new ByteArrayInputStream(data));
		}

		public Scanner(final InputStream in) {
			this.in = in;
		}

		public int readByte() {
			try {
				return in.read();
			} catch (final IOException ignored) {
				return -1;
			}
		}

		public int readShort() {
			return readByte() << 8 | readByte();
		}

		public int readInt() {
			return readShort() << 16 | readShort();
		}

		public long readLong() {
			return ((long) readInt()) << 32 | readInt() & 0xFFFFFFFFl;
		}

		public String readString() {
			return normalize(new String(readSegment()));
		}

		public byte[] readSegment() {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			int b;
			while ((b = readByte()) != EOL && b != -1) {
				out.write(b);
			}
			return out.toByteArray();
		}

		public void readSegment(final byte[] data, final int len, final int off) {
			for (int i = off; i < off + len; i++) {
				data[i] = (byte) readByte();
			}
		}

		private String normalize(final String s) {
			return s.replace("org/powerbot/game/client", org.powerbot.bot.rt6.client.Client.class.getPackage().getName().replace('.', '/')).
					replace("org/powerbot/os/client", org.powerbot.bot.rt4.client.Client.class.getPackage().getName().replace('.', '/'));
		}
	}
}
