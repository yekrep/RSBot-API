package org.powerbot.script.rt6;

import org.powerbot.bot.rt6.client.RSObjectDef;

class ObjectDefinition {
	private final RSObjectDef def;

	ObjectDefinition(final RSObjectDef def) {
		this.def = def;
	}

	int getId() {
		return def != null ? def.getID() : -1;
	}

	String getName() {
		String name;
		if (def == null || (name = def.getName()) == null) {
			name = "";
		}
		return name;
	}

	String[] getActions() {
		String[] actions = new String[0];
		if (def != null && (actions = def.getActions()) == null) {
			actions = new String[0];
		}
		return actions;
	}
}