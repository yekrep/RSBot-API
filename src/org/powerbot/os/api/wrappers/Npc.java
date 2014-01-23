package org.powerbot.os.api.wrappers;

import org.powerbot.os.api.MethodContext;
import org.powerbot.os.client.Client;
import org.powerbot.os.client.NpcConfig;

import java.lang.ref.SoftReference;

public class Npc extends Actor implements Identifiable {
	private final SoftReference<org.powerbot.os.client.Npc> npc;

	public Npc(final MethodContext ctx, final org.powerbot.os.client.Npc npc) {
		super(ctx);
		this.npc = new SoftReference<org.powerbot.os.client.Npc>(npc);
	}

	@Override
	protected org.powerbot.os.client.Actor getActor() {
		return npc.get();
	}

	@Override
	public String getName() {
		final NpcConfig config = getConfig();
		final String str = config != null ? config.getName() : "";
		return str != null ? str : "";
	}

	@Override
	public int getCombatLevel() {
		final NpcConfig config = getConfig();
		return config != null ? config.getLevel() : -1;
	}

	@Override
	public int getId() {
		final NpcConfig config = getConfig();
		return config != null ? config.getId() : -1;
	}

	public String[] getActions() {
		final NpcConfig config = getConfig();
		final String[] arr = config != null ? config.getActions() : new String[0];
		if (arr == null) return new String[0];
		final String[] arr_ = new String[arr.length];
		int c = 0;
		for (final String str : arr) {
			arr_[c++] = str != null ? str : "";
		}
		return arr_;
	}

	private NpcConfig getConfig() {
		final org.powerbot.os.client.Npc npc = this.npc.get();
		return npc != null ? npc.getConfig() : null;
	}

	@Override
	public boolean isValid() {
		final Client client = ctx.getClient();
		final org.powerbot.os.client.Npc npc = this.npc.get();
		if (client == null || npc == null) return false;
		final org.powerbot.os.client.Npc[] arr = client.getNpcs();
		for (final org.powerbot.os.client.Npc a : arr) {
			if (a == npc) return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("%s[id=%d/name=%s/level=%d]",
				Npc.class.getName(), getId(), getName(), getCombatLevel());
	}
}
