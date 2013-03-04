package org.powerbot.ipc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.powerbot.bot.Bot;
import org.powerbot.script.internal.ScriptHandler;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.util.Configuration;
import org.powerbot.util.Tracker;

/**
 * @author Paris
 */
public final class ScheduledChecks implements ActionListener {
	private final static Logger log = Logger.getLogger(ScheduledChecks.class.getName());
	public static volatile long SESSION_TIME = 0;
	public static final long LOCALSCRIPT_TIMEOUT = 15 * 60000000000L;
	public static final AtomicLong timeout = new AtomicLong(0);
	private static final long started = System.nanoTime();

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (!Controller.getInstance().isBound() || Controller.getInstance().getRunningInstances() < 1) {
			Tracker.getInstance().trackEvent("exit", "instance-overload");
			System.exit(1);
		}

		final long uptime = TimeUnit.NANOSECONDS.toMinutes(System.nanoTime() - started);
		if (uptime % 10 == 0) {
			Tracker.getInstance().trackEvent("uptime", Long.toString(uptime));
		}

		if (Bot.instantiated() && Bot.instance().getScriptHandler() != null) {
			final ScriptHandler script = Bot.instance().getScriptHandler();
			final ScriptDefinition definition;
			if ((definition = script.getDefinition()) != null && definition.local && System.nanoTime() > timeout.get()) {
				Tracker.getInstance().trackEvent("script", "timeout", definition.getName());
				log.info("Local script restriction - script stopped");
				script.stop();
			}
		}

		if (Configuration.VERSION < Configuration.VERSION_LATEST || Configuration.VERSION_LATEST == -1) {
			Tracker.getInstance().trackEvent("exit", "version");
			System.exit(1);
		}
	}
}
