package org.powerbot.ipc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.powerbot.core.bot.Bot;
import org.powerbot.core.bot.handlers.ScriptHandler;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.util.Configuration;

/**
 * @author Paris
 */
public final class ScheduledChecks implements ActionListener {
	private final static Logger log = Logger.getLogger(ScheduledChecks.class.getName());
	public static volatile long SESSION_TIME = 0;
	public static final long LOCALSCRIPT_TIMEOUT = 15 * 60000000000L;
	public static final AtomicLong timeout = new AtomicLong(0);

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (!Controller.getInstance().isBound() || Controller.getInstance().getRunningInstances() < 1) {
			System.exit(1);
		}

		if (Bot.instantiated() && Bot.instance().getScriptHandler() != null) {
			final ScriptHandler script = Bot.instance().getScriptHandler();
			final ScriptDefinition definition;
			if ((definition = script.getDefinition()) != null && definition.local && System.nanoTime() > timeout.get()) {
				log.info("Local script restriction - script stopped");
				script.stop();
			}
		}

		if (Configuration.VERSION < Configuration.VERSION_LATEST || Configuration.VERSION_LATEST == -1) {
			System.exit(1);
		}
	}
}
