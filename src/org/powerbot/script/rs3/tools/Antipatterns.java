package org.powerbot.script.rs3.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.powerbot.bot.script.Antipattern;
import org.powerbot.bot.script.ScriptController;
import org.powerbot.bot.script.environment.CameraPattern;
import org.powerbot.bot.script.environment.ExaminePattern;
import org.powerbot.bot.script.environment.WindowPattern;

/**
 * Executes anti-patterns to disrupt distinguishable patterns.
 *
 * @author Paris
 */
public class Antipatterns extends MethodProvider implements Runnable {
	private final List<Antipattern> patterns;
	private final AtomicBoolean enabled;

	public Antipatterns(final MethodContext ctx) {
		super(ctx);

		enabled = new AtomicBoolean(true);

		patterns = new ArrayList<Antipattern>();
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

	/**
	 * Enables or disables antipatterns. This affects local {@link org.powerbot.script.Script}s only.
	 *
	 * @param v {@code true} to enable, otherwise {@code false}
	 * @return the enabled state of antipatterns
	 */
	public boolean setEnabled(final boolean v) {
		if (Boolean.parseBoolean(ctx.properties.getProperty(ScriptController.LOCAL_PROPERTY, Boolean.toString(false)))) {
			return false;
		}
		enabled.set(v);
		return v;
	}

	/**
	 * Returns the enabled state of antipatterns.
	 *
	 * @return {@code true} if antipatterns are enabled, otherwise {@code false}
	 */
	public boolean isEnabled() {
		return enabled.get();
	}
}
