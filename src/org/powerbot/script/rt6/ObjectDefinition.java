package org.powerbot.script.rt6;

import org.powerbot.bot.rt6.client.RSObjectDef;

class ObjectDefinition {
	private final RSObjectDef def;

	ObjectDefinition(final RSObjectDef def) {
		this.def = def;
	}

	int getId() {
		return def.getID();
	}

	String getName() {
		final String n = def.getName();
		return n != null ? n : "";
	}

	String[] getActions() {
		final String[] arr = def.getActions();
		return arr != null ? arr : new String[0];
	}
}