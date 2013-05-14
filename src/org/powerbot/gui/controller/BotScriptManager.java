package org.powerbot.gui.controller;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.zip.Inflater;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import org.powerbot.bot.Bot;
import org.powerbot.ipc.Controller;
import org.powerbot.ipc.ScheduledChecks;
import org.powerbot.script.AbstractScript;
import org.powerbot.script.Manifest;
import org.powerbot.script.Script;
import org.powerbot.script.internal.LocalScriptClassLoader;
import org.powerbot.script.internal.ScriptDefinition;
import org.powerbot.script.internal.ScriptLoader;
import org.powerbot.script.internal.randoms.RandomEvent;
import org.powerbot.service.GameAccounts;
import org.powerbot.service.GameAccounts.Account;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IniParser;

public class BotScriptManager {
	private static final Logger log = Logger.getLogger(BotScriptManager.class.getName());

	public static List<ScriptDefinition> loadScripts() throws IOException {
		final List<ScriptDefinition> list = new ArrayList<>();

		final List<File> paths = new ArrayList<>(2);
		paths.add(new File("bin"));
		paths.add(new File("out"));
		for (final File path : paths) {
			if (path.isDirectory()) {
				loadLocalScripts(list, path, null);
			}
		}

		loadNetworkScripts(list);

		return list;
	}

	private static void loadNetworkScripts(final Collection<ScriptDefinition> list) {
		final Map<String, Map<String, String>> manifests;
		try {
			manifests = IniParser.deserialise(HttpClient.openStream(Configuration.URLs.SCRIPTSCOLLECTION, NetworkAccount.getInstance().getAuth()));
		} catch (final IOException ignored) {
			return;
		}

		final boolean vip = NetworkAccount.getInstance().hasPermission(NetworkAccount.VIP);

		for (final Entry<String, Map<String, String>> entry : manifests.entrySet()) {
			final Map<String, String> params = entry.getValue();
			if (params.containsKey("vip") && IniParser.parseBool(params.get("vip")) && !vip) {
				continue;
			}
			final ScriptDefinition def = new ScriptDefinition(params);
			if (def == null) {
				continue;
			}
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

	private static void loadLocalScripts(final Collection<ScriptDefinition> list, final File parent, final File dir) {
		if (!NetworkAccount.getInstance().hasPermission(NetworkAccount.LOCALSCRIPTS)) {
			return;
		}

		for (final File file : (dir == null ? parent : dir).listFiles()) {
			final String name = file.getName();
			if (file.isDirectory()) {
				loadLocalScripts(list, parent, file);
			} else if (file.isFile() && name.endsWith(".class") && name.indexOf('$') == -1 && !isZKMClassFile(file)) {
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
					} catch (final NoClassDefFoundError ignored) {
						continue;
					}
					if (AbstractScript.class.isAssignableFrom(clazz)) {
						final Class<? extends AbstractScript> script = clazz.asSubclass(AbstractScript.class);
						if (script.isAnnotationPresent(Manifest.class) && !Arrays.asList(script.getInterfaces()).contains(RandomEvent.class)) {
							final ScriptDefinition def = new ScriptDefinition(null, script.getAnnotation(Manifest.class));
							def.source = parent.getCanonicalFile().toString();
							def.className = className;
							def.local = true;
							list.add(def);
						}
					}
				} catch (final Exception ignored) {
				}
			}
		}
	}

	private static boolean isZKMClassFile(final File file) {
		if (file.getName().length() < 9) {
			return true;
		}
		try (final FileInputStream fis = new FileInputStream(file)) {
			final byte[] data = new byte[512];
			fis.read(data);
			fis.close();
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
		} catch (final IOException ignored) {
		}
		return false;
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
		final AbstractScript script;
		try {
			script = cl.loadClass(def.className).asSubclass(AbstractScript.class).newInstance();
		} catch (final Exception ignored) {
			log.severe("Error loading script");
			if (Configuration.SUPERDEV) {
				ignored.printStackTrace();
			}
			return;
		}
		def.setScript(script);
		if (n > def.getInstances()) {
			final String s = "This script can only be used on " + def.getInstances() + " account" + (def.getInstances() == 1 ? "" : "s") + " at a time.";
			if (parent == null) {
				log.info(s);
			} else {
				JOptionPane.showMessageDialog(parent, s);
			}
			return;
		}
		final Bot bot = Bot.getInstance();
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
					final long mins = NetworkAccount.getInstance().hasPermission(NetworkAccount.DEVELOPER) ? 60 : 15;
					ScheduledChecks.timeout.set(System.nanoTime() + TimeUnit.MINUTES.toNanos(mins));
					bot.startScript(def);
				}
			}).start();
		}
	}
}
