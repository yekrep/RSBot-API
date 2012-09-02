package org.powerbot.ipc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.bot.Bot;
import org.powerbot.service.NetworkAccount;

/**
 * @author Paris
 */
public final class ScheduledChecks implements ActionListener {
	private final static Logger log = Logger.getLogger(ScheduledChecks.class.getName());
	public static volatile long SESSION_TIME = 0;
	public static final int LOCALSCRIPT_TIMEOUT = 60 * 3;

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (!Controller.getInstance().isBound() || Controller.getInstance().getRunningInstances() < 1) {
			System.exit(1);
		}

		if (NetworkAccount.getInstance().isLoggedIn()) {
			final long a = Controller.getInstance().getLastSessionUpdateTime();
			if (a < System.currentTimeMillis() - 1000 * 60 * 10) {
				SESSION_TIME = System.currentTimeMillis();
				NetworkAccount.getInstance().session(0);
			}
		}

		if (Bot.isInstantiated() && Bot.getInstance().getActiveScript() != null) {
			final ActiveScript script = Bot.getInstance().getActiveScript();
			if (script.getDefinition().local && !NetworkAccount.getInstance().isDeveloper() && script.started < System.currentTimeMillis() - 1000 * LOCALSCRIPT_TIMEOUT && !script.getContainer().isShutdown()) {
				log.info("Local script stopped after timeout - not authorised developer");
				script.kill();
			}
		}
	}
}
