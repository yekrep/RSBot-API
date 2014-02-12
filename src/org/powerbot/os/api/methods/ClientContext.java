package org.powerbot.os.api.methods;

import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.os.bot.Bot;
import org.powerbot.os.client.Client;

public class ClientContext {
	private final AtomicReference<Client> client;
	private final AtomicReference<Bot> bot;

	public final Game game;
	public final GroundItems groundItems;
	public final Menu menu;
	public final Movement movement;
	public final Npcs npcs;
	public final Objects objects;
	public final Players players;
	public final Varpbits varpbits;

	private ClientContext(final Bot bot) {
		client = new AtomicReference<Client>(null);
		this.bot = new AtomicReference<Bot>(bot);

		game = new Game(this);
		groundItems = new GroundItems(this);
		menu = new Menu(this);
		movement = new Movement(this);
		npcs = new Npcs(this);
		objects = new Objects(this);
		players = new Players(this);
		varpbits = new Varpbits(this);
	}

	public static ClientContext newContext(final Bot bot) {
		return new ClientContext(bot);
	}

	public ClientContext(final ClientContext ctx) {
		client = ctx.client;
		bot = ctx.bot;

		game = ctx.game;
		groundItems = ctx.groundItems;
		menu = ctx.menu;
		movement = ctx.movement;
		npcs = ctx.npcs;
		objects = ctx.objects;
		players = ctx.players;
		varpbits = ctx.varpbits;
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
