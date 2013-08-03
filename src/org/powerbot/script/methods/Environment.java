package org.powerbot.script.methods;

import org.powerbot.service.NetworkAccount;

public class Environment extends MethodProvider {

	public Environment(MethodContext factory) {
		super(factory);
	}

	public static String getDisplayName() {
		final NetworkAccount n = NetworkAccount.getInstance();
		return n.isLoggedIn() ? n.getDisplayName() : null;
	}

	public static int getUserId() {
		final NetworkAccount n = NetworkAccount.getInstance();
		if (!n.isLoggedIn()) {
			return -1;
		}
		try {
			return Integer.parseInt(n.getProp("member_id"));
		} catch (final NumberFormatException ignored) {
			return -1;
		}
	}
}
