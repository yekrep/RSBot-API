package org.powerbot.script;

public interface Suspendable {

	public boolean isSuspended();

	public void suspend();

	public void resume();
}
