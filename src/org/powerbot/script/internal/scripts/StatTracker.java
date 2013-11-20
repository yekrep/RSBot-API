package org.powerbot.script.internal.scripts;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
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
	private static final int UNIT = 10000, INTERVAL = 15;
	private final AtomicLong last;
	private final AtomicBoolean collecting;
	private final AtomicReference<String> name;
	private int[] cache = {};

	public StatTracker() {
		last = new AtomicLong(0L);
		collecting = new AtomicBoolean(false);
		name = new AtomicReference<String>("\0");
	}

	@Override
	public int poll() {
		if (ctx.players.local().isValid()) {
			collect();
		}

		return -1;
	}

	@Override
	public void stop() {
		collect();
	}

	private void collect() {
		final long v = System.nanoTime();
		if (TimeUnit.NANOSECONDS.toMinutes(v - last.get()) < INTERVAL) {
			return;
		}
		last.set(v);

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
		if (n == null || !n.equalsIgnoreCase(name.get())) {
			cache = new int[]{};
			name.set("\0");
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
