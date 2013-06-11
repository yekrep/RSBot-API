package org.powerbot.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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
	private static NetworkAccount instance = null;
	private final static String STORENAME = "netacct", RESPKEY = "response", AUTHKEY = "auth", CREATEDKEY = "created";
	private final static int CACHETTL = 24 * 60 * 60 * 1000;
	public final static int REVALIDATE_INTERVAL = 5000;
	private final CryptFile store, scripts;
	private final Map<String, String> props, response;
	private final AtomicLong updated;

	public static final int VIP = 1, DEVELOPER = 2, ADMIN = 4, LOCALSCRIPTS = 8, ORDER = 8;

	private NetworkAccount() {
		props = new ConcurrentHashMap<>();
		response = new ConcurrentHashMap<>();
		store = new CryptFile(STORENAME, false, NetworkAccount.class);
		scripts = new CryptFile("scripts.1.ini", NetworkAccount.class);
		updated = new AtomicLong(0);
		revalidate();

		new Thread(new Runnable() {
			@Override
			public void run() {
				for (;;) {
					final long m = store.lastModified();
					if (m == 0 ? updated.get() != 0 : updated.get() < m) {
						revalidate();
					}
					try {
						Thread.sleep(REVALIDATE_INTERVAL);
					} catch (final InterruptedException ignored) {
					}
				}
			}
		}).start();
	}

	public synchronized static NetworkAccount getInstance() {
		if (instance == null) {
			instance = new NetworkAccount();
		}
		return instance;
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
		return props.get(k);
	}

	public String getResponse(final String k) {
		return response.get(k);
	}

	public String getAuth() {
		return getProp(AUTHKEY);
	}

	public String getDisplayName() {
		final String[] s = { getProp("display"), getProp("name") };
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

			if (!isValid(props)) {
				logout();
			}
		}

		updated.set(store.lastModified());
	}

	public synchronized boolean login(final String username, final String password, final String auth) throws IOException {
		InputStream is;
		try {
			is = HttpClient.openStream(Configuration.URLs.SIGNIN, StringUtil.urlEncode(username), StringUtil.urlEncode(password), StringUtil.urlEncode(auth));
		} catch (final NullPointerException ignored) {
			ignored.printStackTrace();
			return false;
		}
		final boolean success = readData(is);
		if (success) {
			updateCache();
		} else {
			logout();
		}
		return success;
	}

	private boolean readData(final InputStream is) throws IOException {
		final Map<String, Map<String, String>> data = IniParser.deserialise(is);
		if (data == null || data.size() == 0) {
			return false;
		}
		response.putAll(data.get(RESPKEY));
		if (!data.containsKey(AUTHKEY)) {
			return false;
		}
		props.putAll(data.get(AUTHKEY));
		return true;
	}

	public synchronized void logout() {
		props.clear();
		updateCache();
	}

	public static boolean isValid(final Map<String, String> data) {
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

	private synchronized void updateCache() {
		if (isLoggedIn()) {
			props.put(CREATEDKEY, Long.toString(System.currentTimeMillis()));
			final Map<String, Map<String, String>> map = new HashMap<>();
			map.put(IniParser.EMPTYSECTION, props);
			try {
				IniParser.serialise(map, store.getOutputStream());
			} catch (final IOException ignored) {
			}
		} else {
			store.delete();
			scripts.delete();
		}
	}

	public synchronized InputStream getScriptsList() throws IOException {
		return scripts.download(Configuration.URLs.SCRIPTSCOLLECTION, getAuth());
	}
}
