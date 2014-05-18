package org.powerbot.script.rt4;

import org.powerbot.bot.rt4.Bot;
import org.powerbot.bot.rt4.client.Client;
import org.powerbot.script.Keyboard;
import org.powerbot.script.Mouse;

/**
 * {@inheritDoc}
 */
public class ClientContext extends org.powerbot.script.ClientContext<Client> {
	public final Bank bank;
	public final Camera camera;
	public final Equipment equipment;
	public final Game game;
	public final GroundItems groundItems;
	public final Inventory inventory;
	public final Keyboard<ClientContext> keyboard;
	public final Magic magic;
	public final Menu menu;
	public final Mouse<ClientContext> mouse;
	public final Movement movement;
	public final Npcs npcs;
	public final Objects objects;
	public final Players players;
	public final Skills skills;
	public final Varpbits varpbits;
	public final Widgets widgets;

	private ClientContext(final Bot bot) {
		super(bot);

		bank = new Bank(this);
		camera = new Camera(this);
		equipment = new Equipment(this);
		game = new Game(this);
		groundItems = new GroundItems(this);
		inventory = new Inventory(this);
		keyboard = new Keyboard<ClientContext>(this);
		magic = new Magic(this);
		menu = new Menu(this);
		mouse = new Mouse<ClientContext>(this);
		movement = new Movement(this);
		npcs = new Npcs(this);
		objects = new Objects(this);
		players = new Players(this);
		skills = new Skills(this);
		varpbits = new Varpbits(this);
		widgets = new Widgets(this);
	}

	/**
	 * Creates a new context for the given {@link org.powerbot.bot.rt4.Bot}.
	 *
	 * @param bot the bot to associate with
	 * @return a new context
	 */
	public static ClientContext newContext(final Bot bot) {
		return new ClientContext(bot);
	}

	/**
	 * Creates a new chained context.
	 *
	 * @param ctx the parent context
	 */
	public ClientContext(final ClientContext ctx) {
		super(ctx);

		bank = ctx.bank;
		camera = ctx.camera;
		equipment = ctx.equipment;
		game = ctx.game;
		groundItems = ctx.groundItems;
		inventory = ctx.inventory;
		keyboard = ctx.keyboard;
		magic = ctx.magic;
		menu = ctx.menu;
		mouse = ctx.mouse;
		movement = ctx.movement;
		npcs = ctx.npcs;
		objects = ctx.objects;
		players = ctx.players;
		skills = ctx.skills;
		varpbits = ctx.varpbits;
		widgets = ctx.widgets;
	}
}
