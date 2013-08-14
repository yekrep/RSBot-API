package org.powerbot.script.golem;

/**
 * A humanised layer which disrupts mathematically recognisable patterns of gameplay actions.
 *
 * @author Paris
 */
public abstract class Antipattern {

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
	};

	public Antipattern() {
	}

	/**
	 * Executes the antipattern routine.
	 *
	 * @param preference the preferred assertions
	 */
	public abstract void run(final Preference preference);
}
