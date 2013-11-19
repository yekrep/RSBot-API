package org.powerbot.service.scripts;

import org.powerbot.script.Script;

/**
 * @author Paris
 */
public class ScriptBundle {
	public final ScriptDefinition definition;
	public final Class<? extends Script> script;

	public ScriptBundle(final ScriptDefinition definition, final Class<? extends Script> script) {
		this.definition = definition;
		this.script = script;
	}
}
