package org.powerbot.nscript.internal;

import org.powerbot.nscript.Script;

import java.util.concurrent.atomic.AtomicReference;

public class ScriptContainer {
	private AtomicReference<Script> script;

	public ScriptContainer() {
		this.script = new AtomicReference<>(null);
	}
}
