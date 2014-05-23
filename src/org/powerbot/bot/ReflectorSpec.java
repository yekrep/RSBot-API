package org.powerbot.bot;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ReflectorSpec {
	public final Map<String, Reflector.FieldConfig> configs;

	private ReflectorSpec() {
		configs = new HashMap<String, Reflector.FieldConfig>();
	}

	public static ReflectorSpec parse(final InputStream in) {
		final ReflectorSpec r = new ReflectorSpec();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(in));
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
					final long m = args.length > 1 ? Long.parseLong(args[2]) : 1L;
					r.configs.put(name, new Reflector.FieldConfig(name, args[0], args[1], m));
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
