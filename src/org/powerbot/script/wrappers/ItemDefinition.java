package org.powerbot.script.wrappers;

import org.powerbot.client.RSItemDef;
import org.powerbot.script.lang.Identifiable;
import org.powerbot.script.lang.Nameable;

import java.lang.ref.WeakReference;

public class ItemDefinition implements Identifiable, Nameable {
	private final WeakReference<RSItemDef> def;

	ItemDefinition(final RSItemDef def) {
		this.def = new WeakReference<>(def);
	}

	@Override
	public int getId() {
		final RSItemDef def = this.def.get();
		return def != null ? def.getID() : -1;
	}

	@Override
	public String getName() {
		final RSItemDef def = this.def.get();
		return def != null ? def.getName() : "";
	}

	public boolean isMembers() {
		final RSItemDef def = this.def.get();
		return def != null && def.isMembersObject();
	}

	public String[] getActions() {
		final RSItemDef def = this.def.get();
		return def != null ? def.getActions() : new String[0];
	}

	public String[] getGroundActions() {
		final RSItemDef def = this.def.get();
		return def != null ? def.getGroundActions() : new String[0];
	}
}
