package org.powerbot.script;

public interface Suspendable {
	boolean isSuspended();

	void suspend();

	void resume();
}
