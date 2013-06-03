package org.powerbot.bot;

import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.client.Client;
import org.powerbot.script.methods.Game;

public class World {
	private static final World EMPTY = new World();
	private final Game.Toolkit toolkit;
	private final Game.Viewport viewport;
	private AtomicReference<Client> client;

	public World() {
		this.toolkit = new Game.Toolkit();
		this.viewport = new Game.Viewport();
		this.client = new AtomicReference<>(null);
	}

	public static World getWorld() {
		final Bot bot = Bot.getInstance();
		return bot != null ? bot.world : EMPTY;
	}

	public void setClient(Client client) {
		this.client.set(client);
	}

	public Client getClient() {
		return this.client.get();
	}

	public Game.Toolkit getToolkit() {
		return this.toolkit;
	}

	public Game.Viewport getViewport() {
		return this.viewport;
	}
}
