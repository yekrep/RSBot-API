package org.powerbot.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.powerbot.misc.NetworkAccount;

public class CliController implements Runnable {
	private static final Logger log = Logger.getLogger("CLI");
	private final BotChrome chrome;

	public CliController(final BotChrome chrome) {
		this.chrome = chrome;
	}

	@Override
	public void run() {
		final String user = System.getProperty("bot.login.user", ""), pass = System.getProperty("bot.login.pass", "");

		if (!user.isEmpty()) {
			final NetworkAccount n = NetworkAccount.getInstance();

			if (pass.isEmpty()) {
				log.info("Logging out of account, no password provided");
				n.logout();
			} else {
				log.info("Logging into account " + user + " with password");
				n.logout();
				n.login(user, pass, "");
			}
		}

		final List<String> r = new ArrayList<String>();
		for (final Object e : System.getProperties().keySet()) {
			if (e instanceof String && ((String) e).startsWith("bot.")) {
				r.add((String) e);
			}
		}
		for (final String e : r) {
			System.clearProperty(e);
		}
	}
}
