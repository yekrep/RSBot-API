package org.powerbot.script.xenon.wrappers;

import java.lang.ref.WeakReference;

import org.powerbot.client.RSObjectDef;

public class ObjectDefinition {
	private final WeakReference<RSObjectDef> definition;

	public ObjectDefinition(final RSObjectDef definition) {
		this.definition = new WeakReference<>(definition);
	}

	public int getId() {
		final RSObjectDef def = this.definition.get();
		return def != null ? def.getID() : -1;
	}

	public String getName() {
		final RSObjectDef def = this.definition.get();
		return def != null ? def.getName() : null;
	}

	public String[] getActions() {
		final RSObjectDef def = this.definition.get();
		return def != null ? def.getActions() : null;
	}
}