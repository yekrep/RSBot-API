package org.powerbot.service.scripts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.Inflater;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.powerbot.Configuration;
import org.powerbot.bot.Bot;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.Manifest;
import org.powerbot.script.Script;
import org.powerbot.script.internal.InternalScript;
import org.powerbot.service.GameAccounts;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.Ini;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.CryptFile;

/**
 * @author Paris
 */
public class ScriptList {
	private final static Logger log = Logger.getLogger(ScriptList.class.getName());

	public static List<ScriptDefinition> getList() throws IOException {
		final List<ScriptDefinition> list = new ArrayList<ScriptDefinition>();

		if (NetworkAccount.getInstance().hasPermission(NetworkAccount.LOCALSCRIPTS)) {
			for (final String s : System.getProperty("java.class.path").split(Pattern.quote(File.pathSeparator))) {
				final File f = new File(s);
				if (f.isDirectory()) {
					getLocalList(list, f, null);
				}
			}
		}

		if (!Configuration.BETA) {
			getNetworkList(list);
		}

		return list;
	}

	private static void getNetworkList(final List<ScriptDefinition> list) throws IOException {
		final Ini t = new Ini();
		InputStream is = null;
		try {
			is = NetworkAccount.getInstance().getScriptsList();
			t.read(is);
		} catch (final IOException e) {
			throw e;
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

			final ScriptDefinition def = ScriptDefinition.fromMap(params.getMap());
			if (def != null && params.has("link") && params.has("className") && params.has("key")) {
				def.source = params.get("link");
				def.className = params.get("className");

				final byte[] key = StringUtil.hexStringToByteArray(params.get("key")), kx = new byte[key.length * 2];
				new Random().nextBytes(kx);
				for (int i = 0; i < key.length; i++) {
					kx[i * 2] = key[i];
				}
				def.key = kx;

				list.add(def);
			}
		}
	}

	public static void getLocalList(final List<ScriptDefinition> list, final File parent, final File dir) {
		if (!NetworkAccount.getInstance().hasPermission(NetworkAccount.LOCALSCRIPTS)) {
			return;
		}

		for (final File file : (dir == null ? parent : dir).listFiles()) {
			if (file.isDirectory()) {
				getLocalList(list, parent, file);
			} else if (file.isFile()) {
				final String name = file.getName();
				if (name.endsWith(".class") && name.indexOf('$') == -1) {
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
						if (Script.class.isAssignableFrom(clazz) && !InternalScript.class.isAssignableFrom(clazz)) {
							final Class<? extends Script> script = clazz.asSubclass(Script.class);
							if (script.isAnnotationPresent(Manifest.class)) {
								final Manifest m = script.getAnnotation(Manifest.class);
								final ScriptDefinition def = new ScriptDefinition(m);
								def.source = parent.getCanonicalFile().toString();
								def.className = className;
								def.local = true;
								list.add(def);
							}
						}
					} catch (final IOException ignored) {
					}
				}
			}
		}

	}

	public static void load(final BotChrome chrome, final ScriptDefinition def, final String username) {
		if (!NetworkAccount.getInstance().isLoggedIn()) {
			return;
		}

		CryptFile cache = null;
		final ClassLoader cl;
		if (def.local) {
			try {
				cl = new ScriptClassLoader(new File(def.source).toURI().toURL());
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
				cache = new CryptFile("script.1-" + def.getID().replace('/', '-'), ScriptList.class, ScriptClassLoader.class);
				final InputStream in = cache.download(new URL(def.source));
				cl = new ScriptClassLoader(new ZipInputStream(new CipherInputStream(in, c)));
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
			if (!Configuration.FROMJAR) {
				ignored.printStackTrace();
			}
			return;
		}

		final Bot bot = chrome.getBot();
		if (username != null) {
			bot.setAccount(GameAccounts.getInstance().get(username));
		}

		log.info("Starting script: " + def.getName());
		int hours = 0;
		String msg = null;

		if (def.local) {
			final boolean dev = NetworkAccount.getInstance().hasPermission(NetworkAccount.DEVELOPER);
			hours = dev ? 3 : 1;
			if (!dev) {
				msg = "Apply for a developer account for extended time.";
			}
		} else if (!def.assigned && !NetworkAccount.getInstance().hasPermission(NetworkAccount.VIP)) {
			hours = 2;
			msg = "VIP subscribers and Premium scripts have no time limits.";
		}

		if (hours != 0) {
			msg = "The script will automatically stop after " + hours + " hour" + (hours == 1 ? "" : "s") + "." +
					(msg == null || msg.isEmpty() ? "" : "\n" + msg);
			log.warning(msg.replace('\n', ' '));

			if (!def.local) {
				final AtomicInteger res = new AtomicInteger(-1);
				final String txt = msg;
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							res.set(JOptionPane.showConfirmDialog(chrome, txt, "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE));
						}
					});
				} catch (final InterruptedException ignored) {
				} catch (final InvocationTargetException ignored) {
				}
				if (res.get() != JOptionPane.OK_OPTION) {
					return;
				}
			}
		}

		bot.startScript(new ScriptBundle(def, script), hours == 0 ? 0 : (int) TimeUnit.HOURS.toMillis(hours));
	}
}
