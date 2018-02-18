package org.powerbot.script.rt6;

import java.util.LinkedHashSet;
import java.util.Set;

import org.powerbot.script.Condition;

/**
 * Powers
 * API pertaining to in-game powers.
 */
public class Powers extends ClientAccessor {
	public Powers(final ClientContext factory) {
		super(factory);
	}

	/**
	 * Returns the current prayer points.
	 *
	 * @return the current prayer points
	 */
	public int prayerPoints() {
		return (ctx.varpbits.varpbit(Constants.POWERS_PRAYER_POINTS) & 0x7fff) / 10;
	}

	/**
	 * Returns the current prayer book.
	 *
	 * @return the current prayer book
	 */
	public int prayerBook() {
		return ctx.varpbits.varpbit(Constants.POWERS_PRAYER_BOOK) % 2 != 0 ? Constants.POWERS_BOOK_CURSES : Constants.POWERS_BOOK_PRAYERS;
	}

	/**
	 * Determines if quick prayers selection is active.
	 *
	 * @return {@code true} if quick prayers selection is active; otherwise {@code false}
	 */
	public boolean quickSelectionActive() {
		return ctx.varpbits.varpbit(Constants.POWERS_PRAYERS_SELECTION) == 0x1;
	}

	/**
	 * Determines if quick prayers are active.
	 *
	 * @return {@code true} if quick prayers are active; otherwise {@code false}
	 */
	public boolean quickPrayersActive() {
		return ctx.varpbits.varpbit(Constants.POWERS_PRAYERS_SELECTION) == 0x2;
	}

	/**
	 * Determines if a prayer is active.
	 *
	 * @param effect the {@link Effect} to check
	 * @return {@code true} if prayer is active; otherwise {@code false}
	 */
	public boolean prayerActive(final Effect effect) {
		int setting;
		if (effect instanceof Prayer) {
			setting = Constants.POWERS_PRAYERS;
		} else if (effect instanceof Curse) {
			setting = Constants.POWERS_CURSES;
		} else {
			return false;
		}
		int index = effect.index();
		if (index >= 32) {
			index -= 32;
			setting += 2584;
		}
		return ((ctx.varpbits.varpbit(setting) >>> index) & 0x1) == 1;
	}

	/**
	 * Determines if a prayer is set as a quick prayer.
	 *
	 * @param effect the {@link Effect} to check
	 * @return {@code true} if set as a quick prayer; otherwise {@code false}
	 */
	public boolean prayerQuick(final Effect effect) {
		final int setting;
		if (effect instanceof Prayer) {
			setting = Constants.POWERS_PRAYERS_QUICK;
		} else if (effect instanceof Curse) {
			setting = Constants.POWERS_CURSES_QUICK;
		} else {
			setting = -1;
		}
		return ((ctx.varpbits.varpbit(setting) >>> effect.index()) & 0x1) == 1;
	}

	/**
	 * Returns the prayers currently active.
	 *
	 * @return the {@link Effect}s currently active
	 */
	public Effect[] activePrayers() {
		final int book = prayerBook();
		final Effect[] effects;
		switch (book) {
		case Constants.POWERS_BOOK_PRAYERS:
			effects = Prayer.values();
			break;
		case Constants.POWERS_BOOK_CURSES:
			effects = Curse.values();
			break;
		default:
			effects = new Effect[0];
			break;
		}

		final Set<Effect> active = new LinkedHashSet<Effect>();
		for (final Effect effect : effects) {
			if (prayerActive(effect)) {
				active.add(effect);
			}
		}
		return active.toArray(new Effect[active.size()]);
	}

	/**
	 * Returns the {@link Effect}s set as quick prayers.
	 *
	 * @return the {@link Effect}s set as quick prayers
	 */
	public Effect[] quickPrayers() {
		final int book = prayerBook();
		final Effect[] effects;
		switch (book) {
		case Constants.POWERS_BOOK_PRAYERS:
			effects = Prayer.values();
			break;
		case Constants.POWERS_BOOK_CURSES:
			effects = Curse.values();
			break;
		default:
			effects = new Effect[0];
			break;
		}

		final Set<Effect> quick = new LinkedHashSet<Effect>();
		for (final Effect effect : effects) {
			if (prayerQuick(effect)) {
				quick.add(effect);
			}
		}
		return quick.toArray(new Effect[quick.size()]);
	}

	/**
	 * Toggles quick prayer selection mode.
	 *
	 * @param quick {@code true} if desired prayer selection, {@code false} if desired not prayer selection
	 * @return {@code true} if toggled selection mode; otherwise {@code false}
	 */
	public boolean quickSelection(final boolean quick) {
		if (quickSelectionActive() == quick) {
			return true;
		}
		if (ctx.hud.legacy() ? ctx.hud.open(Hud.Window.PRAYER_ABILITIES) : ctx.hud.opened(Hud.Window.PRAYER_ABILITIES)) {
			if (quick) {
				if (!ctx.widgets.component(Constants.POWERS_PRAYER, Constants.POWERS_QUICK_SELECTION).interact("Select")) {
					return false;
				}
			} else {
				if (!ctx.widgets.component(Constants.POWERS_PRAYER, Constants.POWERS_PRAYER_SELECT_CONFIRM).interact("Confirm")) {
					return false;
				}
			}
		} else {
			if (!ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_PRAYER_BUTTON).interact(quick ? "Select quick" : "Finish")) {
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

	/**
	 * Toggles quick prayers.
	 *
	 * @param active {@code true} if desired active; {@code false} if desired not active
	 * @return {@code true} if quick prayers are toggled; otherwise {@code false}
	 */
	public boolean quickPrayers(final boolean active) {
		return quickPrayersActive() == active || (ctx.hud.legacy() ? ctx.widgets.component(1505, 1) : ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_PRAYER_BUTTON)).interact(active ? "on" : "off") && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return quickPrayersActive() == active;
			}
		}, 150, 10);
	}

	/**
	 * Toggles an {@link Effect}.
	 *
	 * @param effect the {@link Effect} to toggle
	 * @param active {@code true} if desired active; {@code false} if desired not active
	 * @return {@code true} if {@link Effect} is successfully toggled; otherwise {@code false}
	 */
	public boolean prayer(final Effect effect, final boolean active) {
		if (ctx.skills.level(Constants.SKILLS_PRAYER) < effect.level()) {
			return false;
		}
		if (prayerActive(effect) == active) {
			return true;
		}
		if (ctx.hud.open(Hud.Window.PRAYER_ABILITIES)) {
			return ctx.widgets.component(Constants.POWERS_PRAYER, Constants.POWERS_PRAYER_CONTAINER).component(effect.id()).interact(active ? "Activate" : "Deactivate");
		}
		return prayerActive(effect) == active;
	}

	/**
	 * Attempts to set quick prayers to the given {@link Effect}s.
	 *
	 * @param effects the {@link Effect}s
	 * @return {@code true} if selected; otherwise {@code false}.
	 */
	public boolean quickPrayers(final Effect... effects) {
		if (!quickSelectionActive()) {
			quickSelection(true);
		}
		if (quickSelectionActive() && ctx.hud.open(Hud.Window.PRAYER_ABILITIES)) {
			for (final Effect effect : effects) {
				if (prayerQuick(effect)) {
					continue;
				}
				if (ctx.widgets.component(Constants.POWERS_PRAYER, Constants.POWERS_PRAYER_SELECT_CONTAINER).component(effect.id()).interact("Select")) {
					Condition.sleep();
				}
			}

			for (final Effect effect : quickPrayers()) {
				if (prayerQuick(effect) && !search(effects, effect)) {
					if (ctx.widgets.component(Constants.POWERS_PRAYER, Constants.POWERS_PRAYER_SELECT_CONTAINER).component(effect.id()).interact("Deselect")) {
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
			if (e.id() == effect.id()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Effects available in the normal prayer book.
	 */
	public enum Prayer implements Effect {
		THICK_SKIN(0, 0, 1),
		BURST_OF_STRENGTH(1, 1, 4),
		CLARITY_OF_THOUGHT(2, 2, 7),
		SHARP_EYE(3, 12, 8),
		UNSTOPPABLE_FORCE(4, 14, 8),
		MYSTIC_WILL(5, 13, 9),
		CHARGE(6, 15, 9),
		ROCK_SKIN(0, 0, 10),
		SUPERHUMAN_STRENGTH(1, 1, 13),
		IMPROVED_REFLEXES(2, 2, 16),
		RAPID_RESTORE(7, 3, 19),
		RAPID_HEAL(8, 4, 22),
		PROTECT_ITEM_REGULAR(9, 5, 25),
		HAWK_EYE(3, 12, 26),
		UNRELENTING_FORCE(4, 14, 26),
		MYSTIC_LORE(5, 13, 27),
		SUPER_CHARGE(6, 15, 27),
		STEEL_SKIN(0, 0, 28),
		ULTIMATE_STRENGTH(1, 1, 31),
		INCREDIBLE_REFLEXES(2, 2, 34),
		PROTECT_FROM_SUMMONING(10, 16, 35),
		PROTECT_FROM_MAGIC(11, 6, 37),
		PROTECT_FROM_MISSILES(12, 7, 40),
		PROTECT_FROM_MELEE(13, 8, 43),
		EAGLE_EYE(3, 12, 44),
		OVERPOWERING_FORCE(4, 14, 44),
		MYSTIC_MIGHT(5, 13, 45),
		OVERCHARGE(6, 15, 45),
		RETRIBUTION(14, 9, 46),
		REDEMPTION(15, 10, 49),
		SMITE(16, 11, 52),
		CHIVALRY(17, 17, 60),
		RAPID_RENEWAL(18, 19, 65),
		PIETY(19, 18, 70),
		RIGOUR(20, 21, 74),
		AUGURY(21, 20, 77);

		private final int id;
		private final int index;
		private final int level;

		Prayer(final int id, final int index, final int level) {
			this.id = id;
			this.index = index;
			this.level = level;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return id;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int index() {
			return index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int level() {
			return level;
		}
	}

	/**
	 * Effects available in the curses book.
	 */
	public enum Curse implements Effect {
		PROTECT_ITEM_CURSE(0, 0, 50),
		SAP_WARRIOR(1, 1, 50),
		SAP_RANGER(2, 2, 52),
		SAP_RANGE_STRENGTH(3, 25, 53),
		SAP_MAGE(4, 3, 54),
		SAP_MAGIC_STRENGTH(5, 24, 55),
		SAP_SPIRIT(6, 4, 56),
		SAP_DEFENCE(7, 27, 57),
		SAP_STRENGTH(8, 26, 58),
		BERSERKER(9, 5, 59),
		DEFLECT_SUMMONING(10, 6, 62),
		DEFLECT_MAGIC(11, 7, 65),
		DEFLECT_MISSILE(12, 8, 68),
		DEFLECT_MELEE(13, 9, 71),
		LEECH_ATTACK(14, 10, 74),
		LEECH_RANGED(15, 11, 76),
		LEECH_RANGE_STRENGTH(16, 20, 77),
		LEECH_MAGIC(17, 12, 78),
		LEECH_MAGIC_STRENGTH(18, 21, 79),
		LEECH_DEFENCE(19, 13, 80),
		LIGHT_FORM(20, 29, 80),
		DARK_FORM(21, 30, 80),
		LEECH_STRENGTH(22, 14, 82),
		LEECH_ENERGY(23, 15, 84),
		LEECH_ADRENALINE(24, 16, 86),
		CHRONICLE_ABSORPTION(25, 33, 87),
		SOUL_LINK(26, 31, 88),
		WRATH(27, 17, 89),
		TEAMWORK_PROTECTION(28, 32, 89),
		SUPERHEAT_FORM(29, 34, 91),
		SOUL_SPLIT(30, 18, 92),
		FORTITUDE(31, 28, 94),
		TURMOIL(32, 19, 95),
		ANGUISH(33, 22, 95),
		TORMENT(34, 23, 95);

		private final int id;
		private final int index;
		private final int level;

		Curse(final int id, final int index, final int level) {
			this.id = id;
			this.index = index;
			this.level = level;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int id() {
			return id;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int index() {
			return index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int level() {
			return level;
		}
	}

	/**
	 * A Prayer-specific power
	 */
	public interface Effect {
		/**
		 * Returns the id of the prayer.
		 *
		 * @return the id of the prayer
		 */
		int id();

		/**
		 * Returns the index of the prayer.
		 *
		 * @return the index of the prayer
		 */
		int index();

		/**
		 * Returns the level required to use the prayer.
		 *
		 * @return the level required to use the prayer
		 */
		int level();
	}
}
