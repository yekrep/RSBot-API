package org.powerbot.script.methods;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.powerbot.script.util.Delay;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Widget;

public class Prayer extends MethodProvider {
	public static final int WIDGET = 271;
	public static final int WIDGET_ORB = 749;
	public static final int PRAYER_BOOK_CURSES = 0x17;
	public static final int PRAYER_BOOK_NORMAL = 0x16;

	public Prayer(MethodContext factory) {
		super(factory);
	}

	public int getPoints() {
		return (ctx.settings.get(3274) & 0x7fff) / 10;
	}

	public int getPrayerBook() {
		return ctx.settings.get(3277) % 2 != 0 ? PRAYER_BOOK_CURSES : PRAYER_BOOK_NORMAL;
	}

	public boolean isQuickOn() {
		return ctx.settings.get(1769) == 0x2;
	}

	public boolean isEffectActive(Effect effect) {
		int book = getPrayerBook();
		int setting;
		switch (book) {
		case PRAYER_BOOK_CURSES:
			setting = 3275;
			break;
		case PRAYER_BOOK_NORMAL:
			setting = 3272;
			break;
		default:
			setting = -1;
			break;
		}
		return (ctx.settings.get(setting >>> effect.getShift()) & 0x1) == 1;
	}

	public boolean isEffectQuick(Effect effect) {
		int book = getPrayerBook();
		int setting;
		switch (book) {
		case PRAYER_BOOK_CURSES:
			setting = 1768;
			break;
		case PRAYER_BOOK_NORMAL:
			setting = 1770;
			break;
		default:
			setting = -1;
			break;
		}
		return (ctx.settings.get(setting >>> effect.getShift()) & 0x1) == 1;
	}

	public Effect[] getActive() {
		final Set<Effect> active = new LinkedHashSet<>();
		for (final Effect p : getPrayerBook() == PRAYER_BOOK_CURSES ? Curses.values() : Normal.values()) {
			if (isEffectActive(p)) {
				active.add(p);
			}
		}
		return active.toArray(new Effect[active.size()]);
	}

	public Effect[] getQuickEffects() {
		final Set<Effect> quick = new LinkedHashSet<>();
		for (final Effect p : getPrayerBook() == PRAYER_BOOK_CURSES ? Curses.values() : Normal.values()) {
			if (isEffectQuick(p)) {
				quick.add(p);
			}
		}
		return quick.toArray(new Effect[quick.size()]);
	}

	public boolean setQuick(final boolean activate) {
		if (isQuickOn() == activate) {
			return true;
		}
		final Component c = ctx.widgets.get(WIDGET_ORB, 2);
		return c != null && c.interact("Turn");
	}

	public boolean setQuickEffects(final Effect... prayers) {
		final Widget prayer = ctx.widgets.get(WIDGET);
		final Component orb = ctx.widgets.get(WIDGET_ORB, 2);
		if (prayer == null || orb == null) {
			return false;
		}
		for (final Effect e : prayers) {
			if (e.getBook() != (getPrayerBook() == PRAYER_BOOK_CURSES ? PRAYER_BOOK_CURSES : PRAYER_BOOK_NORMAL) ||
					e.getRequiredLevel() > ctx.skills.getRealLevel(Skills.PRAYER)) {
				return false;
			}
		}

		if (!orb.interact("Select quick")) {
			return false;
		}
		final Timer timer = new Timer(1000);
		while (timer.isRunning() && ctx.settings.get(1769) != 0x1) {
			Delay.sleep(15);
		}
		Delay.sleep(100);

		final Component pane = prayer.getComponent(11);
		if (pane == null) {
			return false;
		}
		for (final Effect e : prayers) {
			if (isEffectQuick(e)) {
				continue;
			}
			final Component p = pane.getChild(e.getId());
			if (p == null) {
				return false;
			}
			if (p.interact("Select")) {
				final Timer t = new Timer(500);
				while (t.isRunning() && !isEffectQuick(e)) {
					Delay.sleep(15);
				}
			} else {
				final Component complete = prayer.getComponent(12);
				if (complete != null) {
					complete.interact("Confirm");
				}
				return false;
			}
		}
		Arrays.sort(prayers);
		for (final Effect e : getQuickEffects()) {
			if (Arrays.binarySearch(prayers, e) < 0) {
				final Component p = pane.getChild(e.getId());
				if (p == null) {
					return false;
				}
				if (p.interact("Deselect")) {
					final Timer t = new Timer(500);
					while (t.isRunning() && !isEffectQuick(e)) {
						Delay.sleep(15);
					}
				} else {
					final Component complete = prayer.getComponent(12);
					if (complete != null) {
						complete.interact("Confirm");
					}
					return false;
				}
			}
		}
		final Component complete = prayer.getComponent(12);
		return complete != null && complete.interact("Confirm");
	}

	public boolean setEffect(final Effect prayer, final boolean activate) {
		if (prayer.getBook() != getPrayerBook()
				|| prayer.getRequiredLevel() > ctx.skills.getRealLevel(Skills.PRAYER)) {
			return false;
		}
		if (isEffectActive(prayer) == activate) {
			return true;
		}
		if (ctx.game.openTab(Game.Tab.PRAYER)) {
			Component c = ctx.widgets.get(WIDGET, 9);
			if (c != null) {
				c = c.getChild(prayer.getId());
			}
			return c != null && c.interact(activate ? "Activate" : "Deactivate");
		}
		return false;
	}

	public boolean deactivateAll() {
		if (getActive().length == 0) {
			return true;
		}
		for (final Effect e : getActive()) {
			if (setEffect(e, false)) {
				final Timer timer = new Timer(500);
				while (timer.isRunning() && isEffectActive(e)) {
					Delay.sleep(15);
				}
			}
		}
		return getActive().length == 0;
	}

	public static enum Curses implements Effect {
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
		private final int id, shift, level;

		Curses(final int id, final int shift, final int level) {
			this.id = id;
			this.shift = shift;
			this.level = level;
		}

		public int getId() {
			return this.id;
		}

		public int getBook() {
			return PRAYER_BOOK_CURSES;
		}

		public int getRequiredLevel() {
			return this.level;
		}

		public int getShift() {
			return shift;
		}
	}

	public static enum Normal implements Effect {
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
		private final int id, shift, level;

		private Normal(final int id, final int shift, final int level) {
			this.id = id;
			this.shift = shift;
			this.level = level;
		}

		public int getId() {
			return this.id;
		}

		public int getBook() {
			return PRAYER_BOOK_NORMAL;
		}

		public int getRequiredLevel() {
			return this.level;
		}

		public int getShift() {
			return shift;
		}
	}

	public interface Effect {
		public int getId();

		public int getBook();

		public int getRequiredLevel();

		public int getShift();
	}
}
