package org.powerbot.game.api.methods;

import org.powerbot.service.NetworkAccount;

public class Environment {
	public static String getDisplayName() {
		if (NetworkAccount.getInstance().isLoggedIn()) {
			return NetworkAccount.getInstance().getAccount().getDisplayName();
		}
		return null;
	}

	public static int getUserId() {
		if (NetworkAccount.getInstance().isLoggedIn()) {
			return NetworkAccount.getInstance().getAccount().getID();
		}
		return -1;
	}
}
