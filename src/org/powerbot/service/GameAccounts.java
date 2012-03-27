package org.powerbot.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.powerbot.util.io.IniParser;
import org.powerbot.util.io.SecureStore;

/**
 * @author Paris
 */
public final class GameAccounts extends ArrayList<GameAccounts.Account> {
	private static final long serialVersionUID = 1L;
	private static final GameAccounts instance = new GameAccounts();
	private static final String FILENAME = "accounts.ini";

	private GameAccounts() {
		super();
	}

	public static GameAccounts getInstance() {
		return instance;
	}

	public synchronized void load() throws IOException, GeneralSecurityException {
		clear();
		final InputStream is = SecureStore.getInstance().read(FILENAME);
		if (is != null) {
			final Map<String, Map<String, String>> entries = IniParser.deserialise(is);
			for (final Entry<String, Map<String, String>> e : entries.entrySet()) {
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
	}

	public synchronized void save() throws IOException, GeneralSecurityException {
		final Map<String, Map<String, String>> entries = new HashMap<String, Map<String, String>>();
		for (Iterator<Account> i = iterator(); i.hasNext(); ) {
			final Account a = i.next();
			final Map<String, String> e = new HashMap<String, String>();
			e.put("password", a.password);
			e.put("pin", Integer.toString(a.pin));
			e.put("member", a.member ? "1" : "0");
			e.put("reward", a.reward);
			entries.put(a.toString(), e);
		}
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IniParser.serialise(entries, bos);
		bos.close();
		SecureStore.getInstance().write(FILENAME, new ByteArrayInputStream(bos.toByteArray()));
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
