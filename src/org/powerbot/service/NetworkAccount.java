package org.powerbot.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import org.powerbot.util.Configuration;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IniParser;
import org.powerbot.util.io.SecureStore;

/**
 * @author Paris
 */
public final class NetworkAccount {
	private final static NetworkAccount instance = new NetworkAccount();
	private final static String FILENAME = "signin.ini";
	private Account account;

	private NetworkAccount() {
		try {
			final InputStream is = SecureStore.getInstance().read(FILENAME);
			if (is != null) {
				parseResponse(is);
			}
		} catch (final IOException ignored) {
		} catch (final GeneralSecurityException ignored) {
		}
	}

	public static NetworkAccount getInstance() {
		return instance;
	}

	public boolean isLoggedIn() {
		return account != null && account.getID() != 0;
	}

	public Account getAccount() {
		return account;
	}

	public boolean login(final String username, final String password) throws IOException {
		final URL url = new URL(Configuration.URLs.SIGNIN + "?u=" + URLEncoder.encode(username, "UTF-8") + "&p=" + URLEncoder.encode(password, "UTF-8"));
		InputStream is;
		try {
			is = HttpClient.openStream(url);
		} catch (final NullPointerException ignored) {
			return false;
		}
		final boolean success = parseResponse(is) && isLoggedIn();
		is = null;
		if (success) {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			IniParser.serialise(account.getMap(), bos);
			bos.close();
			is = new ByteArrayInputStream(bos.toByteArray());
		}
		try {
			SecureStore.getInstance().write(FILENAME, is);
		} catch (final GeneralSecurityException ignored) {
		}
		return success;
	}

	private boolean parseResponse(final InputStream is) throws IOException {
		final Map<String, Map<String, String>> data = IniParser.deserialise(is);
		if (data == null || data.size() == 0 || !data.containsKey("auth")) {
			return false;
		}
		final Map<String, String> auth = data.get("auth");
		final int id = Integer.parseInt(auth.get("member_id"));
		final String[] groups = auth.get("groups").split(",");
		final int[] groupIDs = new int[groups.length];
		for (int i = 0; i < groups.length; i++) {
			groupIDs[i] = Integer.parseInt(groups[i]);
		}
		account = new Account(id, auth.get("name"), auth.get("display"), auth.get("email"), groupIDs);
		return true;
	}

	public void logout() {
		account = null;
		try {
			SecureStore.getInstance().write(FILENAME, null);
		} catch (final IOException ignored) {
		} catch (final GeneralSecurityException ignored) {
		}
	}

	public final class Account {
		private final int id;
		private final String name, display, email;
		private final int[] groups;

		public Account(final int id, final String name, final String display, final String email, final int[] groups) {
			this.id = id;
			this.name = name;
			this.display = display;
			this.email = email;
			this.groups = groups;
		}

		public int getID() {
			return id;
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

		public Map<String, Map<String, String>> getMap() {
			final Map<String, String> auth = new HashMap<String, String>();
			auth.put("member_id", Integer.toString(id));
			auth.put("name", name);
			auth.put("display", display);
			auth.put("email", email);
			final StringBuilder groups = new StringBuilder(this.groups.length * 2);
			for (final int group : this.groups) {
				groups.append(',');
				groups.append(Integer.toString(group));
			}
			groups.deleteCharAt(0);
			auth.put("groups", groups.toString());
			final Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
			data.put("auth", auth);
			return data;
		}
	}
}
