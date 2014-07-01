package org.powerbot.bot.rt6;

import java.util.Arrays;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Hud;

public class WindowPattern extends Antipattern.Module {
	public WindowPattern(final ClientContext ctx) {
		super(ctx);
		freq.set(15);
	}

	@Override
	public void run() {
		final boolean a = isAggressive();

		int d = 0;
		Hud.Window[] windows = Hud.Window.values();
		for (int i = 0; i < windows.length; i++) {
			final Hud.Window w = windows[i];
			if (!ctx.hud.floating(w) || ctx.hud.opened(w)) {
				continue;
			}
			if (w.menu() == Hud.Menu.NONE) {
				continue;
			}

			windows[d++] = w;
		}
		windows = Arrays.copyOf(windows, d);
		if (windows.length < 1) {
			return;
		}

		for (int i = a ? Random.nextInt(0, 3) : 2; i < 4; i++) {
			final Hud.Window w = windows[Random.nextInt(0, windows.length)];
			if (!ctx.hud.open(w)) {
				break;
			}

			if (a) {
				Condition.sleep(1200);
			}
		}
	}
}
