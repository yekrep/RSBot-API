package org.powerbot.bot;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.powerbot.util.IOUtils;

public class ReflectorSpec {
	public final Map<String, String> interfaces;
	public final Map<String, Reflector.FieldConfig> configs;
	public final Map<String, Long> constants;

	private ReflectorSpec() {
		interfaces = new HashMap<String, String>();
		configs = new HashMap<String, Reflector.FieldConfig>();
		constants = new HashMap<String, Long>();
	}

	public static ReflectorSpec parse(final InputStream in) {
		final ReflectorSpec r = new ReflectorSpec();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(IOUtils.read(in))));
			String line;

			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.indexOf('#') == 0) {
					continue;
				}

				final int z0 = line.indexOf(':'), z1 = line.indexOf('=', z0 == -1 ? 0 : z0);
				if (z0 == -1 || z1 == -1) {
					continue;
				}

				final String type = line.substring(0, z0), name = z0 + 1 == z1 ? "" : line.substring(z0 + 1, z1);
				final String[] args = z1 + 1 == line.length() ? new String[]{} : line.substring(z1 + 1).split(",");
				if (type.equals("field")) {
					final long m = args.length > 3 ? Long.parseLong(args[3]) : 1L;
					r.configs.put(name, new Reflector.FieldConfig(args[0], args[1], args[2], m));
				} else if (type.equals("interface")) {
					r.interfaces.put(name, args[0]);
				} else if (type.equals("constant")) {
					if (args.length == 2) {
						if (args[1].equalsIgnoreCase("I") || args[1].equalsIgnoreCase("J")) {
							r.constants.put(name, Long.parseLong(args[0]));
						}
					}
				}
			}
		} catch (final IOException ignored) {
		} finally {
			final Closeable c = br == null ? in : br;
			if (c != null) {
				try {
					c.close();
				} catch (final IOException ignored) {
				}
			}
		}

		return r;
	}
}
