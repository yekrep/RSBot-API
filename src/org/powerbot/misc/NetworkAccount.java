package org.powerbot.misc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;

import org.powerbot.Configuration;
import org.powerbot.util.HttpUtils;
import org.powerbot.util.IOUtils;
import org.powerbot.util.Ini;
import org.powerbot.util.StringUtils;

public final class NetworkAccount {
	private static NetworkAccount instance = null;
	private final static String STORENAME = "netacct", RESPKEY = "response", AUTHKEY = "auth", CREATEDKEY = "created";
	private final static int CACHETTL = 24 * 60 * 60 * 1000;
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
	}

	public synchronized static NetworkAccount getInstance() {
		if (instance == null) {
			instance = new NetworkAccount();
		}
		return instance;
	}

	public boolean isLoggedIn() {
		return data.get(AUTHKEY).has(AUTHKEY);
	}

	public boolean hasPermission(final int permission) {
		return (data.get(AUTHKEY).getLong("permissions") & permission) == permission;
	}

	String getAuth() {
		return data.get(AUTHKEY).get(AUTHKEY);
	}

	public int getUID() {
		return data.get(AUTHKEY).getInt("member_id", -1);
	}

	public String getDisplayName() {
		return data.get(AUTHKEY).get("display");
	}

	public String getResponse() {
		return data.get(RESPKEY).get("message");
	}

	synchronized void revalidate() {
		data.clear();

		if (store.exists()) {
			InputStream is = null;
			try {
				is = store.getInputStream();
				data.read(is);
			} catch (final IOException ignored) {
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (final IOException ignored) {
					}
				}
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
		InputStream is = null;
		try {
			is = HttpUtils.openStream(new URL(String.format(Configuration.URLs.LOGIN, StringUtils.urlEncode(username), StringUtils.urlEncode(password), StringUtils.urlEncode(auth))));
			data.read(is);
			updateCache();
			return true;
		} catch (final IOException ignored) {
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (final IOException ignored) {
				}
			}
		}
		return false;
	}

	public synchronized void logout() {
		data.clear();
		updateCache();
	}

	private static boolean isValid(final Ini.Member data) {
		if (!data.has("email") || !data.has("name") || !data.has("permissions")) {
			return false;
		}
		final String salt = (data.get("name") + data.get("email")).toUpperCase();
		final long hash = IOUtils.crc32(StringUtils.getBytesUtf8(salt));
		final long perms = Long.parseLong(data.get("permissions"));
		return perms >> ORDER == hash >> ORDER;
	}

	private synchronized void updateCache() {
		if (isLoggedIn()) {
			data.get(AUTHKEY).put(CREATEDKEY, System.currentTimeMillis());
			OutputStream os = null;
			try {
				os = store.getOutputStream();
				data.write(os);
			} catch (final IOException ignored) {
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (final IOException ignored) {
					}
				}
			}
		} else {
			store.delete();
			scripts.delete();
		}
	}

	public synchronized InputStream getScriptsList() throws IOException {
		return scripts.download(new URL(String.format(Configuration.URLs.SCRIPTS, getAuth())));
	}
}
