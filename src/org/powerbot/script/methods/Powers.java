package org.powerbot.script.methods;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Powers extends MethodProvider {
	public static final int SETTING_PRAYER_POINTS = 3274;
	public static final int SETTING_PRAYER_BOOK = 3277;
	public static final int SETTING_PRAYERS = 3272;
	public static final int SETTING_CURSES = 3275;
	public static final int SETTING_PRAYERS_QUICK = 1770;
	public static final int SETTING_PRAYERS_SELECTION = 1769;
	public static final int SETTING_CURSES_QUICK = 1768;
	public static final int BOOK_PRAYERS = 0;
	public static final int BOOK_CURSES = 1;
	public static final int WIDGET_PRAYER = 1458;
	public static final int COMPONENT_PRAYER_CONTAINER = 24;
	public static final int COMPONENT_PRAYER_SELECT_CONTAINER = 25;
	public static final int COMPONENT_PRAYER_SELECT_CONFIRM = 4;

	public Powers(MethodContext factory) {
		super(factory);
	}

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
		CHIVALRY(17, 27, 60),
		RAPID_RENEWAL(18, 18, 65),
		PIETY(19, 19, 70),
		RIGOUR(20, 21, 74),
		AUGURY(21, 20, 77);

		private final int id;
		private final int index;
		private final int level;

		Prayer(int id, int index, int level) {
			this.id = id;
			this.index = index;
			this.level = level;
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public int getIndex() {
			return index;
		}

		@Override
		public int getLevel() {
			return level;
		}
	}

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
		LEECH_STRENGTH(20, 14, 82),
		LEECH_ENERGY(21, 15, 84),
		LEECH_ADRENALINE(22, 16, 86),
		WRATH(23, 17, 89),
		SOUL_SPLIT(24, 18, 92),
		TURMOIL(25, 19, 95),
		ANGUISH(26, 22, 95),
		TORMENT(27, 23, 95);

		private final int id;
		private final int index;
		private final int level;

		Curse(int id, int index, int level) {
			this.id = id;
			this.index = index;
			this.level = level;
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public int getIndex() {
			return index;
		}

		@Override
		public int getLevel() {
			return level;
		}
	}

	public interface Effect {
		public int getId();

		public int getIndex();

		public int getLevel();
	}

	public int getPrayerPoints() {
		return (ctx.settings.get(SETTING_PRAYER_POINTS) & 0x7fff) / 10;
	}

	public int getPrayerBook() {
		return ctx.settings.get(SETTING_PRAYER_BOOK) % 2;
	}

	public boolean isQuickSelection() {
		return ctx.settings.get(SETTING_PRAYERS_SELECTION) == 0x1;
	}

	public boolean isPrayerActive(Effect effect) {
		int setting;
		if (effect instanceof Prayer) {
			setting = SETTING_PRAYERS;
		} else if (effect instanceof Curse) {
			setting = SETTING_CURSES;
		} else {
			setting = -1;
		}
		return ((ctx.settings.get(setting) >>> effect.getIndex()) & 0x1) == 1;
	}

	public boolean isPrayerQuick(Effect effect) {
		int setting;
		if (effect instanceof Prayer) {
			setting = SETTING_PRAYERS_QUICK;
		} else if (effect instanceof Curse) {
			setting = SETTING_CURSES_QUICK;
		} else {
			setting = -1;
		}
		return ((ctx.settings.get(setting) >>> effect.getIndex()) & 0x1) == 1;
	}

	public Effect[] getActivePrayers() {
		int book = getPrayerBook();
		Effect[] effects;
		switch (book) {
		case BOOK_PRAYERS:
			effects = Prayer.values();
			break;
		case BOOK_CURSES:
			effects = Curse.values();
			break;
		default:
			effects = new Effect[0];
			break;
		}

		Set<Effect> active = new LinkedHashSet<>();
		for (Effect effect : effects) {
			if (isPrayerActive(effect)) {
				active.add(effect);
			}
		}
		return active.toArray(new Effect[active.size()]);
	}

	public Effect[] getQuickPrayers() {
		int book = getPrayerBook();
		Effect[] effects;
		switch (book) {
		case BOOK_PRAYERS:
			effects = Prayer.values();
			break;
		case BOOK_CURSES:
			effects = Curse.values();
			break;
		default:
			effects = new Effect[0];
			break;
		}

		Set<Effect> quick = new LinkedHashSet<>();
		for (Effect effect : effects) {
			if (isPrayerQuick(effect)) {
				quick.add(effect);
			}
		}
		return quick.toArray(new Effect[quick.size()]);
	}

	public boolean setPrayerActive(Effect effect, boolean active) {
		if (ctx.skills.getLevel(Skills.PRAYER) < effect.getLevel()) {
			return false;
		}
		if (isPrayerActive(effect) == active) {
			return true;
		}
		if (ctx.hud.view(Hud.Window.PRAYER_ABILITIES)) {
			return ctx.widgets.get(WIDGET_PRAYER, COMPONENT_PRAYER_CONTAINER).getChild(effect.getId()).interact(active ? "Activate" : "Deactivate");
		}
		return isPrayerActive(effect) == active;
	}

	public boolean setQuickPrayers(Effect... effects) {
		if (!isQuickSelection()) {
			if (ctx.widgets.get(CombatBar.WIDGET, CombatBar.COMPONENT_BUTTON_PRAYER).interact("Select quick")) {
				for (int i = 0; i < 20; i++) {
					if (isQuickSelection()) {
						break;
					}
					sleep(100, 200);
				}
			}
		}
		if (isQuickSelection() && ctx.hud.view(Hud.Window.PRAYER_ABILITIES)) {
			for (Effect effect : effects) {
				if (isPrayerQuick(effect)) {
					continue;
				}
				if (ctx.widgets.get(WIDGET_PRAYER, COMPONENT_PRAYER_SELECT_CONTAINER).getChild(effect.getId()).interact("Select")) {
					sleep(800, 1200);
				}
			}

			Effect[] quicks = getQuickPrayers();
			Arrays.sort(quicks);
			for (Effect effect : effects) {
				if (Arrays.binarySearch(quicks, effect) < 0) {
					if (ctx.widgets.get(WIDGET_PRAYER, COMPONENT_PRAYER_SELECT_CONTAINER).getChild(effect.getId()).interact("Deselect")) {
						sleep(800, 1200);
					}
				}
			}
		}

		for (int i = 0; i < 3; i++) {
			if (!isQuickSelection()) {
				break;
			}

			if (i < 1 && ctx.hud.isVisible(Hud.Window.PRAYER_ABILITIES)) {
				if (!ctx.widgets.get(WIDGET_PRAYER, COMPONENT_PRAYER_SELECT_CONFIRM).interact("Confirm")) {
					continue;
				}
			} else {
				if (!ctx.widgets.get(CombatBar.WIDGET, CombatBar.COMPONENT_BUTTON_PRAYER).interact("Finish")) {
					continue;
				}
			}
			for (int i2 = 0; i2 < 20; i2++) {
				if (!isQuickSelection()) {
					break;
				}
				sleep(100, 200);
			}
		}

		Effect[] quicks = getQuickPrayers();
		if (quicks.length != effects.length) {
			return false;
		}
		Arrays.sort(quicks);
		for (Effect effect : effects) {
			if (Arrays.binarySearch(quicks, effect) < 0) {
				return false;
			}
		}
		return !isQuickSelection();
	}
}
