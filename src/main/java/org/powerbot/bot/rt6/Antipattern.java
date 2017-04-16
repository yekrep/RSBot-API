package org.powerbot.bot.rt6;

import java.util.concurrent.atomic.AtomicInteger;

import org.powerbot.script.PollingScript;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;

public class Antipattern extends PollingScript<ClientContext> {
	private final Module[] modules;

	public Antipattern() {
		modules = new Module[]{
				new ExaminePattern(ctx),
				new CameraPattern(ctx),
				new WindowPattern(ctx),
		};
	}

	@Override
	public void poll() {
		if (ctx.properties.getProperty("antipattern.disable", "").equals("true")) {
			return;
		}
		for (final Module m : modules) {
			if (m.isTick()) {
				m.run();
			}
		}
	}

	public static abstract class Module extends ClientAccessor implements Runnable {
		protected final AtomicInteger freq;
		private long when = 0L;

		public Module(final ClientContext ctx) {
			super(ctx);
			freq = new AtomicInteger(30);
			isTick();
		}

		public boolean isTick() {
			final boolean r = when != 0L && System.nanoTime() > when;
			when = System.nanoTime() + (long) (freq.get() * 60 * 1e9 * Random.nextDouble(1d, 3d));
			return r;
		}

		public boolean isAggressive() {
			return System.currentTimeMillis() % 5 == 0;
		}

		public boolean isStateful() {
			return !isAggressive();
		}
	}
}
