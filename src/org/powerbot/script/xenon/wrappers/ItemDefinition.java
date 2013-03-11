package org.powerbot.script.xenon.wrappers;

import java.lang.ref.WeakReference;

import org.powerbot.game.client.RSItemDef;

public class ItemDefinition {//TODO complete
	private final WeakReference<RSItemDef> def;

	ItemDefinition(final RSItemDef def) {
		this.def = new WeakReference<>(def);
	}
}
