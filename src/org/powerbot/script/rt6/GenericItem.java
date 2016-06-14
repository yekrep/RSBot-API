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
		return CacheItemConfig.load(id()).name;
	}

	public boolean members() {
		return CacheItemConfig.load(id()).members;
	}

	public boolean stackable() {
		return CacheItemConfig.load(id()).stackable;
	}

	public boolean noted() {
		return CacheItemConfig.load(id()).noted;
	}

	public boolean tradeable() {
		return CacheItemConfig.load(id()).tradeable;
	}

	public boolean specialAttack() {
		return CacheItemConfig.load(id()).specialAttack;
	}

	public int adrenaline() {
		return CacheItemConfig.load(id()).adrenaline;
	}

	public boolean cosmetic() {
		return CacheItemConfig.load(id()).cosmetic;
	}

	public int value() {
		return CacheItemConfig.load(id()).value;
	}

	public String[] groundActions() {
		return CacheItemConfig.load(id()).groundActions;
	}

	public String[] backpackActions() {
		return CacheItemConfig.load(id()).actions;
	}

	public String[] equippedActions() {
		return CacheItemConfig.load(id()).equippedActions;
	}

	public String[] bankActions() {
		return CacheItemConfig.load(id()).bankActions;
	}
}
