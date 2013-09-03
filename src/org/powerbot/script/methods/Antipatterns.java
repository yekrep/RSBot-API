package org.powerbot.script.methods;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.golem.Antipattern;
import org.powerbot.script.golem.CameraPattern;
import org.powerbot.script.golem.ExaminePattern;
import org.powerbot.script.golem.WindowPattern;

public class Antipatterns extends MethodProvider implements Runnable {
	private final List<Antipattern> patterns;

	public Antipatterns(final MethodContext ctx) {
		super(ctx);

		patterns = new ArrayList<>();
		patterns.add(new CameraPattern(ctx));
		patterns.add(new ExaminePattern(ctx));
		patterns.add(new WindowPattern(ctx));
	}

	@Override
	public void run() {
		for (final Antipattern a : patterns) {
			if (a.isValid() && a.isTick()) {
				a.run();
			}
		}
	}

	public boolean register(final Antipattern a) {
		return !patterns.contains(a) && patterns.add(a);
	}
}
