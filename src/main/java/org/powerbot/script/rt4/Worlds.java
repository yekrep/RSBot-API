package org.powerbot.script.rt4;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.AbstractQuery;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Identifiable;

/**
 * This class is used to manipulate the world switcher interface.
 */
public class Worlds extends AbstractQuery<Worlds, World, ClientContext> implements Identifiable.Query<Worlds> {
	public static final int WORLD_WIDGET = 69, LOGOUT_WIDGET = 182;
	private ArrayList<World> cache = new ArrayList<World>();

	/**
	 * A query of worlds which could be hopped to.
	 *
	 * @param ctx The client context.
	 */
	public Worlds(ClientContext ctx) {
		super(ctx);
	}

	@Override
	protected Worlds getThis() {
		return this;
	}

	@Override
	protected List<World> get() {
		ArrayList<World> worlds = new ArrayList<World>();
		Component list = list();
		if (!list.valid()) {
			return cache;
		}
		Component[] comps = list.components();
		for (int off = 0; off < comps.length - 6; off += 6) {
			World.Type type = World.Type.forType(comps[off + 1].textureId());
			World.Server server = World.Server.forType(comps[off + 3].textureId());
			World.Specialty special = World.Specialty.get(comps[off + 5].text());
			int number = Integer.valueOf(comps[off + 2].text());
			int population = Integer.valueOf(comps[off + 4].text());
			int textColour = Integer.valueOf(comps[off + 5].textColor());
			worlds.add(new World(ctx, number, population, type, server, special, textColour));
		}
		cache = new ArrayList<World>(worlds);
		return worlds;
	}

	/**
	 * Filters the worlds by types.
	 *
	 * @param types The types to target.
	 * @return this instance for chaining purposes.
	 */
	public Worlds types(final World.Type... types) {
		return select(new Filter<World>() {
			public boolean accept(World world) {
				for (World.Type t : types) {
					if (t.equals(world.type())) {
						return true;
					}
				}
				return false;
			}
		});
	}

	/**
	 * Filters the worlds by specialties.
	 *
	 * @param specialties The specialties to target.
	 * @return this instance for chaining purposes.
	 */
	public Worlds specialties(final World.Specialty... specialties) {
		return select(new Filter<World>() {
			public boolean accept(World world) {
				for (World.Specialty s : specialties) {
					if (s.equals(world.specialty())) {
						return true;
					}
				}
				return false;
			}
		});
	}

	/**
	 * Filters the worlds down to the specified servers.
	 *
	 * @param servers The server locations to filter.
	 * @return This instance for chaining purposes.
	 */
	public Worlds servers(final World.Server... servers) {
		return select(new Filter<World>() {
			public boolean accept(World world) {
				for (World.Server s : servers) {
					if (s.equals(world.server())) {
						return true;
					}
				}
				return false;
			}
		});
	}

	/**
	 * Filters the worlds by player count. This will filter down to any world
	 * which is less than or equal to the parameter.
	 *
	 * @param population The population the worlds should be less than or equal to.
	 * @return this instance for chaining purposes.
	 */
	public Worlds population(final int population) {
		return select(new Filter<World>() {
			public boolean accept(World world) {
				return world.size() <= population;
			}
		});
	}

	/**
	 * Filters the worlds by joinable worlds. This will filter out any
	 * dangerous or skill-required worlds* [*where the requirement is not held].
	 *
	 * @return this instance for chaining purposes.
	 */
	public Worlds joinable() {
		return select(new Filter<World>() {
			public boolean accept(World world) {
				return world.valid() &&
						world.type() != World.Type.DEAD_MAN &&
						world.specialty() != World.Specialty.PVP &&
						world.textColor() != 8355711;
			}
		});
	}

	/**
	 * Opens the world switcher.
	 *
	 * @return {@code true} if successfully opened, {@code false} otherwise.
	 */
	public boolean open() {
		ctx.game.tab(Game.Tab.LOGOUT);
		if (ctx.widgets.widget(WORLD_WIDGET).valid()) {
			return true;
		}
		Component c = component(LOGOUT_WIDGET, "World Switcher");
		return c.valid() && c.click() && Condition.wait(new Condition.Check() {
			public boolean poll() {
				return ctx.widgets.widget(WORLD_WIDGET).valid();
			}
		}, 100, 20);
	}

	@Override
	public World nil() {
		return World.NIL;
	}

	protected final Component list() {
		return ctx.components.select(false, WORLD_WIDGET).select(c -> c.componentCount() > 800).width(174).poll();
	}

	protected final Component component(int widget, String text) {
		return ctx.components.select(widget).select(c -> c.text().equalsIgnoreCase(text)).poll();
	}

	@Override
	public Worlds id(int... ids) {
		return select(new Identifiable.Matcher(ids));
	}

	@Override
	public Worlds id(int[]... ids) {
		int z = 0;

		for (final int[] x : ids) {
			z += x.length;
		}

		final int[] a = new int[z];
		int i = 0;

		for (final int[] x : ids) {
			for (final int y : x) {
				a[i++] = y;
			}
		}

		return select(new Identifiable.Matcher(a));
	}

	@Override
	public Worlds id(Identifiable... ids) {
		return select(new Identifiable.Matcher(ids));
	}
}
