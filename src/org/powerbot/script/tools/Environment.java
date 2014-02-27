package org.powerbot.script.tools;

import org.powerbot.misc.NetworkAccount;

/**
 * Provides basic details of the current user.
 * This class is now deprecated in favour of the properties prefixed with <i>user</i>.
 */
@Deprecated
public class Environment extends MethodProvider {
	public Environment(final MethodContext factory) {
		super(factory);
	}

	/**
	 * Returns the logged in user's forum display name.
	 *
	 * @return the display name
	 * @deprecated use the property <i>user.name</i>
	 * @see org.powerbot.script.tools.MethodContext#properties
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
	 * @see org.powerbot.script.tools.MethodContext#properties
	 */
	@Deprecated
	public static int getUserId() {
		return NetworkAccount.getInstance().getUID();
	}
}
