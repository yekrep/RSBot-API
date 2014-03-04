package org.powerbot.script.rs3.tools;

import org.powerbot.misc.NetworkAccount;

/**
 * Provides basic details of the current user.
 * This class is now deprecated in favour of the properties prefixed with <i>user</i>.
 */
@Deprecated
public class Environment extends ClientAccessor {
	public Environment(final ClientContext factory) {
		super(factory);
	}

	/**
	 * Returns the logged in user's forum display name.
	 *
	 * @return the display name
	 * @deprecated use the property <i>user.name</i>
	 * @see ClientContext#properties
	 */
	@Deprecated
	public static String getDisplayName() {
		final NetworkAccount n = NetworkAccount.getInstance();
		return n.isLoggedIn() ? n.getDisplayName() : null;
	}

	/**
	 * Returns the logged in user's id.
	 *
	 * @return the id of the user
	 * @deprecated use the property <i>user.id</i>
	 * @see ClientContext#properties
	 */
	@Deprecated
	public static int getUserId() {
		return NetworkAccount.getInstance().getUID();
	}
}
