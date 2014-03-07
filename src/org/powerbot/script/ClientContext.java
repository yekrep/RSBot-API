package org.powerbot.script;

import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.bot.script.InputSimulator;
import org.powerbot.script.os.tools.Game;
import org.powerbot.script.os.tools.GroundItems;
import org.powerbot.script.os.tools.Menu;
import org.powerbot.script.os.tools.Mouse;
import org.powerbot.script.os.tools.Movement;
import org.powerbot.script.os.tools.Npcs;
import org.powerbot.script.os.tools.Objects;
import org.powerbot.script.os.tools.Players;
import org.powerbot.script.os.tools.Varpbits;
import org.powerbot.script.os.tools.Widgets;
import org.powerbot.bot.os.Bot;
import org.powerbot.bot.os.client.Client;

public class ClientContext {
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
	private final AtomicReference<Client> client;
	private final AtomicReference<Bot> bot;

	private ClientContext(final Bot bot) {
		client = new AtomicReference<Client>(null);
		this.bot = new AtomicReference<Bot>(bot);

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

	public ClientContext(final ClientContext ctx) {
		client = ctx.client;
		bot = ctx.bot;

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

	public static ClientContext newContext(final Bot bot) {
		return new ClientContext(bot);
	}

	public void setClient(final Client client) {
		this.client.set(client);
	}

	public Client client() {
		return client.get();
	}

	public Bot bot() {
		return bot.get();
	}
}
