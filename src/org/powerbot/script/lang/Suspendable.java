package org.powerbot.script.lang;

public interface Suspendable {
	public boolean isSuspended();

	public void suspend();

	public void resume();
}
