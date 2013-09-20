package org.powerbot.service.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.Inflater;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

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
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IPCLock;

/**
 * @author Paris
 */
public class ScriptList {
	private final static Logger log = Logger.getLogger(ScriptList.class.getName());

	public static List<ScriptDefinition> getList() throws IOException {
		final List<ScriptDefinition> list = new ArrayList<>();

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
		try (final InputStream is = NetworkAccount.getInstance().getScriptsList()) {
			t.read(is);
		} catch (final IOException e) {
			throw(e);
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

				if (params.has("session")) {
					def.session = params.getInt("session");
				}

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
				if (name.endsWith(".class") && name.indexOf('$') == -1 && !isZKMClassFile(file)) {
					try {
						final URL src = parent.getCanonicalFile().toURI().toURL();
						@SuppressWarnings("resource")
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

	public static void load(final ScriptDefinition def, final String username) {
		if (!NetworkAccount.getInstance().isLoggedIn()) {
			return;
		}
		FileLock lock = null;
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
			final Ini t = new Ini();
			lock = IPCLock.getInstance().getLock(30);
			try (final InputStream is = HttpClient.openStream(Configuration.URLs.SCRIPTSAUTH, NetworkAccount.getInstance().getAuth(), def.getID())) {
				t.read(is);
			} catch (final IOException ignored) {
				log.severe("Unable to obtain auth response");
				return;
			} catch (final NullPointerException ignored) {
				log.severe("Could not identify auth server");
				return;
			}
			if (!t.get("auth").getBool("access")) {
				String m = t.get("auth").get("message");
				if (m != null && !m.isEmpty()) {
					m = ": " + m;
				}
				log.severe("You are not authorised to run this script" + m);
				return;
			}
		}
		final Script script;
		try {
			script = cl.loadClass(def.className).asSubclass(Script.class).newInstance();
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
		final Bot bot = BotChrome.getInstance().getBot();
		if (username != null) {
			bot.setAccount(GameAccounts.getInstance().get(username));
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				log.info("Starting script: " + def.getName());
				final int hours = 1 * (NetworkAccount.getInstance().hasPermission(NetworkAccount.DEVELOPER) ? 3 : 1);
				bot.startScript(new ScriptBundle(def, script), def.local ? (int) TimeUnit.HOURS.toMillis(hours) : 0);
			}
		}).start();
	}

	private static boolean isZKMClassFile(final File file) {
		if (file.getName().length() < 9) {
			return true;
		}

		final byte[] data = new byte[512];
		try (final FileInputStream fis = new FileInputStream(file)) {
			fis.read(data);
		} catch (final IOException ignored) {
		}

		if (data == null || data.length < 12) {
			return true;
		}
		if (data[7] != 0x33) {
			return true;
		}

		for (int i = 11; i < data.length - 2; ) {
			if (data[i++] == 0x5a && data[i++] == 0x4b && data[i++] == 0x4d) {
				return true;
			}
		}

		return false;
	}
}
