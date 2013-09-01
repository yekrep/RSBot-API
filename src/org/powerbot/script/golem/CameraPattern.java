package org.powerbot.script.golem;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;

public class CameraPattern extends Antipattern {

	public CameraPattern(final MethodContext factory) {
		super(factory);
	}

	@Override
	public void run() {
		boolean aggressive = isAggressive();
		int angle = ctx.camera.getYaw();
		int c = aggressive ?
				Random.nextInt(1, 6) :
				Random.nextInt(1, 3);
		for (int i = 0; i < c; i++) {
			boolean left = Random.nextBoolean();
			ctx.keyboard.send(left ? "{VK_LEFT down}" : "{VK_RIGHT down}");
			if (aggressive) {
				sleep(100, Random.nextInt(200, 300));
			} else {
				sleep(100, 800);
			}
			ctx.keyboard.send(left ? "{VK_LEFT up}" : "{VK_RIGHT up}");
		}

		if (isStateful()) {
			final int dev = 10;
			ctx.camera.setAngle(angle + Random.nextInt(-dev, dev));
		}
	}
}
