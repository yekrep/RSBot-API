package org.powerbot.os.api;

import org.powerbot.os.bot.Bot;
import org.powerbot.os.client.Client;

import java.util.concurrent.atomic.AtomicReference;

public class MethodContext {
	private final AtomicReference<Client> client;
	private final AtomicReference<Bot> bot;
	public final Game game;
	public final GroundItems groundItems;
	public final Npcs npcs;
	public final Players players;

	private MethodContext(final Bot bot) {
		client = new AtomicReference<Client>(null);
		this.bot = new AtomicReference<Bot>(bot);

		game = new Game(this);
		groundItems = new GroundItems(this);
		npcs = new Npcs(this);
		players = new Players(this);
	}

	public static MethodContext newContext(final Bot bot) {
		return new MethodContext(bot);
	}

	public MethodContext(final MethodContext ctx) {
		client = ctx.client;
		bot = ctx.bot;

		game = ctx.game;
		groundItems = ctx.groundItems;
		npcs = ctx.npcs;
		players = ctx.players;
	}

	public void setClient(final Client client) {
		this.client.set(client);
	}

	public Client getClient() {
		return client.get();
	}

	public Bot getBot() {
		return bot.get();
	}
}
