package org.powerbot.script.framework;

public interface Suspendable {

	public boolean isSuspended();

	public void suspend();

	public void resume();
}
