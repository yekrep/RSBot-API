package org.powerbot.script.golem;

import java.util.Arrays;

import org.powerbot.script.methods.Hud;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;

public class WindowPattern extends Antipattern {
	public WindowPattern(final MethodContext factory) {
		super(factory);
	}

	@Override
	public void run() {
		final boolean a = isAggressive();

		int d = 0;
		Hud.Window[] windows = Hud.Window.values();
		for (int i = 0; i < windows.length; i++) {
			Hud.Window w = windows[i];
			if (!ctx.hud.isOpen(w)) {
				continue;
			}

			windows[d++] = w;
		}
		windows = Arrays.copyOf(windows, d);
		if (windows.length < 1) {
			return;
		}

		for (int i = a ? Random.nextInt(0, 3) : 2; i < 4; i++) {
			Hud.Window w = windows[Random.nextInt(0, windows.length)];
			if (!ctx.hud.view(w)) {
				break;
			}

			if (a) {
				sleep(Random.nextInt(0, Random.nextInt(1000, 2000)));
			}
		}
	}
}
