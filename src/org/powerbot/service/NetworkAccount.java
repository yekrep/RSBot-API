package org.powerbot.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.powerbot.Configuration;
import org.powerbot.util.Ini;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.CryptFile;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;

/**
 * @author Paris
 */
public final class NetworkAccount {
	private static NetworkAccount instance = null;
	private final static String STORENAME = "netacct", RESPKEY = "response", AUTHKEY = "auth", CREATEDKEY = "created";
	private final static int CACHETTL = 24 * 60 * 60 * 1000;
	public final static int REVALIDATE_INTERVAL = 5000;
	private final CryptFile store, scripts;
	private final Ini data;
	private final AtomicLong updated;

	public static final int VIP = 1, DEVELOPER = 2, ADMIN = 4, LOCALSCRIPTS = 8, ORDER = 8;

	private NetworkAccount() {
		data = new Ini();
		store = new CryptFile(STORENAME, false, NetworkAccount.class);
		scripts = new CryptFile("scripts.1.ini", NetworkAccount.class);
		updated = new AtomicLong(0);
		revalidate();

		new Thread(new Runnable() {
			@Override
			public void run() {
				for (; ; ) {
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
		return data.has(AUTHKEY);
	}

	public boolean hasPermission(final int permission) {
		return (data.get(AUTHKEY).getLong("permission") & permission) == permission;
	}

	public String getProp(final String k) {
		return data.get(AUTHKEY).get(k);
	}

	public String getResponse(final String k) {
		return data.get(RESPKEY).get(k);
	}

	public String getAuth() {
		return getProp(AUTHKEY);
	}

	public String getDisplayName() {
		final String[] s = {getProp("display"), getProp("name")};
		return s[s[0] == null || s[0].isEmpty() ? 1 : 0];
	}

	public synchronized void revalidate() {
		data.clear();

		if (store.exists()) {
			try (final InputStream is = store.getInputStream()) {
				data.read(is);
			} catch (final IOException ignored) {
			}

			if (Long.parseLong(data.get(AUTHKEY).get(CREATEDKEY, "0")) + CACHETTL < System.currentTimeMillis()) {
				login("", "", getAuth());
			}

			if (!isValid(data.get(AUTHKEY))) {
				logout();
			}
		}

		updated.set(store.lastModified());
	}

	public synchronized boolean login(final String username, final String password, final String auth) {
		try (final InputStream is = HttpClient.openStream(Configuration.URLs.SIGNIN, StringUtil.urlEncode(username), StringUtil.urlEncode(password), StringUtil.urlEncode(auth))) {
			data.read(is);
			updateCache();
			return true;
		} catch (final IOException ignored) {
		}
		return false;
	}

	public synchronized void logout() {
		data.clear();
		updateCache();
	}

	public static boolean isValid(final Ini.Member data) {
		if (!data.has("email") || !data.has("name") || !data.has("permissions")) {
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
			data.get(AUTHKEY).put(CREATEDKEY, System.currentTimeMillis());
			try (final OutputStream os = store.getOutputStream()) {
				data.write(os);
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
