package org.powerbot.bot.rt6;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.ClientContext;

public class CameraPattern extends Antipattern.Module {
	public CameraPattern(final ClientContext ctx) {
		super(ctx);
	}

	@Override
	public void run() {
		final boolean a = isAggressive();
		final float t = ctx.camera.yaw(), c = Random.nextInt(1, 3) * (a ? 2 : 1);

		for (int i = 0; i < c; i++) {
			final String k = Random.nextBoolean() ? "LEFT" : "RIGHT";
			ctx.input.send("{VK_" + k + " down}");
			Condition.sleep(a ? 300 : 800);
			ctx.input.send("{VK_" + k + " up}");
		}

		if (isStateful()) {
			final int d = 10;
			ctx.camera.angle((int) t + Random.nextInt(-d, d));
		}
	}
}
