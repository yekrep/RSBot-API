package org.powerbot.script.wrappers;

import java.lang.ref.WeakReference;

import org.powerbot.client.RSObjectDef;

class ObjectDefinition {
	private final WeakReference<RSObjectDef> definition;

	ObjectDefinition(final RSObjectDef definition) {
		this.definition = new WeakReference<RSObjectDef>(definition);
	}

	int getId() {
		final RSObjectDef def = this.definition.get();
		return def != null ? def.getID() : -1;
	}

	String getName() {
		final RSObjectDef def = this.definition.get();
		String name = "";
		if (def != null && (name = def.getName()) == null) {
			name = "";
		}
		return name;
	}

	String[] getActions() {
		final RSObjectDef def = this.definition.get();
		String[] actions = new String[0];
		if (def != null && (actions = def.getActions()) == null) {
			actions = new String[0];
		}
		return actions;
	}
}