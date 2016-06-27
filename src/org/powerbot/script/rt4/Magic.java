package org.powerbot.script.rt4;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Game.Tab;

/**
 * Magic
 */
public class Magic extends ClientAccessor {
	public Magic(final ClientContext ctx) {
		super(ctx);
	}
	
	/**
	 * Retrieves the current book. Required the {@link Game#tab()} to be on
	 * the Magic Tab.
	 * 
	 * @return The current book, or {@link Book.NIL} if the Magic Tab is
	 * closed.
	 */
	public Book book() {
		for (final Book b : Book.values()) {
			if (ctx.widgets.widget(b.widget).component(0).valid()) {
				return b;
			}
		}
		return Book.NIL;
	}

	/**
	 * Retrieves the selected spell, or {@link Spell.NIL} if
	 * there is no actively selected spell.
	 * 
	 * @return The selected Spell, or {@link Spell.NIL} if no spell is
	 * selected.
	 */
	public Spell spell() {
		final Book book = book();
		for (final Spell spell : Spell.values()) {
			if (spell.book != book) {
				continue;
			}
			if (ctx.widgets.component(spell.book.widget, spell.component()).borderThickness() == 2) {
				return spell;
			}
		}
		return Spell.NIL;
	}

	/**
	 * Casts the specified spell. If the bot cannot switch to the Magic tab, or
	 * if the Magic Book is not correct, it will return {@code false}.
	 * 
	 * @param spell The spell to cast.
	 * @return {@code true} if the spell was successfully casted, {@code false}
	 * otherwise.
	 */
	public boolean cast(final Spell spell) {
		if (!ctx.game.tab(Game.Tab.MAGIC)) {
			return false;
		}
		final Spell s = spell();
		if (s != Spell.NIL) {
			if (!ctx.widgets.component(spell.book.widget, s.component()).click("Cast") || !Condition.wait(new Condition.Check() {
				@Override
				public boolean poll() {
					return spell() == Spell.NIL;
				}
			}, 10, 30)) {
				return false;
			}
		}
		return ready(spell) && ctx.widgets.component(spell.book.widget,
					spell.component()).click("Cast");
	}
	
	/**
	 * Determines whether or not the specified spell is ready to be casted.
	 * This requires the Magic tab to be opened to check. If the Magic tab
	 * is not open, it will always return {@code false}.
	 * 
	 * @param spell The spell to validate.
	 * @return {@code true} if it is ready to be cast, {@code false} otherwise.
	 */
	public boolean ready(final Spell spell) {
		return ctx.game.tab() == Tab.MAGIC &&
				ctx.widgets.component(spell.book.widget, spell.component())
				.textureId() != spell.offTexture;
	}

	public enum Spell {
		NIL(Book.NIL, -1, -1),//Selected spell 192,x=bt2

		HOME_TELEPORT(Book.MODERN, 0, 0),
		WIND_STRIKE(Book.MODERN, 1, 1, 65),
		CONFUSE(Book.MODERN, 3, 2, 66),
		ENCHANT_CROSSBOW_BOLT_OPAL(Book.MODERN, 4, 3),
		WATER_STRIKE(Book.MODERN, 5, 4, 67),
		ENCHANT_LEVEL_1_JEWELLERY(Book.MODERN, 7, 5, 68),
		ENCHANT_CROSSBOW_BOLT_SAPPHIRE(Book.MODERN, 7, 3),
		EARTH_STRIKE(Book.MODERN, 9, 6, 69),
		WEAKEN(Book.MODERN, 11, 7, 70),
		FIRE_STRIKE(Book.MODERN, 13, 8, 71),
		ENCHANT_CROSSBOW_BOLT_JADE(Book.MODERN, 14, 3),
		BONES_TO_BANANAS(Book.MODERN, 15, 9, 72),
		WIND_BOLT(Book.MODERN, 17, 10, 73),
		CURSE(Book.MODERN, 19, 11, 74),
		BIND(Book.MODERN, 20, 12, 369),
		LOW_LEVEL_ALCHEMY(Book.MODERN, 21, 13, 75),
		WATER_BOLT(Book.MODERN, 23, 14, 76),
		ENCHANT_CROSSBOW_BOLT_PEARL(Book.MODERN, 24, 3),
		VARROCK_TELEPORT(Book.MODERN, 25, 15, 77),
		ENCHANT_LEVEL_2_JEWELLERY(Book.MODERN, 27, 16, 78),
		ENCHANT_CROSSBOW_BOLT_EMERALD(Book.MODERN, 27, 3),
		EARTH_BOLT(Book.MODERN, 29, 17, 79),
		ENCHANT_CROSSBOW_BOLT_RED_TOPAZ(Book.MODERN, 29, 3),
		LUMBRIDGE_TELEPORT(Book.MODERN, 31, 18, 80),
		TELEKINETIC_GRAB(Book.MODERN, 33, 19, 81),
		FIRE_BOLT(Book.MODERN, 25, 20, 82),
		FALADOR_TELEPORT(Book.MODERN, 37, 21, 83),
		CRUMBLE_UNDEAD(Book.MODERN, 39, 22, 84),
		TELEPORT_TO_HOUSE(Book.MODERN, 40, 23, 405),
		WIND_BLAST(Book.MODERN, 41, 24, 85),
		SUPERHEAT_ITEM(Book.MODERN, 43, 25, 86),
		CAMELOT_TELEPORT(Book.MODERN, 45, 26, 87),
		WATER_BLAST(Book.MODERN, 47, 27, 88),
		ENCHANT_LEVEL_3_JEWELLERY(Book.MODERN, 49, 28, 89),
		ENCHANT_CROSSBOW_BOLT_RUBY(Book.MODERN, 49, 3),
		IBAN_BLAST(Book.MODERN, 50, 29, 103),
		SNARE(Book.MODERN, 50, 30, 370),
		MAGIC_DART(Book.MODERN, 50, 31, 374),
		ARDOUGNE_TELEPORT(Book.MODERN, 51, 32, 104),
		EARTH_BLAST(Book.MODERN, 51, 33, 90),
		HIGH_ALCHEMY(Book.MODERN, 55, 34, 91),
		CHARGE_WATER_ORB(Book.MODERN, 56, 35, 92),
		ENCHANT_LEVEL_4_JEWELLERY(Book.MODERN, 57, 36, 93),
		ENCHANT_CROSSBOW_BOLT_DIAMOND(Book.MODERN, 57, 3),
		WATCHTOWER_TELEPORT(Book.MODERN, 58, 37, 105),
		FIRE_BLAST(Book.MODERN, 59, 38, 94),
		CHARGE_EARTH_ORB(Book.MODERN, 60, 39, 95),
		BONES_TO_PEACHES(Book.MODERN, 60, 40, 404),
		SARADOMIN_STRIKE(Book.MODERN, 60, 41, 111),
		CLAWS_OF_GUTHIX(Book.MODERN, 60, 42, 110),
		FLAMES_OF_ZAMORAK(Book.MODERN, 60, 43, 109),
		TROLLHEIM_TELEPORT(Book.MODERN, 61, 44, 373),
		WIND_WAVE(Book.MODERN, 62, 45, 96),
		CHARGE_FIRE_ORB(Book.MODERN, 63, 46, 97),
		TELEPORT_APE_ATOLL(Book.MODERN, 64, 47, 407),
		WATER_WAVE(Book.MODERN, 65, 48, 98),
		CHARGE_AIR_ORB(Book.MODERN, 66, 49, 99),
		VULNERABILITY(Book.MODERN, 66, 50, 106),
		ENCHANT_LEVEL_5_JEWELLERY(Book.MODERN, 68, 51, 100),
		ENCHANT_CROSSBOW_BOLT_DRAGONSTONE(Book.MODERN, 68, 3),
		TELEPORT_KOUREND(Book.MODERN, 69, 52, 410),
		EARTH_WAVE(Book.MODERN, 70, 53, 101),
		ENFEEBLE(Book.MODERN, 73, 54, 107),
		TELEOTHER_LUMBRIDGE(Book.MODERN, 74, 55, 399),
		FIRE_WAVE(Book.MODERN, 75, 56, 102),
		ENTANGLE(Book.MODERN, 79, 57, 371),
		STUN(Book.MODERN, 80, 58, 108),
		CHARGE(Book.MODERN, 80, 59, 372),
		TELEOTHER_FALADOR(Book.MODERN, 82, 60, 400),
		TELE_BLOCK(Book.MODERN, 85, 61, 402),
		TELEPORT_TO_BOUNTY_TARGET(Book.MODERN, 90, 62, 409),
		ENCHANT_LEVEL_6_JEWELLERY(Book.MODERN, 87, 63, 403),
		ENCHANT_CROSSBOW_BOLT_ONYX(Book.MODERN, 87, 3),
		TELEOTHER_CAMELOT(Book.MODERN, 90, 64, 401),
		ENCHANT_LEVEL_7_JEWELLERY(Book.MODERN, 93, 65, 411);
		
		private final Book book;
		private final int level, component, offTexture;

		private Spell(final Book book, final int level, final int component) {
			this(book, level, component, -1);
		}
		
		private Spell(final Book book, final int level, final int component,
				final int offTexture) {
			this.book = book;
			this.level = level;
			this.component = component;
			this.offTexture = offTexture;
		}

		public Book book() {
			return book;
		}

		public int level() {
			return level;
		}

		public int component() {
			return component + book.offset;
		}
	}

	public enum Book {
		MODERN(218, 1), NIL(-1, 0);
		private final int widget;
		private final int offset;

		private Book(final int widget, final int offset) {
			this.widget = widget;
			this.offset = offset;
		}

		public int widget() {
			return widget;
		}
	}
}
