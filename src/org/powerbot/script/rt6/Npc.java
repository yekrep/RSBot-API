package org.powerbot.script.rt6;

import java.awt.Color;

import org.powerbot.bot.rt6.client.RSNPC;
import org.powerbot.bot.rt6.client.RSNPCDef;
import org.powerbot.script.Identifiable;

public class Npc extends Actor implements Identifiable {
	public static final Color TARGET_COLOR = new Color(255, 0, 255, 15);
	private final RSNPC npc;

	public Npc(final ClientContext ctx, final RSNPC npc) {
		super(ctx);
		this.npc = npc;
	}

	@Override
	protected RSNPC getAccessor() {
		return npc;
	}

	@Override
	public String name() {
		final RSNPCDef d = npc.getRSNPCDef();
		return d.isNull() ? "" : d.getName();
	}

	@Override
	public int combatLevel() {
		final RSNPCDef d = npc.getRSNPCDef();
		return d.isNull() ? -1 : d.getLevel();
	}

	@Override
	public int id() {
		final RSNPCDef d = npc.getRSNPCDef();
		return d.isNull() ? -1 : d.getID();
	}

	public String[] actions() {
		final RSNPCDef d = npc.getRSNPCDef();
		return d.isNull() ? new String[0] : d.getActions();
	}

	public int prayerIcon() {
		final int[] a1 = getOverheadArray1();
		final short[] a2 = getOverheadArray2();
		final int len = a1.length;
		if (len != a2.length) {
			return -1;
		}

		for (int i = 0; i < len; i++) {
			if (a1[i] == 440) {
				return a2[i];
			}
		}
		return -1;
	}

	private int[] getOverheadArray1() {
		final RSNPCDef d = npc.getRSNPCDef();
		final int[] arr1 = npc.getOverhead().getArray1(), arr2 = d.getOverheadArray1();
		return arr1 != null ? arr1 : arr2 != null ? arr2 : new int[0];
	}


	private short[] getOverheadArray2() {
		final RSNPCDef d = npc.getRSNPCDef();
		final short[] arr1 = npc.getOverhead().getArray2(), arr2 = d.getOverheadArray2();
		return arr1 != null ? arr1 : arr2 != null ? arr2 : new short[0];
	}

	@Override
	public boolean valid() {
		final RSNPC npc = getAccessor();
		return !npc.isNull() && ctx.npcs.select().contains(this);
	}

	@Override
	public String toString() {
		return Npc.class.getSimpleName() + "[id=" + id() + ",name=" + name() + ",level=" + combatLevel() + "]";
	}
}
