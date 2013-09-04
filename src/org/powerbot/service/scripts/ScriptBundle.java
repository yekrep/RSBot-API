package org.powerbot.service.scripts;

import org.powerbot.script.Script;

/**
 * @author Paris
 */
public class ScriptBundle {
	public final ScriptDefinition definitiion;
	public final Script script;

	public ScriptBundle(final ScriptDefinition definition, final Script script) {
		this.definitiion = definition;
		this.script = script;
	}
}
