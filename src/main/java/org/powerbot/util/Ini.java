package org.powerbot.util;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Ini implements Serializable {
	private static final String DEFAULT = "";
	private static final long serialVersionUID = 2162875213770806139L;
	private transient final Map<String, Member> table;
	private transient final AtomicBoolean empty;

	public Ini() {
		table = new ConcurrentHashMap<>();
		empty = new AtomicBoolean(true);
	}

	public boolean has() {
		return has(DEFAULT);
	}

	private boolean has(final String k) {
		return table.containsKey(k);
	}

	public Member get() {
		return get(DEFAULT);
	}

	public Member get(final String k) {
		final Member m;
		if (table.containsKey(k)) {
			m = table.get(k);
		} else {
			table.put(k, m = new Member());
		}
		return m;
	}

	public Ini put(final Member m) {
		return put(DEFAULT, m);
	}

	private Ini put(final String k, final Member m) {
		table.put(k, m);
		return this;
	}

	public Ini put(final Map<String, String> m) {
		return put(DEFAULT, m);
	}

	private Ini put(final String k, final Map<String, String> m) {
		return put(k, new Member(m));
	}

	public Ini remove() {
		return remove(DEFAULT);
	}

	private Ini remove(final String k) {
		table.remove(k);
		return this;
	}

	public Ini clear() {
		table.clear();
		return this;
	}

	public Ini write(final File f) {
		try (final OutputStream os = new FileOutputStream(f)) {
			write(os);
		} catch (final IOException ignored) {
		}
		return this;
	}

	public Ini write(final OutputStream os) throws IOException {
		final byte[] b;
		try {
			b = this.toString().getBytes("UTF-8");
		} catch (final UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
		os.write(b);
		os.close();
		return this;
	}

	private void writeObject(final ObjectOutputStream out) throws IOException {
		write(out);
	}

	public Ini read(final File f) {
		try (final InputStream is = new FileInputStream(f)) {
			return read(is);
		} catch (final IOException ignored) {
			return this;
		}
	}

	public Ini read(final InputStream is) throws IOException {
		final BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String k = DEFAULT, line;
		Member m;
		if (table.containsKey(k)) {
			m = table.get(k);
		} else {
			table.put(k, m = new Member());
		}

		while ((line = br.readLine()) != null) {
			String l = line.trim();
			if (l.isEmpty()) {
				continue;
			}
			if (l.length() > 1 && l.charAt(0) == '[' && l.charAt(l.length() - 1) == ']') {
				k = l.length() == 2 ? DEFAULT : l.substring(1, l.length() - 1);
				if (table.containsKey(k)) {
					m = table.get(k);
				} else {
					table.put(k, m = new Member());
				}
			} else {
				l = getLine(line, br).trim();
				int z = l.indexOf('=');
				if (z == -1) {
					continue;
				}
				m.put(l.substring(0, z).trim(), ++z == l.length() ? "" : l.substring(z).trim());
			}
		}

		br.close();
		return this;
	}

	private static String getLine(final String line, final BufferedReader br) throws IOException {
		if (line.isEmpty() || line.charAt(line.length() - 1) != '\\') {
			return line;
		}

		final StringBuilder s = new StringBuilder();
		final String lf = System.getProperty("line.separator");

		String l;
		while ((l = br.readLine()) != null) {
			if (l.charAt(l.length() - 1) == '\\') {
				s.append(l).deleteCharAt(s.length() - 1);
			} else {
				s.append(l).append(lf);
				break;
			}
		}

		return s.delete(s.length() - lf.length(), lf.length()).toString();
	}

	private void readObject(final ObjectInputStream in) throws IOException {
		read(in);
	}

	@SuppressWarnings({"EmptyMethod", "RedundantThrows"})
	private void readObjectNoData() throws ObjectStreamException {
	}

	public Set<Map.Entry<String, Member>> entrySet() {
		return table.entrySet();
	}

	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		final boolean r = empty.get();

		if (table.containsKey(DEFAULT)) {
			final String v = table.get(DEFAULT).toString();
			if (!(r && v.isEmpty())) {
				s.append(v);
			}
		}

		for (final Map.Entry<String, Member> e : table.entrySet()) {
			final String k = e.getKey();
			if (k.equals(DEFAULT)) {
				continue;
			}
			final String v = e.getValue().toString();
			if (!(r && v.isEmpty())) {
				s.append("\n[").append(e.getKey()).append("]\n").append(v);
			}
		}

		return s.toString();
	}

	private static boolean parseBoolean(String v) {
		return v != null && !((v = v.trim()).isEmpty() || v.equals("0") || v.equalsIgnoreCase("false") || v.equalsIgnoreCase("off") || v.equalsIgnoreCase("no"));
	}

	public class Member {
		private transient final Map<String, String> values;

		private Member() {
			this(new ConcurrentHashMap<>());
		}

		private Member(final Map<String, String> values) {
			this.values = values;
		}

		public Ini parent() {
			return Ini.this;
		}

		public boolean has(final String k) {
			return values.containsKey(k);
		}

		public String get(final String k) {
			return values.get(k);
		}

		public String get(final String k, final String d) {
			return values.getOrDefault(k, d);
		}

		public boolean getBool(final String k) {
			return getBool(k, false);
		}

		public boolean getBool(final String k, final boolean d) {
			if (!values.containsKey(k)) {
				return d;
			}
			return parseBoolean(values.get(k));
		}

		public int getInt(final String k) {
			return getInt(k, 0);
		}

		public int getInt(final String k, final int d) {
			if (!values.containsKey(k)) {
				return d;
			}
			try {
				return Integer.parseInt(values.get(k));
			} catch (final NumberFormatException ignored) {
				return d;
			}
		}

		public long getLong(final String k) {
			return getLong(k, 0L);
		}

		public long getLong(final String k, final long d) {
			if (!values.containsKey(k)) {
				return d;
			}
			try {
				return Long.parseLong(values.get(k));
			} catch (final NumberFormatException ignored) {
				return d;
			}
		}

		public double getDouble(final String k) {
			return getDouble(k, 0d);
		}

		double getDouble(final String k, final double d) {
			if (!values.containsKey(k)) {
				return d;
			}
			try {
				return Double.parseDouble(values.get(k));
			} catch (final NumberFormatException ignored) {
				return d;
			}
		}

		public Member put(final String k) {
			values.put(k, DEFAULT);
			return this;
		}

		public Member put(final String k, final String v) {
			if (v == null) {
				values.remove(k);
			} else {
				values.put(k, v);
			}
			return this;
		}

		public Member put(final String k, final boolean v) {
			values.put(k, v ? "1" : "0");
			return this;
		}

		public Member put(final String k, final int v) {
			values.put(k, Integer.toString(v));
			return this;
		}

		public Member put(final String k, final long v) {
			values.put(k, Long.toString(v));
			return this;
		}

		public Member put(final String k, final double v) {
			values.put(k, Double.toString(v));
			return this;
		}

		public Member remove(final String k) {
			values.remove(k);
			return this;
		}

		public Map<String, String> getMap() {
			return values;
		}

		@Override
		public String toString() {
			final StringBuilder s = new StringBuilder();
			final boolean r = empty.get();

			for (final Map.Entry<String, String> e : values.entrySet()) {
				String v = e.getValue();
				if (r && (v == null || v.isEmpty() || v.equals("0") || v.equals("0.0"))) {
					continue;
				}
				v = v == null ? "" : v.replace("\n", "\\\n");
				s.append(e.getKey()).append('=').append(v).append("\n");
			}

			return s.toString();
		}
	}
}
