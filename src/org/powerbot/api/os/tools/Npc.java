package org.powerbot.api.os.tools;

import java.awt.Color;
import java.lang.ref.SoftReference;

import org.powerbot.api.ClientContext;
import org.powerbot.bot.os.client.Client;
import org.powerbot.bot.os.client.NpcConfig;

public class Npc extends Actor implements Identifiable {
	public static final Color TARGET_STROKE_COLOR = new Color(255, 0, 255, 15);
	private final SoftReference<org.powerbot.bot.os.client.Npc> npc;

	Npc(final ClientContext ctx, final org.powerbot.bot.os.client.Npc npc) {
		super(ctx);
		this.npc = new SoftReference<org.powerbot.bot.os.client.Npc>(npc);
	}

	@Override
	protected org.powerbot.bot.os.client.Actor getActor() {
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
		if (arr == null) {
			return new String[0];
		}
		final String[] arr_ = new String[arr.length];
		int c = 0;
		for (final String str : arr) {
			arr_[c++] = str != null ? str : "";
		}
		return arr_;
	}

	private NpcConfig getConfig() {
		final org.powerbot.bot.os.client.Npc npc = this.npc.get();
		return npc != null ? npc.getConfig() : null;
	}

	@Override
	public boolean isValid() {
		final Client client = ctx.client();
		final org.powerbot.bot.os.client.Npc npc = this.npc.get();
		if (client == null || npc == null) {
			return false;
		}
		final org.powerbot.bot.os.client.Npc[] arr = client.getNpcs();
		for (final org.powerbot.bot.os.client.Npc a : arr) {
			if (a == npc) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("%s[id=%d/name=%s/level=%d]",
				Npc.class.getName(), getId(), getName(), getCombatLevel());
	}
}
