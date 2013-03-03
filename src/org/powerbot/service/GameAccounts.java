package org.powerbot.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.powerbot.util.io.CryptFile;
import org.powerbot.util.io.IniParser;

/**
 * @author Paris
 */
public final class GameAccounts extends ArrayList<GameAccounts.Account> {
	private static final long serialVersionUID = 1L;
	private static final GameAccounts instance = new GameAccounts();
	private final CryptFile store;

	private GameAccounts() {
		super();
		store = new CryptFile("gameaccts", GameAccounts.class);
		load();
	}

	public static GameAccounts getInstance() {
		return instance;
	}

	private synchronized void load() {
		InputStream is;
		try {
			is = store.getInputStream();
		} catch (IOException ignored) {
			return;
		}
		final Map<String, Map<String, String>> data;
		try {
			data = IniParser.deserialise(is);
		} catch (IOException ignored) {
			return;
		} finally {
			try {
				is.close();
			} catch (final IOException ignored) {
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
				}
			}
			add(a);
		}
	}

	public synchronized void save() {
		final Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
		for (Iterator<Account> i = iterator(); i.hasNext(); ) {
			final Account a = i.next();
			final Map<String, String> e = new HashMap<String, String>();
			e.put("password", a.password);
			e.put("pin", Integer.toString(a.pin));
			e.put("member", a.member ? "1" : "0");
			data.put(a.toString(), e);
		}
		OutputStream os = null;
		try {
			os = store.getOutputStream();
			IniParser.serialise(data, os);
		} catch (final Exception ignored) {
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (final IOException ignored) {
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
