package org.powerbot.service;

import org.powerbot.bot.Bot;
import org.powerbot.ipc.Controller;
import org.powerbot.ipc.Message;
import org.powerbot.script.methods.Environment;
import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.CryptFile;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.IniParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Paris
 */
public final class NetworkAccount {
	public static final int VIP = 1, DEVELOPER = 2, ADMIN = 4, LOCALSCRIPTS = 8, ORDER = 8;
	private final static String STORENAME = "netacct", AUTHKEY = "auth", RESPKEY = "response", CREATEDKEY = "created";
	private final static int CACHETTL = 24 * 60 * 60 * 1000;
	private static NetworkAccount instance = null;
	private final CryptFile store;
	private final Map<String, String> props;

	private NetworkAccount() {
		props = new HashMap<>();
		store = new CryptFile(STORENAME, true, NetworkAccount.class);
		revalidate();
	}

	public synchronized static NetworkAccount getInstance() {
		if (instance == null) {
			instance = new NetworkAccount();
		}
		return instance;
	}

	public static boolean validate(final Map<String, String> data) {
		if (data.isEmpty() || !data.containsKey("email") || !data.containsKey("name") || !data.containsKey("permissions")) {
			return false;
		}
		final String salt = (data.get("name") + data.get("email")).toUpperCase();
		final long hash;
		try {
			hash = IOHelper.crc32(StringUtil.getBytesUtf8(salt));
		} catch (final IOException ignored) {
			return false;
		}
		final long perms = Long.parseLong(data.get("permissions"));
		return perms >> ORDER == hash >> ORDER;
	}

	public boolean isLoggedIn() {
		return !props.isEmpty();
	}

	public boolean hasPermission(final int permission) {
		final String s = getProp("permissions");
		if (s == null || s.isEmpty()) {
			return false;
		}
		final long l = Long.parseLong(s);
		return (l & permission) == permission;
	}

	public String getProp(final String k) {
		return props.containsKey(k) ? props.get(k) : null;
	}

	public String getAuth() {
		return getProp(AUTHKEY);
	}

	public String getDisplayName() {
		final String[] s = {getProp("display"), getProp("name")};
		return s[s[0] == null || s[0].isEmpty() ? 1 : 0];
	}

	public synchronized void revalidate() {
		props.clear();

		if (store.exists()) {
			try {
				final Map<String, String> data = IniParser.deserialise(store.getInputStream()).get(IniParser.EMPTYSECTION);
				if (data.containsKey(CREATEDKEY) && Long.parseLong(data.get(CREATEDKEY)) + CACHETTL > System.currentTimeMillis()) {
					props.putAll(data);
				} else {
					login("", "", data.get(AUTHKEY));
				}
			} catch (final IOException ignored) {
			}

			if (!validate(props)) {
				logout();
			}
		}
	}

	public synchronized Map<String, String> login(final String username, final String password, final String auth) throws IOException {
		InputStream is = HttpClient.openStream(Configuration.URLs.SIGNIN, StringUtil.urlEncode(username),
				StringUtil.urlEncode(password), StringUtil.urlEncode(auth), Long.toString(Configuration.getUID()));

		boolean success = false;
		final Map<String, Map<String, String>> data = IniParser.deserialise(is);
		Map<String, String> resp = null;
		if (data != null && data.size() != 0 && data.containsKey(RESPKEY)) {
			resp = data.get(RESPKEY);
			success = isSuccess(resp) && data.containsKey(AUTHKEY);
		}

		if (success) {
			props.putAll(data.get(AUTHKEY));
			broadcast();
			updateCache();
		} else {
			logout();
		}

		return resp;
	}

	public boolean isSuccess(final Map<String, String> resp) {
		return resp != null && resp.containsKey("success") && IniParser.parseBool(resp.get("success"));
	}

	public synchronized void logout() {
		props.clear();
		broadcast();
		updateCache();
	}

	private synchronized void broadcast() {
		Controller.getInstance().broadcast(new Message(false, Message.SIGNIN));
	}

	private synchronized void updateCache() {
		String name = null, id = null;

		if (isLoggedIn()) {
			props.put(CREATEDKEY, Long.toString(System.currentTimeMillis()));
			final Map<String, Map<String, String>> map = new HashMap<>();
			map.put(IniParser.EMPTYSECTION, props);
			try {
				IniParser.serialise(map, store.getOutputStream());
			} catch (final IOException ignored) {
			}

			name = getDisplayName();
			id = getProp("member_id");
		} else {
			store.delete();
		}

		if (name != null && id != null && Bot.instantiated()) {
			Environment.getProperties().put("user.name", name);
			Environment.getProperties().put("user.id", id);
		}
	}
}
