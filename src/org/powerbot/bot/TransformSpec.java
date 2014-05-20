package org.powerbot.bot;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TransformSpec {
	public final Map<String, Map<String, Reflector.Field>> fields;
	public final Map<String, String> attributes;
	public final Map<Integer, Integer> constants;
	public final Map<Integer, Integer> multipliers;
	public String name;
	public int version;

	private static final int MAGIC = 0xFADFAD, ATTRIBUTE = 1, GET_STATIC = 2, GET_FIELD = 3, ADD_FIELD = 4, ADD_METHOD = 5, ADD_INTERFACE = 6, SET_SUPER = 7, SET_SIGNATURE = 8, INSERT_CODE = 9, OVERRIDE_CLASS = 10, CONSTANT = 11, MULTIPLIER = 12, END_OF_FILE = 13;

	private TransformSpec(final String name, final int version) {
		this.name = name;
		this.version = version;
		fields = new HashMap<String, Map<String, Reflector.Field>>();
		attributes = new HashMap<String, String>();
		constants = new HashMap<Integer, Integer>();
		multipliers = new HashMap<Integer, Integer>();
	}

	public static TransformSpec read(final InputStream in) throws IOException {
		final ScannerInputStream scanner = new ScannerInputStream(in);

		if (scanner.readInt() != MAGIC) {
			scanner.close();
			throw new IOException("invalid patch format");
		}

		final TransformSpec t = new TransformSpec(scanner.readString(), scanner.readShort());

		while (scanner.available() > 0) {
			final int op = scanner.readByte();
			switch (op) {
			case ATTRIBUTE: {
				final String k = scanner.readString(), v = new StringBuilder(scanner.readString()).reverse().toString();
				t.attributes.put(k, v);
				break;
			}
			case GET_STATIC:
			case GET_FIELD: {
				final String c = scanner.readString();
				final Map<String, Reflector.Field> map;
				if (t.fields.containsKey(c)) {
					map = t.fields.get(c);
				} else {
					t.fields.put(c, map = new HashMap<String, Reflector.Field>());
				}
				int n = scanner.readShort();
				while (n-- > 0) {
					final int ga = scanner.readInt();
					final String gn = scanner.readString(), gd = scanner.readString();
					final String o = scanner.readString(), f = scanner.readString(), d = scanner.readString();
					final byte overflow = scanner.readByte();//1 = int, 2 = long, default = 0
					final long value;
					switch (overflow) {
					case 1:
						value = scanner.readInt();
						break;
					case 2:
						value = scanner.readLong();
						break;
					default:
						value = 0;
						break;
					}
					if (map.containsKey(gn)) {
						throw new IOException("we don't support overloading yet...");
					}
					map.put(gn, new Reflector.Field(o, f, op == GET_FIELD, overflow, value));
				}
				break;
			}
			case ADD_FIELD: {
				final String c = scanner.readString();
				int n = scanner.readShort();
				while (n-- > 0) {
					final int a = scanner.readInt();
					final String f = scanner.readString(), d = scanner.readString();
				}
				break;
			}
			case ADD_METHOD: {
				final String c = scanner.readString();
				int n = scanner.readShort();
				while (n-- > 0) {
					final int a = scanner.readInt();
					final String m = scanner.readString(), d = scanner.readString();
					final byte[] arr = new byte[scanner.readInt()];
					scanner.read(arr);
					final int l = scanner.readByte(), s = scanner.readByte();
				}
				break;
			}
			case ADD_INTERFACE: {
				final String c = scanner.readString(), s = scanner.readString();
				break;
			}
			case SET_SUPER: {
				final String c = scanner.readString(), s = scanner.readString();
				break;
			}
			case SET_SIGNATURE: {
				final String c = scanner.readString();
				int n = scanner.readShort();
				while (n-- > 0) {
					final String m = scanner.readString(), d = scanner.readString();
					final int a = scanner.readInt();
					final String m2 = scanner.readString(), d2 = scanner.readString();
				}
				break;
			}
			case INSERT_CODE: {
				final String c = scanner.readString();
				final String m = scanner.readString(), d = scanner.readString();
				int n = scanner.readByte();
				while (n-- > 0) {
					final int o = scanner.readShort();
					final byte[] arr = new byte[scanner.readInt()];
					scanner.read(arr);
				}
				break;
			}
			case OVERRIDE_CLASS: {
				final String c1 = scanner.readString(), c2 = scanner.readString();
				int n = scanner.readByte();
				while (n-- > 0) {
					final String c = scanner.readString();
				}
			}
			case CONSTANT:
				t.constants.put((int) scanner.readShort(), (int) scanner.readShort());
				break;
			case MULTIPLIER:
				t.multipliers.put((int) scanner.readShort(), scanner.readInt());
				break;
			case END_OF_FILE:
				scanner.close();
				break;
			}
		}

		return t;
	}

	@Override
	public String toString() {
		return name + "-" + Integer.toString(version);
	}

	private static final class ScannerInputStream extends DataInputStream {
		private final static int EOL = 0xA;

		public ScannerInputStream(final InputStream in) {
			super(in);
		}

		public byte[] readSegment() throws IOException {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final byte[] b = new byte[1];
			int l;
			while ((l = in.read(b)) > 0 && b[0] != EOL) {
				out.write(b, 0, l);
			}
			return out.toByteArray();
		}

		public String readString() throws IOException {
			return normalize(new String(readSegment()));
		}

		private static String normalize(final String s) {
			return s.replace("org/powerbot/game/client", org.powerbot.bot.rt6.client.Client.class.getPackage().getName().replace('.', '/')).
					replace("org/powerbot/os/client", org.powerbot.bot.rt4.client.Client.class.getPackage().getName().replace('.', '/'));
		}
	}
}
