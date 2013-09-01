package org.powerbot.script.golem;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;

/**
 * A humanised layer which disrupts mathematically recognisable patterns of gameplay actions.
 *
 * @author Paris
 */
public abstract class Antipattern extends MethodProvider implements Runnable {

	public Antipattern(final MethodContext factory) {
		super(factory);
	}

	protected boolean isStateful() {
		return true;
	}

	protected boolean isAggressive() {
		return System.nanoTime() % 5 == 0;
	}
}
