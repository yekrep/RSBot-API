package org.powerbot.service.scripts;

import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.script.Script;

/**
 * @author Paris
 */
public class ScriptBundle {
	public final ScriptDefinition definition;
	public final Class<? extends Script> script;
	public final AtomicReference<Script> instance;

	public ScriptBundle(final ScriptDefinition definition, final Class<? extends Script> script) {
		this.definition = definition;
		this.script = script;
		instance = new AtomicReference<Script>(null);
	}
}
