package org.powerbot.script.util;

public interface Suspendable {

	public boolean isSuspended();

	public void suspend();

	public void resume();
}
