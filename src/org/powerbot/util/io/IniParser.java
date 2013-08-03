package org.powerbot.util.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author Paris
 */
public class IniParser {
	private static final String NEWLINE = System.getProperty("line.separator");
	private static final char SECTIONOPEN = '[';
	private static final char SECTIONCLOSE = ']';
	private static final char KEYBOUND = '=';
	private static final char ESCAPE = '\\';
	private static final char COMMENTS = ';';
	public static final String EMPTYSECTION = "";

	private IniParser() {
	}

	public static void serialise(final Map<String, Map<String, String>> data, final File out) throws IOException {
		final BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		serialise(data, bw);
		bw.close();
	}

	public static void serialise(final Map<String, Map<String, String>> data, final OutputStream os) throws IOException {
		final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
		serialise(data, bw);
		bw.close();
	}

	public static void serialise(final Map<String, Map<String, String>> data, final Writer out) throws IOException {
		int c = 0;
		if (data.containsKey(EMPTYSECTION)) {
			writeSection(EMPTYSECTION, data.get(EMPTYSECTION), out);
			c++;
		}
		for (final Entry<String, Map<String, String>> entry : data.entrySet()) {
			if (c != 0) {
				out.write(NEWLINE);
			}
			final String section = entry.getKey();
			if (section.equals(EMPTYSECTION)) {
				continue;
			}
			writeSection(section, entry.getValue(), out);
			c++;
		}
	}

	private static void writeSection(final String section, final Map<String, String> map, final Writer out) throws IOException {
		if (!(section == null || section.isEmpty())) {
			out.write(SECTIONOPEN);
			out.write(section);
			out.write(SECTIONCLOSE);
			out.write(NEWLINE);
		}
		for (final Entry<String, String> entry : map.entrySet()) {
			out.write(entry.getKey());
			out.write(KEYBOUND);
			final String value = entry.getValue();
			if (value != null) {
				out.write(value.replace(new String(new char[]{ESCAPE}), new String(new char[]{ESCAPE, ESCAPE})).replaceAll("(\r?\n)", "\\$1"));
			}
			out.write(NEWLINE);
		}
	}

	public static Map<String, Map<String, String>> deserialise(final File file) throws IOException {
		return deserialise(new BufferedReader(new FileReader(file)));
	}

	public static Map<String, Map<String, String>> deserialise(final InputStream is) throws IOException {
		return deserialise(new BufferedReader(new InputStreamReader(is)));
	}

	public static Map<String, Map<String, String>> deserialise(final BufferedReader br) throws IOException {
		final Map<String, Map<String, String>> data = new HashMap<>();
		deserialise(br, data, new TreeMap<String, String>());
		return data;
	}

	@SuppressWarnings("unchecked")
	public static void deserialise(final BufferedReader br, final Map<String, Map<String, String>> data, final Map<String, String> keys) throws IOException {
		String line, section = EMPTYSECTION, key = "";
		boolean multiline = false;

		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (line.length() > 0 && line.charAt(0) == COMMENTS) {
				continue;
			}
			if (multiline) {
				final String prev = data.get(section).get(key);
				data.get(section).put(key, prev.substring(0, prev.length() - 2) + NEWLINE + line);
				multiline = line.lastIndexOf(ESCAPE) == line.length() - 1 && (line.length() > 2 ? line.charAt(line.length() - 2) != ESCAPE : true);
				continue;
			}
			if (line.isEmpty()) {
				continue;
			}
			int z;
			final int l = line.length();
			if (line.charAt(0) == SECTIONOPEN) {
				z = line.indexOf(SECTIONCLOSE, 1);
				z = z == -1 ? l : z;
				section = z == 1 ? "" : line.substring(1, z).trim();
			} else {
				z = line.indexOf(KEYBOUND);
				z = z == -1 ? l : z;
				String value = "";
				key = line.substring(0, z).trim();
				if (++z < l) {
					value = line.substring(z).trim();
				}
				multiline = value.length() > 0 && value.lastIndexOf(ESCAPE) == value.length() - 1 && (value.length() > 2 ? value.charAt(value.length() - 2) != ESCAPE : true);
				if (!data.containsKey(section)) {
					Map<String, String> map = null;
					try {
						final Method method = keys.getClass().getMethod("clone");
						if (method != null) {
							map = (Map<String, String>) method.invoke(data);
							map.clear();
						}
					} catch (final Exception ignored) {
					}
					if (map == null) {
						map = new HashMap<>();
					}
					data.put(section, map);
				}
				data.get(section).put(key, value);
			}
		}

		br.close();
	}

	public static boolean parseBool(final String mode) {
		return mode.equals("1") || mode.equalsIgnoreCase("true") || mode.equalsIgnoreCase("yes");
	}
}
