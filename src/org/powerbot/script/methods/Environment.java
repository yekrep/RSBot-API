package org.powerbot.script.methods;

import org.powerbot.service.NetworkAccount;

public class Environment extends MethodProvider {
	public Environment(MethodContext factory) {
		super(factory);
	}

	/**
	 * Returns the logged in user's forum display name.
	 *
	 * @return the display name
	 */
	public static String getDisplayName() {
		final NetworkAccount n = NetworkAccount.getInstance();
		return n.isLoggedIn() ? n.getDisplayName() : null;
	}

	/**
	 * Returns the logged in user's id.
	 *
	 * @return the id of the user
	 */
	public static int getUserId() {
		return NetworkAccount.getInstance().getUID();
	}
}
