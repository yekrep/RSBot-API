package org.powerbot.misc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.powerbot.util.Ini;

public final class GameAccounts extends ArrayList<GameAccounts.Account> {
	private static final GameAccounts instance = new GameAccounts();
	private static final long serialVersionUID = -8481913088185494034L;
	private final CryptFile store;

	private GameAccounts() {
		super();
		store = new CryptFile("accounts.1.ini", false);
		load();
	}

	public static GameAccounts getInstance() {
		return instance;
	}

	private synchronized void load() {
		if (!store.exists()) {
			return;
		}

		final Ini t = new Ini();

		InputStream is = null;
		try {
			is = store.getInputStream();
			t.read(is);
		} catch (final IOException ignored) {
			return;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (final IOException ignored) {
				}
			}
		}

		for (final Entry<String, Ini.Member> e : t.entrySet()) {
			final String k = e.getKey().trim();
			if (k.isEmpty()) {
				continue;
			}
			final Account a = new Account(e.getKey());
			final Ini.Member v = e.getValue();
			a.password = v.get("password");
			a.pin = v.getInt("pin", -1);
			a.member = v.getBool("member");
			add(a);
		}
	}

	public synchronized void save() {
		final Ini t = new Ini();
		for (final Account a : this) {
			t.get(a.toString()).put("password", a.password).put("pin", a.pin).put("member", a.member);
		}

		OutputStream os = null;
		try {
			os = store.getOutputStream();
			t.write(os);
		} catch (final IOException ignored) {
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (final IOException ignored) {
				}
			}
		}
	}

	public boolean contains(String username) {
		username = normaliseUsername(username);
		for (final Account a : this) {
			if (a.username.equalsIgnoreCase(username)) {
				return true;
			}
		}
		return false;
	}

	public Account get(String username) {
		username = normaliseUsername(username);
		for (final Account a : this) {
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

	public int find(String username) {
		username = normaliseUsername(username);
		int i = 0;

		for (final Account a : this) {
			if (a.username.equals(username)) {
				return i;
			}
			i++;
		}

		return -1;
	}

	public final class Account {
		private final String username;
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
