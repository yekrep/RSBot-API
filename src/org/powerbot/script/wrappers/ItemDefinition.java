package org.powerbot.script.wrappers;

import org.powerbot.client.RSItemDef;

import java.lang.ref.WeakReference;

public class ItemDefinition {
	private final WeakReference<RSItemDef> def;

	ItemDefinition(final RSItemDef def) {
		this.def = new WeakReference<>(def);
	}

	public int getId() {
		final RSItemDef def = this.def.get();
		return def != null ? def.getID() : -1;
	}

	public String getName() {
		final RSItemDef def = this.def.get();
		return def != null ? def.getName() : null;
	}

	public boolean isMembers() {
		final RSItemDef def = this.def.get();
		return def != null && def.isMembersObject();
	}

	public String[] getActions() {
		final RSItemDef def = this.def.get();
		return def != null ? def.getActions() : null;
	}

	public String[] getGroundActions() {
		final RSItemDef def = this.def.get();
		return def != null ? def.getGroundActions() : null;
	}
}
