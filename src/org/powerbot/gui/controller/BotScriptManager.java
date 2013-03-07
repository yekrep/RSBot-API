package org.powerbot.gui.controller;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.zip.Inflater;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import org.powerbot.bot.Bot;
import org.powerbot.core.script.Script;
import org.powerbot.game.api.Manifest;
import org.powerbot.gui.BotChrome;
import org.powerbot.ipc.Controller;
import org.powerbot.ipc.ScheduledChecks;
import org.powerbot.service.GameAccounts;
import org.powerbot.service.GameAccounts.Account;
import org.powerbot.service.NetworkAccount;
import org.powerbot.service.scripts.LocalScriptClassLoader;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.service.scripts.ScriptLoader;
import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IniParser;
import org.powerbot.xboot.ZipInputStream;

public class BotScriptManager {
	private static final Logger log = Logger.getLogger(BotScriptManager.class.getName());

	public static List<ScriptDefinition> loadScripts() throws IOException {
		final List<ScriptDefinition> list = new ArrayList<ScriptDefinition>();

		final List<File> paths = new ArrayList<File>(2);
		paths.add(new File("bin"));
		paths.add(new File("out"));
		for (final File path : paths) {
			if (path.isDirectory()) {
				loadLocalScripts(list, path, null);
			}
		}

		final Map<String, Map<String, String>> manifests = IniParser.deserialise(HttpClient.openStream(Configuration.URLs.SCRIPTSCOLLECTION, NetworkAccount.getInstance().getAuth()));
		final boolean vip = NetworkAccount.getInstance().hasPermission(NetworkAccount.VIP);
		for (final Entry<String, Map<String, String>> entry : manifests.entrySet()) {
			final Map<String, String> params = entry.getValue();
			if (params.containsKey("vip") && IniParser.parseBool(params.get("vip")) && !vip) {
				continue;
			}
			final ScriptDefinition def = ScriptDefinition.fromMap(params);
			if (def != null) {
				def.source = entry.getKey();
				if (entry.getValue().containsKey("className") && entry.getValue().containsKey("key")) {
					def.className = entry.getValue().get("className");
					final byte[] key = StringUtil.hexStringToByteArray(entry.getValue().get("key")), kx = new byte[key.length * 2];
					new Random().nextBytes(kx);
					for (int i = 0; i < key.length; i++) {
						kx[i * 2] = key[i];
					}
					def.key = kx;
					list.add(def);
				}
			}
		}
		return list;
	}

	public static void loadLocalScripts(final List<ScriptDefinition> list, final File parent, final File dir) {
		if (!NetworkAccount.getInstance().hasPermission(NetworkAccount.LOCALSCRIPTS)) {
			return;
		}
		for (final File file : (dir == null ? parent : dir).listFiles()) {
			if (file.isDirectory()) {
				loadLocalScripts(list, parent, file);
			} else if (file.isFile()) {
				final String name = file.getName();
				try {
					if (name.endsWith(".class") && name.indexOf('$') == -1) {
						if (name.length() < 9) {
							continue;
						}
						final FileInputStream fis = new FileInputStream(file);
						final byte[] data = new byte[512];
						fis.read(data);
						fis.close();
						if (data == null || data.length < 12) {
							continue;
						}
						if (data[7] != 0x33) {
							continue;
						}
						boolean skip = false;
						for (int i = 11; i < data.length - 2; ) {
							if (data[i++] == 0x5a && data[i++] == 0x4b && data[i++] == 0x4d) {
								skip = true;
								break;
							}
						}
						if (skip) {
							continue;
						}
						final URL src = parent.getCanonicalFile().toURI().toURL();
						@SuppressWarnings("resource")
						final ClassLoader cl = new URLClassLoader(new URL[]{src});
						String className = file.getCanonicalPath().substring(parent.getCanonicalPath().length() + 1);
						className = className.substring(0, className.lastIndexOf('.'));
						className = className.replace(File.separatorChar, '.');
						final Class<?> clazz;
						try {
							clazz = cl.loadClass(className);
						} catch (final NoClassDefFoundError ignored) {
							continue;
						}
						if (Script.class.isAssignableFrom(clazz)) {
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
					}
				} catch (final Exception ignored) {
				}
			}
		}
	}

	public static void loadScript(final ScriptDefinition def, final String accountName, final Component parent) {
		if (!NetworkAccount.getInstance().isLoggedIn()) {
			return;
		}
		final Collection<String> runningList = Controller.getInstance().getRunningScripts();
		int n = 0;
		for (final String running : runningList) {
			if (def.getID().equals(running)) {
				n++;
			}
		}
		final ClassLoader cl;
		if (def.local) {
			try {
				cl = new LocalScriptClassLoader(new File(def.source).toURI().toURL());
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
				final InputStream in = HttpClient.openStream(Configuration.URLs.SCRIPTSDOWNLOAD, NetworkAccount.getInstance().getAuth(), def.source.toString());
				cl = (ClassLoader) ScriptLoader.getInstance(new ZipInputStream(new CipherInputStream(in, c)));
			} catch (final Exception ignored) {
				log.severe("Could not download script");
				ignored.printStackTrace();
				return;
			}
			final Map<String, Map<String, String>> data;
			try {
				data = IniParser.deserialise(HttpClient.openStream(Configuration.URLs.SCRIPTSAUTH, NetworkAccount.getInstance().getAuth(), def.getID(), Integer.toString(n)));
			} catch (final IOException ignored) {
				log.severe("Unable to obtain auth response");
				return;
			} catch (final NullPointerException ignored) {
				log.severe("Could not identify auth server");
				return;
			}
			if (data == null || !data.containsKey("auth")) {
				log.severe("Error reading auth response");
				return;
			}
			if (!data.get("auth").containsKey("access") || !IniParser.parseBool(data.get("auth").get("access"))) {
				if (data.get("auth").containsKey("message")) {
					final String s = data.get("auth").get("message");
					if (parent == null) {
						log.info(s);
					} else {
						JOptionPane.showMessageDialog(parent, s);
					}
				}
				log.severe("You are not authorised to run this script");
				return;
			}
		}
		final Script script;
		try {
			script = cl.loadClass(def.className).asSubclass(Script.class).newInstance();
		} catch (final Exception ignored) {
			log.severe("Error loading script");
			if (Configuration.SUPERDEV) {
				ignored.printStackTrace();
			}
			return;
		}
		final Manifest manifest = script.getClass().getAnnotation(Manifest.class);
		if (manifest != null && manifest.singleinstance() && n > 0) {
			final String s = "This script can only be used on one account at a time.";
			if (parent == null) {
				log.info(s);
			} else {
				JOptionPane.showMessageDialog(parent, s);
			}
			return;
		}
		final Bot bot = Bot.instance();
		bot.setAccount(null);
		for (final Account a : GameAccounts.getInstance()) {
			if (accountName.equalsIgnoreCase(a.toString())) {
				bot.setAccount(a);
				break;
			}
		}
		runningList.removeAll(Controller.getInstance().getRunningScripts());
		if (!runningList.isEmpty()) {
			log.severe("You changed a script on another bot, please try again");
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					final long mins = (30 + new Random().nextInt(180)) * (NetworkAccount.getInstance().hasPermission(NetworkAccount.DEVELOPER) ? 12 : 1);
					ScheduledChecks.timeout.set(System.nanoTime() + TimeUnit.MINUTES.toNanos(mins));
					bot.startScript(script, def);
				}
			}).start();
		}
	}
}
