package org.powerbot.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.Adler32;

import javax.crypto.Cipher;

import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.CipherStreams;
import org.powerbot.util.io.IniParser;

/**
 * @author Paris
 */
public final class GameAccounts extends ArrayList<GameAccounts.Account> {
	private static final long serialVersionUID = 1L;
	private static final GameAccounts instance = new GameAccounts();
	private final String name;
	private final File store;
	private final byte[] key;
	private final static String CIPHER_ALGORITHM = "RC4", KEY_ALGORITHM = "RC4";

	private GameAccounts() {
		super();

		final Adler32 c = new Adler32();
		c.update(new byte[] {0x21, 0x70, 0x1, 0xf, 0x6e});
		final long uid = Configuration.getUID();
		c.update((int) (uid & 0xffffffff));
		c.update((int) (uid >> 32));
		final long l = c.getValue();
		name = String.format("%s.tmp", Long.toHexString(l));
		store = new File(System.getProperty("java.io.tmpdir"), name);
		key = CipherStreams.getSharedKey(StringUtil.getBytesUtf8(name));

		load();
	}

	public static GameAccounts getInstance() {
		return instance;
	}

	private synchronized void load() {
		InputStream fis = null;
		Map<String, Map<String, String>> data = null;
		if (store.isFile()) {
			try {
				fis = new FileInputStream(store);
				final InputStream is = CipherStreams.getCipherInputStream(fis, Cipher.ENCRYPT_MODE, key, CIPHER_ALGORITHM, KEY_ALGORITHM);
				clear();
				data = IniParser.deserialise(is);
			} catch (final Exception ignored) {
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (final IOException ignored2) {
					}
				}
			}
		}
		if (data == null) {
			return;
		}
		for (final Entry<String, Map<String, String>> e : data.entrySet()) {
			final Account a = new Account(e.getKey());
			for (final Entry<String, String> p : e.getValue().entrySet()) {
				final String k = p.getKey(), v = p.getValue();
				if (k.equalsIgnoreCase("password")) {
					a.password = v;
				} else if (k.equalsIgnoreCase("pin")) {
					a.pin = Integer.parseInt(v);
				} else if (k.equalsIgnoreCase("member")) {
					a.member = Integer.parseInt(v) == 1;
				} else if (k.equalsIgnoreCase("reward")) {
					a.reward = v;
				}
			}
			add(a);
		}
	}

	public synchronized void save() {
		final Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
		for (Iterator<Account> i = iterator(); i.hasNext();) {
			final Account a = i.next();
			final Map<String, String> e = new HashMap<String, String>();
			e.put("password", a.password);
			e.put("pin", Integer.toString(a.pin));
			e.put("member", a.member ? "1" : "0");
			e.put("reward", a.reward);
			data.put(a.toString(), e);
		}
		OutputStream fos = null;
		try {
			fos = new FileOutputStream(store);
			final OutputStream os = CipherStreams.getCipherOutputStream(fos, Cipher.DECRYPT_MODE, key, CIPHER_ALGORITHM, KEY_ALGORITHM);
			IniParser.serialise(data, os);
		} catch (final Exception ignored) {
			ignored.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (final IOException ignored2) {
				}
			}
		}
	}

	public Account get(String username) {
		username = normaliseUsername(username);
		for (Iterator<Account> i = iterator(); i.hasNext(); ) {
			final Account a = i.next();
			if (a.username.equalsIgnoreCase(username)) {
				return a;
			}
		}
		return null;
	}

	public static String normaliseUsername(String username) {
		username = username.toLowerCase().trim().replaceAll("\\s", "_");
		return username;
	}

	public Account add(final String username) {
		final Account account = new Account(username);
		add(account);
		return account;
	}

	public final class Account {
		private String username;
		private String password;
		public int pin = -1;
		public boolean member = false;
		public String reward;

		public Account(final String username) {
			this.username = normaliseUsername(username);
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(final String password) {
			this.password = password;
		}

		public String getPIN() {
			return String.format("%04d", pin);
		}

		@Override
		public String toString() {
			return username;
		}
	}
}
