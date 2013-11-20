package org.powerbot.script.internal.scripts;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.script.PollingScript;
import org.powerbot.script.internal.InternalScript;
import org.powerbot.script.internal.ScriptController;
import org.powerbot.script.wrappers.Player;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.util.Tracker;

/**
 * @author Paris
 */
public class StatTracker extends PollingScript implements InternalScript {
	private static final int UNIT = 10000;
	private final AtomicBoolean collecting;
	private final AtomicReference<String> name;
	private int[] cache = {};

	public StatTracker() {
		collecting = new AtomicBoolean(false);
		name = new AtomicReference<String>("\0");
	}

	@Override
	public int poll() {
		if (!ctx.players.local().isValid()) {
			return -1;
		}

		collect();
		return 1000 * 60 * 15;
	}

	@Override
	public void stop() {
		collect();
	}

	private void collect() {
		final Player p = ctx.players.local();

		if (!p.isValid()) {
			return;
		}

		final Controller c = getController();
		if (!(c instanceof ScriptController)) {
			return;
		}
		final ScriptDefinition def = ((ScriptController) c).getDefinition();
		final String id = def.local ? ScriptDefinition.LOCALID : def.getID();

		if (id == null || id.isEmpty()) {
			return;
		}

		final String n = p.getName();
		if (!n.equalsIgnoreCase(name.get())) {
			cache = new int[]{};
			name.set(n);
		}

		final int[] exp;
		try {
			exp = ctx.skills.getExperiences();
		} catch (final Exception ignored) {
			return;
		}

		final Tracker t = Tracker.getInstance();

		if (!collecting.compareAndSet(false, true)) {
			return;
		}

		for (int i = 0; i < Math.min(exp.length, cache.length); i++) {
			final String s = Integer.toString(i);
			final int x = cache[i], y = exp[i];
			int d = y - x;

			while (d > UNIT) {
				t.trackEvent("exp", id, s);
				d -= UNIT;
			}

			exp[i] -= d;
		}

		cache = exp;
		collecting.set(false);
	}
}
