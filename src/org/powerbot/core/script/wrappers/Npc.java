package org.powerbot.core.script.wrappers;

import java.lang.ref.WeakReference;

import org.powerbot.game.client.RSCharacter;
import org.powerbot.game.client.RSNPC;
import org.powerbot.game.client.RSNPCDef;

public class Npc extends Character {
	private final WeakReference<RSNPC> npc;

	public Npc(final RSNPC npc) {
		this.npc = new WeakReference<>(npc);
	}

	@Override
	protected RSNPC getAccessor() {
		return npc.get();
	}

	@Override
	public String getName() {
		final RSNPC npc = getAccessor();
		final RSNPCDef def;
		return npc != null && (def = npc.getRSNPCDef()) != null ? def.getName() : null;
	}

	@Override
	public int getLevel() {
		final RSNPC npc = getAccessor();
		final RSNPCDef def;
		return npc != null && (def = npc.getRSNPCDef()) != null ? def.getLevel() : -1;
	}

	public int getId() {
		final RSNPC npc = getAccessor();
		final RSNPCDef def;
		return npc != null && (def = npc.getRSNPCDef()) != null ? def.getID() : -1;
	}

	public String[] getActions() {
		final RSNPC npc = getAccessor();
		final RSNPCDef def;
		return npc != null && (def = npc.getRSNPCDef()) != null ? def.getActions() : null;
	}

	public int getPrayerIcon() {
		final RSNPC npc = getAccessor();
		final RSNPCDef def;
		return npc != null && (def = npc.getRSNPCDef()) != null ? def.getPrayerIcon() : -1;
	}
}
