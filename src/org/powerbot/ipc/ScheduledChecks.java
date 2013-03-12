package org.powerbot.ipc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.powerbot.bot.Bot;
import org.powerbot.script.Manifest;
import org.powerbot.script.Script;
import org.powerbot.script.internal.ScriptManager;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.util.Configuration;
import org.powerbot.util.Tracker;

/**
 * @author Paris
 */
public final class ScheduledChecks implements ActionListener {
	public static final long LOCALSCRIPT_TIMEOUT = 15 * 60000000000L;
	public static final AtomicLong timeout = new AtomicLong(0);
	private final static Logger log = Logger.getLogger(ScheduledChecks.class.getName());
	private static final long started = System.nanoTime();
	public static volatile long SESSION_TIME = 0;

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

		final ScriptManager controller = Bot.instance().getScriptController();
		if (controller != null) {
			final ScriptDefinition definition = Bot.instance().getScriptDefinition();
			if (definition != null) {
				if (definition.local && System.nanoTime() > timeout.get()) {
					Tracker.getInstance().trackEvent("script", "timeout", definition.getName());
					log.info("Local script restriction - script stopped");
					controller.stop();
				}
			}

			final Collection<String> running = Controller.getInstance().getRunningScripts();
			for (final Script script : controller.getScripts()) {
				final Manifest manifest = script.getClass().getAnnotation(Manifest.class);
				if (manifest != null) {
					int n = 0;
					for (final String check : running) {
						if (definition.getID().equals(check)) {
							n++;
						}
					}
					if (n > manifest.instantces()) {
						if (Controller.getInstance().getRunningScripts().contains(definition.getID())) {
							Tracker.getInstance().trackEvent("script", "instance-bypass", definition.getID());
							controller.stop();
						}
					}
				}
			}
		}

		if (Configuration.VERSION < Configuration.VERSION_LATEST || Configuration.VERSION_LATEST == -1) {
			Tracker.getInstance().trackEvent("exit", "version");
			System.exit(1);
		}
	}
}
