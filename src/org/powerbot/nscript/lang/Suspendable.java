package org.powerbot.nscript.lang;

public interface Suspendable {
	public boolean isSuspended();

	public void suspend();

	public void resume();
}
