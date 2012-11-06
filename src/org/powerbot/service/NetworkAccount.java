package org.powerbot.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.powerbot.ipc.Controller;
import org.powerbot.ipc.Message;
import org.powerbot.ipc.Message.MessageType;
import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.CryptFile;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.IniParser;

/**
 * @author Paris
 */
public final class NetworkAccount {
	private static NetworkAccount instance = null;
	private final static String STORENAME = "netacct", AUTHKEY = "auth", CREATEDKEY = "created";
	private final static int CACHETTL = 24 * 60 * 60 * 1000;
	private final CryptFile store;
	private Account account;

	public static final class Permissions {
		public static final int VIP = 1, DEVELOPER = 2, ADMIN = 4, LOCALSCRIPTS = 8, ORDER = 8;
	}

	private NetworkAccount() {
		store = new CryptFile(STORENAME, NetworkAccount.class);
		revalidate();
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

	public boolean hasPermission(final int permission) {
		return account != null && (account.getPermissions() & permission) == permission;
	}

	public Account getAccount() {
		return account;
	}

	public synchronized void revalidate() {
		account = null;

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

			if (!isLoggedIn() || !account.validate()) {
				store.delete();
			}
		}
	}

	public synchronized boolean login(final String username, final String password, final String auth) throws IOException {
		InputStream is;
		try {
			is = HttpClient.openStream(Configuration.URLs.SIGNIN, StringUtil.urlEncode(username), StringUtil.urlEncode(password), StringUtil.urlEncode(auth));
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
		broadcast();
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
		broadcast();
	}

	private synchronized void broadcast() {
		Controller.getInstance().broadcast(new Message(false, MessageType.SIGNIN));
	}

	public final static class Account {
		private final int id;
		private final long permissions;
		private final String auth, name, display, email;
		private final int[] groups;

		public Account(final int id, final String auth, final String name, final String display, final String email, final long permissions, final int[] groups) {
			this.id = id;
			this.auth = auth;
			this.name = name;
			this.display = display;
			this.email = email;
			this.permissions = permissions;
			this.groups = groups;
		}

		public boolean validate() {
			final String salt = (getName() + getEmail()).toUpperCase();
			final long hash;
			try {
				hash = IOHelper.crc32(StringUtil.getBytesUtf8(salt));
			} catch (final IOException ignored) {
				return false;
			}
			return getPermissions() >> Permissions.ORDER == hash >> Permissions.ORDER;
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

		public long getPermissions() {
			return permissions;
		}

		public int[] getGroupIDs() {
			return groups;
		}

		public Map<String, String> getMap() {
			final Map<String, String> data = new HashMap<String, String>();
			data.put("member_id", Integer.toString(id));
			data.put("auth", auth);
			data.put("name", name);
			data.put("display", display);
			data.put("email", email);
			data.put("permissions", Long.toString(permissions));
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
			return new Account(id, auth.get("auth"), auth.get("name"), auth.get("display"), auth.get("email"), Long.parseLong(auth.get("permissions")), groupIDs);
		}
	}
}
