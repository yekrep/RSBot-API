package org.powerbot.script.rt6;

import org.powerbot.script.Identifiable;
import org.powerbot.script.Nameable;

/**
 * GenericItem
 */
abstract class GenericItem extends Interactive implements Identifiable, Nameable {
	public GenericItem(final ClientContext ctx) {
		super(ctx);
	}

	@Override
	public String name() {
		return CacheItemConfig.load(ctx.bot().getCacheWorker(), id()).name;
	}

	public boolean members() {
		return CacheItemConfig.load(ctx.bot().getCacheWorker(), id()).members;
	}

	public boolean stackable() {
		return CacheItemConfig.load(ctx.bot().getCacheWorker(), id()).stackable;
	}

	public boolean noted() {
		return CacheItemConfig.load(ctx.bot().getCacheWorker(), id()).noted;
	}

	public boolean tradeable() {
		return CacheItemConfig.load(ctx.bot().getCacheWorker(), id()).tradeable;
	}

	public boolean specialAttack() {
		return CacheItemConfig.load(ctx.bot().getCacheWorker(), id()).specialAttack;
	}

	public int adrenaline() {
		return CacheItemConfig.load(ctx.bot().getCacheWorker(), id()).adrenaline;
	}

	public boolean cosmetic() {
		return CacheItemConfig.load(ctx.bot().getCacheWorker(), id()).cosmetic;
	}

	public int value() {
		return CacheItemConfig.load(ctx.bot().getCacheWorker(), id()).value;
	}

	public String[] groundActions() {
		return CacheItemConfig.load(ctx.bot().getCacheWorker(), id()).groundActions;
	}

	public String[] backpackActions() {
		return CacheItemConfig.load(ctx.bot().getCacheWorker(), id()).actions;
	}

	public String[] equippedActions() {
		return CacheItemConfig.load(ctx.bot().getCacheWorker(), id()).equippedActions;
	}

	public String[] bankActions() {
		return CacheItemConfig.load(ctx.bot().getCacheWorker(), id()).bankActions;
	}
}
