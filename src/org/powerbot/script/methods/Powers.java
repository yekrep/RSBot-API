package org.powerbot.script.methods;

import java.util.LinkedHashSet;
import java.util.Set;

public class Powers extends MethodProvider {
	public static final int SETTING_PRAYER_POINTS = 3274;
	public static final int SETTING_PRAYER_BOOK = 3277;
	public static final int SETTING_PRAYERS = 3272;
	public static final int SETTING_CURSES = 3275;
	public static final int SETTING_PRAYERS_QUICK = 1770;
	public static final int SETTING_CURSES_QUICK = 1768;
	public static final int BOOK_PRAYERS = 0;
	public static final int BOOK_CURSES = 1;
	public static final int WIDGET_PRAYER = 1458;
	public static final int COMPONENT_PRAYER_CONTAINER = 24;

	public Powers(MethodContext factory) {
		super(factory);
	}

	public enum Prayer implements Effect {
		;
		private final int level;
		private final int drainRate;
		private final boolean members;

		Prayer(int level, int drainRate) {
			this(level, drainRate, false);
		}

		Prayer(int level, int drainRate, boolean members) {
			this.level = level;
			this.drainRate = drainRate;
			this.members = members;
		}

		@Override
		public int getIndex() {
			return ordinal();
		}

		@Override
		public int getLevel() {
			return level;
		}

		@Override
		public int getDrainRate() {
			return drainRate;
		}

		@Override
		public boolean isMembers() {
			return members;
		}
	}

	public enum Curse implements Effect {
		;
		private final int level;
		private final int drainRate;
		private final boolean members;

		Curse(int level, int drainRate) {
			this(level, drainRate, false);
		}

		Curse(int level, int drainRate, boolean members) {
			this.level = level;
			this.drainRate = drainRate;
			this.members = members;
		}

		@Override
		public int getIndex() {
			return ordinal();
		}

		@Override
		public int getLevel() {
			return level;
		}

		@Override
		public int getDrainRate() {
			return drainRate;
		}

		@Override
		public boolean isMembers() {
			return members;
		}
	}

	public interface Effect {
		public int getIndex();

		public int getLevel();

		public int getDrainRate();

		public boolean isMembers();
	}

	public int getPrayerPoints() {
		return (ctx.settings.get(SETTING_PRAYER_POINTS) & 0x7fff) / 10;
	}

	public int getPrayerBook() {
		return ctx.settings.get(SETTING_PRAYER_BOOK) % 2;
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
}
