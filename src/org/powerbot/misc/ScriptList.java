package org.powerbot.misc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.powerbot.bot.ScriptClassLoader;
import org.powerbot.bot.ScriptController;
import org.powerbot.bot.rt6.Login;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.AbstractScript;
import org.powerbot.script.ClientContext;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.util.Ini;
import org.powerbot.util.StringUtils;
import org.powerbot.util.TarReader;

public class ScriptList {
	private final static Logger log = Logger.getLogger("Script");
	private final static String[] classpath;

	static {
		final String p = System.getProperty("java.class.path", "") + File.pathSeparator + System.getProperty("java.search.path");
		classpath = p.split(Pattern.quote(File.pathSeparator));
	}

	public static List<ScriptBundle.Definition> getList() throws IOException {
		final List<ScriptBundle.Definition> list = new ArrayList<ScriptBundle.Definition>();

		if (NetworkAccount.getInstance().hasPermission(NetworkAccount.LOCALSCRIPTS)) {
			for (final String s : classpath) {
				final File f;
				if (!s.isEmpty() && (f = new File(s)).isDirectory()) {
					getLocalList(list, f, null);
				}
			}
		}

		getNetworkList(list);

		return list;
	}

	private static void getNetworkList(final List<ScriptBundle.Definition> list) throws IOException {
		final Ini t = new Ini();
		InputStream is = null;
		try {
			is = NetworkAccount.getInstance().getScriptsList();
			t.read(is);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (final IOException ignored) {
				}
			}
		}

		for (final Map.Entry<String, Ini.Member> entry : t.entrySet()) {
			final Ini.Member params = entry.getValue();

			final ScriptBundle.Definition def = ScriptBundle.Definition.fromMap(params.getMap());
			if (def != null && params.has("link") && params.has("className") && params.has("key")) {
				def.source = params.get("link");
				def.className = params.get("className");

				final byte[] key = StringUtils.hexStringToByteArray(params.get("key")), kx = new byte[key.length * 2];
				new SecureRandom().nextBytes(kx);
				for (int i = 0; i < key.length; i++) {
					kx[i * 2] = key[i];
				}
				def.key = kx;

				list.add(def);
			}
		}
	}

	private static void getLocalList(final List<ScriptBundle.Definition> list, final File parent, final File dir) {
		if (!NetworkAccount.getInstance().hasPermission(NetworkAccount.LOCALSCRIPTS)) {
			return;
		}
		final File[] files = (dir == null ? parent : dir).listFiles();
		if (files == null || files.length == 0) {
			return;
		}

		for (final File file : files) {
			if (file.isDirectory()) {
				getLocalList(list, parent, file);
				continue;
			}

			final String name = file.getName();
			if (!name.endsWith(".class") || name.indexOf('$') != -1) {
				continue;
			}

			try {
				final URL src = parent.getCanonicalFile().toURI().toURL();
				final ClassLoader cl = new URLClassLoader(new URL[]{src});
				String className = file.getCanonicalPath().substring(parent.getCanonicalPath().length() + 1);
				className = className.substring(0, className.lastIndexOf('.'));
				className = className.replace(File.separatorChar, '.');
				final Class<?> clazz;
				try {
					clazz = cl.loadClass(className);
				} catch (final Throwable ignored) {
					continue;
				}

				final Class<? extends AbstractScript> script;
				if (AbstractScript.class.isAssignableFrom(clazz)
						&& (script = clazz.asSubclass(AbstractScript.class)).isAnnotationPresent(Script.Manifest.class)) {
					final Script.Manifest m = script.getAnnotation(Script.Manifest.class);
					final ScriptBundle.Definition def = new ScriptBundle.Definition(m);
					def.source = parent.getCanonicalFile().toString();
					def.className = className;
					def.local = true;
					def.client = getScriptTypeArg(script);

					final Map<String, String> t = ScriptBundle.parseProperties(m.properties());
					if (t.containsKey("client")) {
						final String c = t.get("client");
						if (c.equals("6")) {
							def.client = org.powerbot.script.rt6.ClientContext.class;
						} else if (c.equals("4")) {
							def.client = org.powerbot.script.rt4.ClientContext.class;
						}
					}

					list.add(def);
				}
			} catch (final IOException ignored) {
			}
		}
	}

	public static Type getScriptTypeArg(Class<? extends AbstractScript> c) {
		final List<Class<? extends AbstractScript>> cls = new LinkedList<Class<? extends AbstractScript>>();

		while (!c.equals(AbstractScript.class) && !c.equals(PollingScript.class)) {
			cls.add(c);
			c = c.getSuperclass().asSubclass(AbstractScript.class);
		}

		int i = 0;
		final ListIterator<Class<? extends AbstractScript>> li = cls.listIterator(cls.size());
		while (li.hasPrevious()) {
			c = li.previous();
			final Type p = c.getGenericSuperclass();
			if (!(p instanceof ParameterizedType)) {
				break;
			}
			final Type[] t = ((ParameterizedType) p).getActualTypeArguments();
			if (t == null || t.length < i) {
				break;
			}
			if (t[i] instanceof TypeVariable<?>) {
				final TypeVariable<?>[] e = c.getTypeParameters();
				final String s = ((TypeVariable<?>) t[i]).getName();
				for (int j = 0; j < e.length; j++) {
					if (e[j].getName().equals(s)) {
						i = j;
						break;
					}
				}
			} else {
				return t[i];
			}
		}

		return null;
	}

	public static Class<?> getPrimaryClientContext(Class<?> c) {
		while (c != org.powerbot.script.rt6.ClientContext.class && c != org.powerbot.script.rt4.ClientContext.class && c != Object.class) {
			c = c.getSuperclass();
		}

		return c;
	}

	public static void load(final BotChrome chrome, final ScriptBundle.Definition def, final String username) {
		if (!NetworkAccount.getInstance().isLoggedIn()) {
			return;
		}

		final ClientContext ctx = chrome.bot.get().ctx;

		CryptFile cache = null;
		final ClassLoader cl;
		if (def.local) {
			try {
				cl = new ScriptClassLoader(ctx, new File(def.source).toURI().toURL());
			} catch (final Exception ignored) {
				return;
			}
		} else {
			try {
				final byte[] buf = new byte[def.key.length / 2], key = new byte[16];
				for (int i = 0; i < buf.length; i++) {
					buf[i] = def.key[i * 2];
				}
				final Inflater inf = new Inflater();
				inf.setInput(buf);
				inf.inflate(key, 0, key.length);
				inf.end();
				final Cipher c = Cipher.getInstance("RC4");
				c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, 0, key.length, "ARCFOUR"));
				cache = new CryptFile("script.1-" + def.getID().replace('/', '-'));
				final InputStream in = cache.download(new URL(def.source));
				cl = new ScriptClassLoader(ctx, new TarReader(new GZIPInputStream(new CipherInputStream(in, c))));
			} catch (final Exception ignored) {
				log.severe("Could not download script");
				ignored.printStackTrace();
				return;
			}
		}

		final Class<? extends Script> script;
		try {
			script = cl.loadClass(def.className).asSubclass(Script.class);
		} catch (final Exception ignored) {
			if (cache != null) {
				cache.delete();
			}
			log.severe("Error loading script");
			return;
		}

		if (username != null) {
			ctx.properties.put(Login.LOGIN_USER_PROPERTY, username);
		}

		log.info("Starting " + def.getName());
		int hours = 0;
		String msg = null;
		final int m = 2;

		if (def.local) {
			final boolean dev = NetworkAccount.getInstance().hasPermission(NetworkAccount.DEVELOPER);
			hours = (dev ? 3 : 1) * m;
			if (!dev) {
				msg = "Apply for a developer account for extended time.";
			}
		} else if (!def.assigned && !NetworkAccount.getInstance().hasPermission(NetworkAccount.VIP)) {
			hours = 2 * m;
			msg = "VIP subscribers and Premium scripts have no time limits.";
		}

		if (hours != 0) {
			msg = "The script will automatically stop after " + hours + " hour" + (hours == 1 ? "" : "s") + "." +
					(msg == null || msg.isEmpty() ? "" : "\n" + msg);
			log.warning(msg.replace('\n', ' '));

			if (!def.local) {
				final AtomicInteger res = new AtomicInteger(-1);
				final String txt = msg;
				final Runnable r = new Runnable() {
					@Override
					public void run() {
						res.set(JOptionPane.showConfirmDialog(chrome.window.get(), txt, "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE));
					}
				};


				if (SwingUtilities.isEventDispatchThread()) {
					r.run();
				} else {
					try {
						SwingUtilities.invokeAndWait(r);
					} catch (final InterruptedException ignored) {
					} catch (final InvocationTargetException ignored) {
					}
				}

				if (res.get() != JOptionPane.OK_OPTION) {
					return;
				}
			}

			ctx.properties.put(ScriptController.TIMEOUT_PROPERTY, Long.toString(TimeUnit.HOURS.toMillis(hours)));
		}

		final NetworkAccount n = NetworkAccount.getInstance();
		final boolean l = n.isLoggedIn();
		ctx.properties.put("user.id", l ? Integer.toString(n.getUID()) : "0");
		ctx.properties.put("user.name", l ? n.getDisplayName() : "");
		ctx.properties.put("user.vip", Boolean.toString(l && n.hasPermission(NetworkAccount.VIP)));

		ctx.properties.put(ScriptController.LOCAL_PROPERTY, Boolean.toString(def.local));
		final ScriptController<? extends ClientContext> c = (ScriptController<? extends ClientContext>) ctx.controller;
		c.bundle.set(new ScriptBundle(def, script));
		c.run();
	}
}
