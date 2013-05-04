package org.powerbot.script.xenon.wrappers;

import java.lang.ref.WeakReference;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSNPC;
import org.powerbot.game.client.RSNPCDef;
import org.powerbot.game.client.RSNPCNode;
import org.powerbot.script.internal.Nodes;

public class Npc extends Actor {
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

	@Override
	public boolean isValid() {
		final Client client = Bot.client();
		if (client == null) return false;
		final RSNPC npc = getAccessor();
		if (npc != null) {
			final int[] indices = client.getRSNPCIndexArray();
			final org.powerbot.game.client.HashTable npcTable = client.getRSNPCNC();
			for (final int index : indices) {
				Object node = Nodes.lookup(npcTable, index);
				if (node == null) continue;
				if (node instanceof RSNPCNode) node = ((RSNPCNode) node).getRSNPC();
				if (node instanceof RSNPC) if (node.equals(npc)) return true;
			}
		}
		return false;
	}
}
