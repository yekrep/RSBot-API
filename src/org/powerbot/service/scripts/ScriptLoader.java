package org.powerbot.service.scripts;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.powerbot.util.io.IOHelper;

/**
 * @author Paris
 */
public final class ScriptLoader {

	public static Object getInstance(final URL... urls) throws Exception {
		final Class<?> cl = URLClassLoader.class;
		return cl.getDeclaredConstructor(URL[].class).newInstance(new Object[] { urls });
	}

	public static Object getInstance(final ZipInputStream in) throws Exception {
		final Class<?> scl = ScriptClassLoader.class;
		final Object cl = scl.newInstance();
		final Method m = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
		m.setAccessible(true);

		ZipEntry entry;
		while ((entry = in.getNextEntry()) != null) {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final byte[] data = new byte[IOHelper.BUFFER_SIZE];
			int len;
			while ((len = in.read(data)) != -1) {
				out.write(data, 0, len);
			}
			final byte[] buf = out.toByteArray();
			final String name = entry.getName();
			m.invoke(cl, name.substring(0, name.indexOf('.')).replace('/', '.'), buf, 0, buf.length);
			in.closeEntry();
		}
		in.close();

		return cl;
	}
}
