package org.powerbot.service.scripts;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.powerbot.util.io.IOHelper;

/**
 * @author Paris
 */
public final class ScriptLoader {

	public static Object getInstance(final ZipInputStream in) throws Exception {
		final Class<?> scl = ScriptClassLoader.class;
		final Object cl = scl.newInstance();
		final Method m = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
		m.setAccessible(true);
		final Map<String, byte[]> map = new HashMap<String, byte[]>();

		ZipEntry entry;
		while ((entry = in.getNextEntry()) != null) {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final byte[] data = new byte[IOHelper.BUFFER_SIZE];
			int len;
			while ((len = in.read(data)) != -1) {
				out.write(data, 0, len);
			}
			final String name = entry.getName();
			map.put(name.substring(0, name.indexOf('.')).replace('/', '.'), out.toByteArray());
			in.closeEntry();
		}
		in.close();

		final Stack<String> list = new Stack<String>();
		final List<String> loaded = new ArrayList<String>(map.size());
		list.addAll(map.keySet());

		while (!list.isEmpty()) {
			final String name = list.pop();

			if (loaded.contains(name)) {
				continue;
			}

			final byte[] buf = map.get(name);

			try {
				m.invoke(cl, name, buf, 0, buf.length);
				loaded.add(name);
			} catch (final Exception e) {
				if (e instanceof InvocationTargetException) {
					final Throwable t = ((InvocationTargetException) e).getCause();
					if (t instanceof NoClassDefFoundError) {
						list.push(name);
						list.push(t.getMessage());
						continue;
					}
				}
				throw e;
			}
		}

		map.clear();
		return cl;
	}
}
