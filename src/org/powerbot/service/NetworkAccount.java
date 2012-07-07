package org.powerbot.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.powerbot.util.StringUtil;
import org.powerbot.util.io.CryptFile;
import org.powerbot.util.io.IniParser;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public final class NetworkAccount {
	private static NetworkAccount instance = null;
	private final static String STORENAME = "netacct", AUTHKEY = "auth", CREATEDKEY = "created";
	private final static int CACHETTL = 24 * 60 * 60 * 1000;
	private final CryptFile store;
	private Account account;

	private NetworkAccount() {
		store = new CryptFile(STORENAME, NetworkAccount.class);

		if (store.exists()) {
			try {
				final Map<String, String> data = IniParser.deserialise(store.getInputStream()).get(IniParser.EMPTYSECTION);
				if (data.containsKey(CREATEDKEY) && Long.parseLong(data.get(CREATEDKEY)) + CACHETTL > System.currentTimeMillis()) {
					account = Account.fromMap(data);
				} else {
					login("", "", data.get(AUTHKEY));
				}
			} catch (final IOException ignored) {
			}

			if (!isLoggedIn()) {
				store.delete();
			}
		}
	}

	public synchronized static NetworkAccount getInstance() {
		if (instance == null) {
			instance = new NetworkAccount();
		}
		return instance;
	}

	public boolean isLoggedIn() {
		return account != null && account.getID() != 0;
	}

	public boolean isVIP() {
		return account != null && account.isVIP();
	}

	public Account getAccount() {
		return account;
	}

	public synchronized boolean login(final String username, final String password, final String auth) throws IOException {
		InputStream is;
		try {
			is = Resources.openHttpStream("signin", StringUtil.urlEncode(username), StringUtil.urlEncode(password), StringUtil.urlEncode(auth));
		} catch (final NullPointerException ignored) {
			ignored.printStackTrace();
			return false;
		}
		final boolean success = parseResponse(is) && isLoggedIn();
		if (success) {
			final Map<String, String> data = account.getMap();
			data.put(CREATEDKEY, Long.toString(System.currentTimeMillis()));
			final Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
			map.put(IniParser.EMPTYSECTION, data);
			IniParser.serialise(map, store.getOutputStream());
		} else {
			store.delete();
		}
		return success;
	}

	private boolean parseResponse(final InputStream is) throws IOException {
		final Map<String, Map<String, String>> data = IniParser.deserialise(is);
		if (data == null || data.size() == 0 || !data.containsKey(AUTHKEY)) {
			return false;
		}
		final Map<String, String> auth = data.get(AUTHKEY);
		account = Account.fromMap(auth);
		return true;
	}

	public synchronized void logout() {
		account = null;
		store.delete();
	}

	public final static class Account {
		private final int id;
		private final String auth, name, display, email;
		private final int[] groups;

		public Account(final int id, final String auth, final String name, final String display, final String email, final int[] groups) {
			this.id = id;
			this.auth = auth;
			this.name = name;
			this.display = display;
			this.email = email;
			this.groups = groups;
		}

		public int getID() {
			return id;
		}

		public String getAuth() {
			return auth;
		}

		public String getName() {
			return name;
		}

		public String getDisplayName() {
			return display;
		}

		public String getEmail() {
			return email;
		}

		public int[] getGroupIDs() {
			return groups;
		}

		public boolean isVIP() {
			final String groups;
			try {
				groups = Resources.getServerData().get("access").get("vip");
			} catch (final Exception ignored) {
				return false;
			}
			for (final String group : groups.split(",")) {
				final int g;
				try {
					g = Integer.parseInt(group);
				} catch (final NumberFormatException ignored) {
					continue;
				}
				for (final int check : this.groups) {
					if (check == g) {
						return true;
					}
				}
			}
			return false;
		}

		public Map<String, String> getMap() {
			final Map<String, String> data = new HashMap<String, String>();
			data.put("member_id", Integer.toString(id));
			data.put("auth", auth);
			data.put("name", name);
			data.put("display", display);
			data.put("email", email);
			final StringBuilder groups = new StringBuilder(this.groups.length * 2);
			for (final int group : this.groups) {
				groups.append(',');
				groups.append(Integer.toString(group));
			}
			groups.deleteCharAt(0);
			data.put("groups", groups.toString());
			return data;
		}

		public static Account fromMap(final Map<String, String> auth) {
			final int id = Integer.parseInt(auth.get("member_id"));
			final String[] groups = auth.get("groups").split(",");
			final int[] groupIDs = new int[groups.length];
			for (int i = 0; i < groups.length; i++) {
				groupIDs[i] = Integer.parseInt(groups[i]);
			}
			return new Account(id, auth.get("auth"), auth.get("name"), auth.get("display"), auth.get("email"), groupIDs);
		}
	}
}
