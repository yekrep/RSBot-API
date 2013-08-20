package org.powerbot.script.golem;

import java.util.EnumSet;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;

/**
 * A humanised layer which disrupts mathematically recognisable patterns of gameplay actions.
 *
 * @author Paris
 */
public abstract class Antipattern extends MethodProvider {
	/**
	 * Preferred assertions for execution behaviour.
	 */
	public enum Preference {
		/**
		 * Run asynchronously, otherwise no-op.
		 */
		ASYNC,

		/**
		 * Perform extra aggressive techniques.
		 */
		AGGRESSIVE,

		/**
		 * Attempt to return back to the prior state.
		 */
		STATEFUL,
	}

	public Antipattern(MethodContext factory) {
		super(factory);
	}

	/**
	 * Executes the antipattern routine.
	 *
	 * @param preferences the preferred assertions
	 */
	public abstract void run(EnumSet<Preference> preferences);
}
