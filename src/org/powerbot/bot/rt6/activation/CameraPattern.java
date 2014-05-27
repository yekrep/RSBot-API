package org.powerbot.bot.rt6.activation;

import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.Random;

public class CameraPattern extends Antipattern.Module {
	public CameraPattern(final ClientContext ctx) {
		super(ctx);
	}

	@Override
	public void run() {
		final boolean a = isAggressive();
		final int t = ctx.camera.yaw(), c = Random.nextInt(1, 3) * (a ? 2 : 1);

		for (int i = 0; i < c; i++) {
			final String k = Random.nextBoolean() ? "LEFT" : "RIGHT";
			ctx.input.send("{VK_" + k + " down}");
			try {
				Thread.sleep(100, a ? Random.nextInt(200, 300) : 800);
			} catch (final InterruptedException ignored) {
			}
			ctx.input.send("{VK_" + k + " up}");
		}

		if (isStateful()) {
			final int d = 10;
			ctx.camera.angle(t + Random.nextInt(-d, d));
		}
	}
}
