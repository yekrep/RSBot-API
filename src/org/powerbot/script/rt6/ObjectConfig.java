package org.powerbot.script.rt6;

import org.powerbot.bot.rt6.client.RSObjectDef;

class ObjectConfig {
	private final RSObjectDef def;

	ObjectConfig(final RSObjectDef def) {
		this.def = def;
	}

	int getId() {
		return def != null ? def.getID() : -1;
	}

	String getName() {
		final String n = def != null ? def.getName() : "";
		return n != null ? n : "";
	}

	String[] getActions() {
		final String[] arr = def != null ? def.getActions() : new String[0];
		return arr != null ? arr : new String[0];
	}
}