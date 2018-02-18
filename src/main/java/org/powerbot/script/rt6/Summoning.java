package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;

/**
 * Summoning
 */
public class Summoning extends ClientAccessor {
	/**
	 * The inventory of the current {@link org.powerbot.script.rt6.Summoning.Familiar} if it is a Beast of Burden.
	 */
	public final FamiliarInventory familiarInventory;

	public Summoning(final ClientContext factory) {
		super(factory);
		familiarInventory = new FamiliarInventory(factory);
	}

	/**
	 * Returns the current amount of summoning points.
	 *
	 * @return the current amount summoning points
	 */
	public int points() {
		return ctx.skills.level(Constants.SKILLS_SUMMONING);
	}

	/**
	 * Returns the time left for the spawned familiar.
	 *
	 * @return the time left for the spawned familiar
	 */
	public int timeLeft() {
		return (ctx.varpbits.varpbit(Constants.SUMMONING_TIME) >>> 6) * 30;
	}

	/**
	 * Returns the current amount of special points.
	 *
	 * @return the amount of special points
	 */
	public int specialPoints() {
		return ctx.varpbits.varpbit(Constants.SUMMONING_POINTS);
	}

	/**
	 * Determines if a familiar is summoned.
	 *
	 * @return {@code true} if a familiar is summoned; otherwise {@code false}
	 */
	public boolean summoned() {
		return ctx.varpbits.varpbit(Constants.SUMMONING_NPC) > 0;
	}

	/**
	 * Selects the specified option in the summoning menu.
	 *
	 * @param option the desired option to select
	 * @return {@code true} if the action was selected; otherwise {@code false}
	 */
	public boolean select(final Option option) {
		return select(option.text());
	}

	/**
	 * Selects the specified option in the summoning menu.
	 *
	 * @param action the desired option to select
	 * @return {@code true} if the action was selected; otherwise {@code false}
	 */
	public boolean select(final String action) {
		final Component c = ctx.hud.legacy() ? ctx.widgets.component(1506, 2) :
				ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_SUMMONING_BUTTON);
		if (Option.RENEW_FAMILIAR.text().toLowerCase().contains(action.toLowerCase())) {
			final Familiar familiar = familiar();
			return familiar != null && familiar.requiredPoints() <= specialPoints() &&
					ctx.backpack.select().id(ctx.varpbits.varpbit(Constants.SUMMONING_POUCH)).count() > 0 && c.interact(action);
		}
		if (Option.DISMISS.text().toLowerCase().contains(action.toLowerCase())) {
			if (c.interact(action) && Condition.wait(new Condition.Check() {
				@Override
				public boolean poll() {
					return ctx.chat.select().text("Yes").isEmpty();
				}
			})) {
				final ChatOption o = ctx.chat.poll();
				if (o.select(Random.nextBoolean())) {
					Condition.sleep();
					if (o.select(Random.nextBoolean())) {
						return Condition.wait(new Condition.Check() {
							@Override
							public boolean poll() {
								return !summoned();
							}
						});
					}
				}
			}
			return false;
		}
		if (Option.CAST.text().toLowerCase().contains(action.toLowerCase())) {
			final Familiar familiar = familiar();
			return familiar != null && familiar.requiredSpecialPoints() <= specialPoints()
					&& c.interact(action);
		}
		return c.interact(action);
	}

	/**
	 * Returns the {@link Option} the left click option is set to.
	 *
	 * @return the {@link Option} when left clicked
	 */
	public Option clickOption() {
		final int val = ctx.varpbits.varpbit(Constants.SUMMONING_LEFT);
		for (final Option o : Option.values()) {
			if (val == o.value()) {
				return o;
			}
		}
		return Option.FOLLOWER_DETAILS;
	}

	/**
	 * Changes the left click option.
	 *
	 * @param option the desired option
	 * @return {@code true} if the option was successfully changed; otherwise {@code false}
	 */
	public boolean clickOption(final Option option) {
		if (ctx.varpbits.varpbit(Constants.SUMMONING_LEFT) == option.value()) {
			return true;
		}
		if (!(ctx.hud.legacy() ? ctx.widgets.component(1506, 2) :
				ctx.widgets.component(Constants.COMBATBAR_WIDGET, Constants.COMBATBAR_SUMMONING_BUTTON)).interact("Select")) {
			return false;
		}
		if (!Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return ctx.widgets.widget(Constants.SUMMONING_LEFT_SELECT).valid();
			}
		}, 30, 100)) {
			return false;
		}
		if (ctx.widgets.component(Constants.SUMMONING_LEFT_SELECT, option.id()).interact("Select")) {
			Condition.wait(new Condition.Check() {
				@Override
				public boolean poll() {
					return ctx.varpbits.varpbit(Constants.SUMMONING_LEFT_SELECTED) == option.tentative();
				}
			}, 150, 20);
		}
		final Component confirm = ctx.widgets.component(Constants.SUMMONING_LEFT_SELECT, Constants.SUMMONING_CONFIRM);
		for (int i = 0; i < 3; i++) {
			if (!confirm.valid()) {
				break;
			}
			if (confirm.interact("Confirm")) {
				Condition.wait(new Condition.Check() {
					@Override
					public boolean poll() {
						return ctx.varpbits.varpbit(Constants.SUMMONING_LEFT) == option.value();
					}
				}, 150, 20);
			}
		}
		return ctx.varpbits.varpbit(Constants.SUMMONING_LEFT) == option.value();
	}


	/**
	 * Returns the {@link Npc} of the currently summoned familiar.
	 *
	 * @return the {@link Npc}; otherwise {@link org.powerbot.script.rt6.Npcs#get()}
	 */
	public Npc npc() {
		if (!summoned()) {
			return ctx.npcs.nil();
		}
		final Player local = ctx.players.local();
		final int id = ctx.varpbits.varpbit(Constants.SUMMONING_NPC);
		return ctx.npcs.select().id(id, id + 1).select(new Filter<Npc>() {
			@Override
			public boolean accept(final Npc npc) {
				final Actor actor;
				return (actor = npc.interacting()) != null && actor.equals(local);
			}
		}).nearest().poll();
	}

	/**
	 * Returns the {@link Familiar} of the currently summoned familiar.
	 *
	 * @return the {@link Familiar}
	 */
	public Familiar familiar() {
		if (!summoned()) {
			return null;
		}
		for (final Familiar f : Familiar.values()) {
			if (f.pouchId() == ctx.varpbits.varpbit(Constants.SUMMONING_POUCH)) {
				return f;
			}
		}
		return null;
	}

	/**
	 * Calls the familiar on the familiar menu.
	 *
	 * @return {@code true} if the action was clicked.
	 */
	public boolean call() {
		final Component c = ctx.widgets.component(Constants.SUMMONING_WIDGET, 49);
		return c != null && summoned() && c.visible() && c.interact("Call");
	}

	/**
	 * Dismisses the familiar on the familiar menu.
	 *
	 * @return {@code true} if the action was clicked.
	 */
	public boolean dismiss() {
		final Component c = ctx.widgets.component(Constants.SUMMONING_WIDGET, 51);
		return c != null && summoned() && c.visible() && c.interact("Dismiss Now");
	}

	/**
	 * Selects take bob on the familiar menu.
	 *
	 * @return {@code true} if the action was clicked.
	 */
	public boolean takeBoB() {
		final Component c = ctx.widgets.component(Constants.SUMMONING_WIDGET, 67);
		return c != null && summoned() && c.visible() && c.interact("Take");
	}

	/**
	 * Renews the familiar on the familiar menu.
	 *
	 * @return {@code true} if the action was clicked.
	 */
	public boolean renew() {
		final Component c = ctx.widgets.component(Constants.SUMMONING_WIDGET, 69);
		return c != null && summoned() && c.visible() && c.interact("Renew");
	}

	/**
	 * Casts on the familiar menu.
	 *
	 * @return {@code true} if the action was clicked.
	 */
	public boolean cast() {
		final Component c = ctx.widgets.component(Constants.SUMMONING_WIDGET, 5);
		return c != null && summoned() && c.visible() && c.interact("Cast");
	}

	/**
	 * Attacks on the familiar menu.
	 *
	 * @return {@code true} if the action was clicked.
	 */
	public boolean attack() {
		final Component c = ctx.widgets.component(Constants.SUMMONING_WIDGET, 65);
		return c != null && summoned() && c.visible() && c.interact("Attack");
	}

	/**
	 * An enumeration of possible familiars.
	 */
	public enum Familiar {
		SPIRIT_WOLF(12047, 1, 0, 1, 12533, 3),
		DREADFOWL(12043, 4, 0, 1, 12445, 3),
		MEERKATS(19622, 4, 0, 1, 19621, 12),
		SPIRIT_SPIDER(12059, 10, 0, 2, 12428, 6),
		THORNY_SNAIL(12019, 13, 3, 2, 12459, 3),
		GRANITE_CRAB(12009, 16, 0, 2, 12533, 12),
		SPIRIT_MOSQUITO(12778, 17, 0, 2, 12838, 3),
		DESERT_WYRM(12049, 18, 0, 1, 12460, 6),
		SPIRIT_SCORPION(12055, 19, 0, 2, 12432, 6),
		SPIRIT_TZ_KIH(12808, 22, 0, 3, 12839, 6),
		ALBINO_RAT(12067, 23, 0, 3, 12430, 6),
		SPIRIT_KALPHITE(12063, 25, 6, 3, 12446, 6),
		COMPOST_MOUND(12091, 28, 0, 6, 12440, 12),
		GIANT_CHINCHOMPA(12800, 29, 0, 1, 12834, 3),
		VAMPYRE_BAT(12053, 31, 0, 4, 12447, 4),
		HONEY_BADGER(12065, 32, 0, 4, 12433, 4),
		BEAVER(12021, 33, 0, 4, 12429, 3),
		VOID_RAVAGER(12818, 34, 0, 4, 12443, 3),
		VOID_SPINNER(12780, 34, 0, 4, 12443, 3),
		VOID_SHIFTER(12814, 34, 0, 4, 12443, 3),
		VOID_TORCHER(12798, 34, 0, 4, 12443, 3),
		BRONZE_MINOTAUR(12073, 36, 0, 9, 12462, 6),
		BULL_ANT(12087, 40, 8, 5, 12431, 12),
		MACAW(12071, 41, 0, 5, 12422, 12),
		EVIL_TURNIP(12051, 42, 0, 5, 12448, 6),
		SPIRIT_COCKATRICE(12095, 43, 0, 5, 12458, 3),
		SPIRIT_GUTHATRICE(12097, 43, 0, 5, 12458, 3),
		SPIRIT_SARATRICE(12099, 43, 0, 5, 12458, 3),
		SPIRIT_ZAMATRICE(12101, 43, 0, 5, 12458, 3),
		SPIRIT_PENGATRICE(12103, 43, 0, 5, 12458, 3),
		SPIRIT_CORAXATRICE(12105, 43, 0, 5, 12458, 3),
		SPIRIT_VULATRICE(12107, 43, 0, 5, 12458, 3),
		IRON_MINOTAUR(12075, 46, 0, 9, 12463, 6),
		PYRELORD(12816, 46, 0, 5, 12829, 6),
		MAGPIE(12041, 47, 0, 5, 12426, 3),
		BLOATED_LEECH(12061, 49, 0, 5, 12444, 6),
		SPIRIT_TERRORBIRD(12007, 52, 12, 6, 12441, 8),
		ABYSSAL_PARASITE(12035, 54, 7, 6, 12454, 6),
		SPIRIT_JELLY(12027, 55, 0, 6, 12453, 6),
		STEEL_MINOTAUR(12077, 56, 0, 9, 12464, 6),
		IBIS(12531, 56, 0, 6, 12424, 12),
		SPIRIT_GRAAHK(12810, 57, 0, 6, 12835, 3),
		SPIRIT_KYATT(12812, 57, 0, 6, 12836, 3),
		SPIRIT_LARUPIA(12784, 57, 0, 6, 12840, 6),
		KARAMTHULHU_OVERLORD(12023, 58, 0, 6, 12455, 3),
		SMOKE_DEVIL(12085, 61, 0, 7, 12468, 6),
		ABYSSAL_LURKER(12037, 62, 7, 7, 12427, 3),
		SPIRIT_COBRA(12015, 63, 0, 7, 12436, 3),
		STRANGER_PLANT(12045, 64, 0, 7, 12467, 6),
		MITHRIL_MINOTAUR(12079, 66, 0, 9, 12465, 6),
		BARKER_TOAD(12123, 66, 0, 7, 12452, 6),
		WAR_TORTOISE(12031, 67, 18, 7, 12439, 20),
		BUNYIP(12029, 68, 0, 7, 12438, 3),
		FRUIT_BAT(12033, 69, 0, 7, 12423, 6),
		RAVENOUS_LOCTUS(12820, 70, 0, 4, 12830, 12),
		ARTIC_BEAR(12057, 71, 0, 8, 12451, 6),
		PHOENIX(14623, 72, 0, 8, 14622, 5),
		OBSIDIAN_GOLEM(12792, 73, 0, 8, 12826, 12),
		GRANITE_LOBSTER(12069, 74, 0, 8, 12449, 6),
		PRAYING_MANTRIS(12011, 75, 0, 8, 12450, 6),
		ADAMANT_MINOTAUR(12081, 76, 0, 9, 12466, 6),
		FORGE_REGENT(12782, 76, 0, 9, 12841, 6),
		TALON_BEAST(12794, 77, 0, 9, 12831, 6),
		GIANT_ENT(12013, 78, 0, 8, 12457, 6),
		FIRE_TITAN(12802, 79, 0, 9, 12824, 20),
		ICE_TITAN(12806, 79, 0, 9, 12824, 20),
		MOSS_TITAN(12804, 79, 0, 9, 12824, 20),
		HYDRA(12025, 80, 0, 8, 12442, 6),
		SPIRIT_DAGANNOTH(12017, 83, 0, 9, 12456, 6),
		LAVA_TITAN(12788, 83, 0, 9, 12837, 4),
		SWAMP_TITAN(12776, 85, 0, 9, 12832, 6),
		RUNE_MINOTAUR(12083, 86, 0, 9, 12467, 6),
		GHAST_FAMILIAR(21444, 87, 0, 1, 21453, 20),
		UNICORN_STALLION(12039, 88, 0, 9, 12434, 20),
		GEYSER_TITAN(12786, 89, 0, 10, 12833, 6),
		WOLPERTINGER(12089, 92, 0, 10, 12437, 20),
		ABYSSAL_TITAN(12796, 93, 7, 10, 12827, 6),
		IRON_TITAN(12822, 95, 0, 10, 12828, 12),
		PACK_YAK(12093, 96, 30, 10, 12435, 12),
		STEEL_TITAN(12790, 99, 0, 10, 12825, 12);
		private final int pouch, level, space, points, scroll, special;

		Familiar(final int pouch, final int level, final int space, final int points, final int scroll, final int special) {
			this.pouch = pouch;
			this.level = level;
			this.space = space;
			this.points = points;
			this.special = special;
			this.scroll = scroll;
		}

		public int pouchId() {
			return this.pouch;
		}

		public int requiredLevel() {
			return this.level;
		}

		public int bobSpace() {
			return this.space;
		}

		public int requiredPoints() {
			return this.points;
		}

		public int scrollId() {
			return this.scroll;
		}

		public int requiredSpecialPoints() {
			return this.special;
		}
	}

	/**
	 * An enumeration of game options.
	 */
	public enum Option {
		FOLLOWER_DETAILS("Follower Details", 7, 0x10, 0x0),
		CAST("Cast", 9, 0x11, 0x1),
		ATTACK("Attack", 11, 0x12, 0x2),
		CALL_FOLLOWER("Call Follower", 13, 0x13, 0x3),
		DISMISS("Dismiss", 15, 0x14, 0x4),
		TAKE_BOB("Take BoB", 17, 0x15, 0x5),
		RENEW_FAMILIAR("Renew Familiar", 19, 0x16, 0x6),
		INTERACT("Interact", 22, 0x17, 0x7);
		private final String text;
		private final int id, setting, set;

		Option(final String text, final int id, final int setting, final int set) {
			this.text = text;
			this.id = id;
			this.setting = setting;
			this.set = set;
		}

		public String text() {
			return this.text;
		}

		public int id() {
			return id;
		}

		public int value() {
			return setting;
		}

		public int tentative() {
			return set;
		}
	}

	/**
	 * The {@link org.powerbot.script.rt6.Summoning.Familiar} inventory.
	 */
	public static class FamiliarInventory extends ClientAccessor {
		public FamiliarInventory(final ClientContext ctx) {
			super(ctx);
		}

		/**
		 * Stores the specified {@link org.powerbot.script.rt6.Item} in the {@link org.powerbot.script.rt6.Summoning.Familiar}s inventory.
		 *
		 * @param id the {@link org.powerbot.script.rt6.Item} ID
		 * @return {@code true} if the {@link org.powerbot.script.rt6.Item} was stored, otherwise {@code false}
		 */
		public boolean store(final int id) {
			if (!opened()) {
				return false;
			}
			for (final Item i : ctx.backpack.select()) {
				if (i.id() == id) {
					return i.interact("Store-All") && Condition.wait(new Condition.Check() {
						@Override
						public boolean poll() {
							return !i.valid();
						}
					});
				}
			}
			return false;
		}

		/**
		 * Opens the {@link org.powerbot.script.rt6.Summoning.Familiar} inventory.
		 *
		 * @return true if the inventory exists and was opened
		 */
		public boolean open() {
			if (!opened()) {
				final Npc familiar = ctx.summoning.npc();
				if (familiar.interact("Store")) {
					Condition.wait(new Condition.Check() {
						@Override
						public boolean poll() {
							return opened();
						}
					});
				}
			}
			return opened();
		}

		/**
		 * Returns {@code true} if the {@link org.powerbot.script.rt6.Summoning.Familiar} inventory exists and is open.
		 *
		 * @return {@code true} if open, otherwise {@code false}
		 */
		public boolean opened() {
			final Component inventory = ctx.widgets.component(Constants.FAMILIAR_INVENTORY_WIDGET, Constants.FAMILIAR_INVENTORY_COMPONENT);
			return inventory.visible() && inventory.inViewport();
		}

		/**
		 * Closes the {@link org.powerbot.script.rt6.Summoning.Familiar} inventory if it exists and is open.
		 *
		 * @return {@code true} if the inventory is not open
		 */
		public boolean close() {
			final Component close = ctx.widgets.component(Constants.FAMILIAR_INVENTORY_WIDGET, 22).component(1);
			if (close.visible() && close.click()) {
				Condition.wait(new Condition.Check() {
					@Override
					public boolean poll() {
						return !close.visible();
					}
				});
			}
			return !opened();
		}

		/**
		 * Returns an array of {@link org.powerbot.script.rt6.Item}s in the familiar inventory.
		 *
		 * @return the list of {@link org.powerbot.script.rt6.Item}s, or an empty array of the inventory is not open
		 */
		public Item[] items() {
			if (opened()) {
				final Component inventory = ctx.widgets.component(Constants.FAMILIAR_INVENTORY_WIDGET, Constants.FAMILIAR_INVENTORY_ITEMS);
				final List<Item> items = new ArrayList<Item>();
				for (final Component c : inventory.components()) {
					if (c.itemId() != -1) {
						items.add(new Item(ctx, c.itemId(), c.itemStackSize(), c));
					}
				}

				return items.toArray(new Item[items.size()]);
			}
			return new Item[0];
		}
	}
}
