package org.powerbot.script.rt6;

import java.awt.Color;

import org.powerbot.bot.rt6.client.NpcConfig;
import org.powerbot.script.Actionable;
import org.powerbot.script.Identifiable;
import org.powerbot.script.StringUtils;

/**
 * Npc
 */
public class Npc extends Actor implements Identifiable, Actionable {
	public static final Color TARGET_COLOR = new Color(255, 0, 255, 15);
	private static final int[] lookup;
	private CacheNpcConfig cacheNpcConfig = null;
	private CacheVarbitConfig cacheVarbitConfig = null;

	static {
		lookup = new int[32];
		int i = 2;
		for (int j = 0; j < 32; j++) {
			lookup[j] = i - 1;
			i += i;
		}
	}

	private final org.powerbot.bot.rt6.client.Npc npc;

	public Npc(final ClientContext ctx, final org.powerbot.bot.rt6.client.Npc npc) {
		super(ctx);
		this.npc = npc;
	}

	@Override
	protected org.powerbot.bot.rt6.client.Npc getAccessor() {
		return npc;
	}

	@Override
	public String name() {
		final NpcConfig d = npc.getConfig();
		return d.isNull() ? "" : StringUtils.stripHtml(d.getName());
	}

	@Override
	public int combatLevel() {
		final NpcConfig d = npc.getConfig();
		return d.isNull() ? -1 : d.getCombatLevel();
	}

	@Override
	public int id() {
		final NpcConfig d = npc.getConfig();
		if (d.isNull()) {
			return -1;
		}
		final CacheNpcConfig cacheConfig = cacheNpcConfig == null ? (cacheNpcConfig = CacheNpcConfig.load(ctx.bot().getCacheWorker(), d.getId())) : cacheNpcConfig;
		if (!cacheConfig.valid()) {
			return d.getId();
		}
		final int varbit = cacheConfig.scriptId, varp = cacheConfig.configId;
		int index = -1;
		if (varbit != -1) {
			final CacheVarbitConfig varbitConfig = cacheVarbitConfig == null ? (cacheVarbitConfig = CacheVarbitConfig.load(ctx.bot().getCacheWorker(), varbit)) : cacheVarbitConfig;
			if (varbitConfig != null) {
				final int mask = lookup[varbitConfig.upperBitIndex - varbitConfig.lowerBitIndex];
				index = ctx.varpbits.varpbit(varbitConfig.configId, varbitConfig.lowerBitIndex, mask);
			}
		} else if (varp != -1) {
			index = varp;
		}
		if (index >= 0) {
			final int[] children = cacheConfig.childrenIds;
			if (children.length > 0) {
				if (index >= children.length) {
					//set default child index
					index = children.length - 1;
				}
				final int child;
				if ((child = children[index]) != -1) {
					return child;
				}
			}
		}
		return d.getId();
	}

	@Override
	public String[] actions() {
		final NpcConfig d = npc.getConfig();
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
		final NpcConfig d = npc.getConfig();
		final int[] arr1 = npc.getOverhead().getArray1(), arr2 = d.getOverheadArray1();
		return arr1 != null ? arr1 : arr2 != null ? arr2 : new int[0];
	}


	private short[] getOverheadArray2() {
		final NpcConfig d = npc.getConfig();
		final short[] arr1 = npc.getOverhead().getArray2(), arr2 = d.getOverheadArray2();
		return arr1 != null ? arr1 : arr2 != null ? arr2 : new short[0];
	}

	@Override
	public boolean valid() {
		final org.powerbot.bot.rt6.client.Npc npc = getAccessor();
		if (npc.isNull()) {
			return false;
		}
		for (final Npc n : ctx.npcs.select()) {
			if (n.getAccessor().equals(npc)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return Npc.class.getSimpleName() + "[id=" + id() + ",name=" + name() + ",level=" + combatLevel() + "]";
	}
}
