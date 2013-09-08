package org.powerbot.script.methods;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.internal.Antipattern;
import org.powerbot.script.internal.scripts.pattern.CameraPattern;
import org.powerbot.script.internal.scripts.pattern.ExaminePattern;
import org.powerbot.script.internal.scripts.pattern.WindowPattern;

/**
 * Executes anti-patterns to disrupt distinguishable patterns.
 *
 * @author Paris
 */
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

	/**
	 * Registers (adds) an anti-pattern to the internal executor.
	 *
	 * @param a the antipattern
	 * @return <tt>true</tt> if registered; otherwise <tt>false</tt>
	 */
	public boolean register(final Antipattern a) {
		return !patterns.contains(a) && patterns.add(a);
	}
}
