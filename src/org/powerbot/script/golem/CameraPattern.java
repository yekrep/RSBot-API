package org.powerbot.script.golem;

import java.util.EnumSet;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;

public class CameraPattern extends Antipattern {
	private static final int STANDARD_DEVIATION = 10;

	public CameraPattern(MethodContext factory) {
		super(factory);
	}

	@Override
	public void run(EnumSet<Preference> preferences) {
		boolean aggressive = System.nanoTime() % 5 == 0;
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

		if (preferences.contains(Preference.STATEFUL)) {
			ctx.camera.setAngle(angle + Random.nextInt(-STANDARD_DEVIATION, STANDARD_DEVIATION));
		}
	}
}
