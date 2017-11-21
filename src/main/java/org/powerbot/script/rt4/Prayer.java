package org.powerbot.script.rt4;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.StringUtils;

/**
 * Prayer
 */
public class Prayer extends ClientAccessor {
	public Prayer(final ClientContext ctx) {
		super(ctx);
	}

	public int prayerPoints() {
		return StringUtils.parseInt(ctx.widgets.component(Constants.MOVEMENT_MAP, Constants.MOVEMENT_QUICK_PRAYER + 1).text());
	}

	@Deprecated
	public int points() {
		return prayerPoints();
	}

	public boolean prayersActive() {
		return ctx.varpbits.varpbit(Constants.PRAYER_SELECTION) > 0;
	}

	@Deprecated
	public boolean praying() {
		return prayersActive();
	}

	public boolean quickPrayer() {
		return ctx.varpbits.varpbit(Constants.PRAYER_QUICK_SELECTED, 0x1) == 1;
	}

	public boolean quickPrayer(final boolean on) {
		return quickPrayer() == on || (ctx.widgets.component(Constants.MOVEMENT_MAP, Constants.MOVEMENT_QUICK_PRAYER).click() && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return quickPrayer() == on;
			}
		}, 300, 6));
	}

	public boolean quickSelectionActive() {
		return ctx.widgets.widget(Constants.PRAYER_QUICK_SELECT).component(0).valid();
	}

	public boolean prayerActive(final Effect effect) {
		return ctx.varpbits.varpbit(Constants.PRAYER_SELECTION, effect.ordinal(), 0x1) == 1;
	}

	public boolean prayer(final Effect effect, final boolean active) {
		if (ctx.skills.realLevel(Constants.SKILLS_PRAYER) < effect.level()) {
			return false;
		}
		if (prayerActive(effect) == active) {
			return true;
		}
		if (ctx.game.tab(Game.Tab.PRAYER)) {
			return ctx.widgets.component(Constants.PRAYER_SELECT, effect.index()).interact(active ? "Activate" : "Deactivate");
		}
		return prayerActive(effect) == active;
	}

	public boolean quickSelection(final boolean quick) {
		if (quickSelectionActive() == quick) {
			return true;
		}
		if (quick) {
			if (!ctx.widgets.component(Constants.MOVEMENT_MAP, Constants.MOVEMENT_QUICK_PRAYER).interact("Setup")) {
				return false;
			}
		} else {
			if (!ctx.game.tab(Game.Tab.PRAYER) || !ctx.widgets.component(Constants.PRAYER_QUICK_SELECT, 6).interact("Done")) {
				return false;
			}
		}

		return Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return quickSelectionActive() == quick;
			}
		}, 150, 10);
	}

	public boolean prayerQuick(final Effect effect) {
		return ctx.varpbits.varpbit(Constants.PRAYER_QUICK_SELECTION, effect.ordinal(), 0x1) == 1;
	}

	public Effect[] activePrayers() {
		final Set<Effect> active = new LinkedHashSet<Effect>();
		for (final Effect effect : Effect.values()) {
			if (prayerActive(effect)) {
				active.add(effect);
			}
		}
		return active.toArray(new Effect[active.size()]);
	}

	public Effect[] quickPrayers() {
		final Set<Effect> quick = new LinkedHashSet<Effect>();
		for (final Effect effect : Effect.values()) {
			if (prayerQuick(effect)) {
				quick.add(effect);
			}
		}
		return quick.toArray(new Effect[quick.size()]);
	}


	public boolean quickPrayers(final Effect... effects) {
		if (!quickSelectionActive()) {
			if (!quickSelection(true)) {
				return false;
			}
		}
		if (quickSelectionActive() && ctx.game.tab(Game.Tab.PRAYER)) {
			for (final Effect effect : effects) {
				if (prayerQuick(effect)) {
					continue;
				}

				if (ctx.widgets.component(Constants.PRAYER_QUICK_SELECT, Constants.PRAYER_QUICK_SELECT_CONTAINER).component(effect.quickSelectIndex()).interact("Toggle")) {
					Condition.sleep();
				}
			}

			for (final Effect effect : quickPrayers()) {
				if (prayerQuick(effect) && !search(effects, effect)) {
					if (ctx.widgets.component(Constants.PRAYER_QUICK_SELECT, Constants.PRAYER_QUICK_SELECT_CONTAINER).component(effect.quickSelectIndex()).interact("Toggle")) {
						Condition.sleep();
					}
				}
			}
		}

		for (int i = 0; i < 3; i++) {
			if (!quickSelectionActive()) {
				break;
			}
			quickSelection(false);
		}
		return !quickSelectionActive();
	}

	private boolean search(final Effect[] effects, final Effect effect) {
		for (final Effect e : effects) {
			if (e.index() == effect.index()) {
				return true;
			}
		}
		return false;
	}


	public enum Effect {
		THICK_SKIN(4, 1),
		BURST_OF_STRENGTH(5, 4),
		CLARITY_OF_THOUGHT(6, 7),
		ROCK_SKIN(7, 10),
		SUPERHUMAN_STRENGTH(8, 13),
		IMPROVED_REFLEXES(9, 16),
		RAPID_RESTORE(10, 19),
		RAPID_HEAL(11, 22),
		PROTECT_ITEM(12, 25),
		STEEL_SKIN(13, 28),
		ULTIMATE_STRENGTH(14, 31),
		INCREDIBLE_REFLEXES(15, 34),
		PROTECT_FROM_MAGIC(16, 37),
		PROTECT_FROM_MISSILES(17, 40),
		PROTECT_FROM_MELEE(18, 43),
		RETRIBUTION(19, 46),
		REDEMPTION(20, 49),
		SMITE(21, 52),
		SHARP_EYE(22, 8),
		MYSTIC_WILL(23, 9),
		HAWK_EYE(24, 26),
		MYSTIC_LORE(25, 27),
		EAGLE_EYE(26, 44),
		MYSTIC_MIGHT(27, 45),
		RIGOUR(30, 74),
		CHIVALRY(28, 60),
		PIETY(29, 70),
		AUGURY(31, 77),
		PRESERVE(32, 55);
		private final int index;
		private final int level;

		Effect(final int index, final int level) {
			this.index = index;
			this.level = level;
		}

		public int index() {
			return index;
		}

		public int level() {
			return level;
		}

		public int quickSelectIndex() {
			return ordinal();
		}
	}
}
