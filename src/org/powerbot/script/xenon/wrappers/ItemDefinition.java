package org.powerbot.script.xenon.wrappers;

import java.lang.ref.WeakReference;

import org.powerbot.game.client.RSItemDef;

public class ItemDefinition implements Validatable {
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

	@Override
	public boolean isValid() {
		return def.get() != null;
	}
}
