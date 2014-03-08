package org.powerbot.script.os.tools;

import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.bot.InputSimulator;
import org.powerbot.bot.os.Bot;
import org.powerbot.bot.os.client.Client;
import org.powerbot.bot.ScriptController;
import org.powerbot.script.Script;

public class ClientContext extends org.powerbot.script.ClientContext {
	private final AtomicReference<Client> client;

	public final Script.Controller controller;

	public final Game game;
	public final GroundItems groundItems;
	public final Menu menu;
	public final Mouse mouse;
	public final Movement movement;
	public final Npcs npcs;
	public final Objects objects;
	public final Players players;
	public final Varpbits varpbits;
	public final Widgets widgets;
	public final InputSimulator input;

	private ClientContext(final Bot bot) {
		super(bot);
		client = new AtomicReference<Client>(null);

		controller = new ScriptController<ClientContext>(this);

		game = new Game(this);
		groundItems = new GroundItems(this);
		menu = new Menu(this);
		mouse = new Mouse(this);
		movement = new Movement(this);
		npcs = new Npcs(this);
		objects = new Objects(this);
		players = new Players(this);
		varpbits = new Varpbits(this);
		widgets = new Widgets(this);
		input = new InputSimulator(null);
	}

	public static ClientContext newContext(final Bot bot) {
		return new ClientContext(bot);
	}

	public ClientContext(final ClientContext ctx) {
		super(ctx.bot());

		client = ctx.client;

		controller = ctx.controller;

		game = ctx.game;
		groundItems = ctx.groundItems;
		menu = ctx.menu;
		mouse = ctx.mouse;
		movement = ctx.movement;
		npcs = ctx.npcs;
		objects = ctx.objects;
		players = ctx.players;
		varpbits = ctx.varpbits;
		widgets = ctx.widgets;
		input = ctx.input;
	}

	public void setClient(final Client client) {
		this.client.set(client);
	}

	public Client client() {
		return client.get();
	}

	public Script.Controller controller() {
		return controller;
	}
}
