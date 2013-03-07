package org.powerbot.script.internal;

public interface ScriptListener {
	public void scriptStarted(ScriptContainer scriptContainer);

	public void scriptPaused(ScriptContainer scriptContainer);

	public void scriptResumed(ScriptContainer scriptContainer);

	public void scriptStopped(ScriptContainer scriptContainer);
}
