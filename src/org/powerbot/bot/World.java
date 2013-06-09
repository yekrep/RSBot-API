package org.powerbot.bot;

import org.powerbot.client.Client;
import org.powerbot.script.methods.Components;
import org.powerbot.script.methods.Game;
import org.powerbot.script.wrappers.Widget;

import java.util.concurrent.atomic.AtomicReference;

public class World {
	private static final World EMPTY = new World();
	private final Game.Toolkit toolkit;
	private final Game.Viewport viewport;
	private AtomicReference<Client> client;
	public int preferredWorld;
	public Components.Container components;
	public Widget[] cache;

	public World() {
		this.toolkit = new Game.Toolkit();
		this.viewport = new Game.Viewport();
		this.client = new AtomicReference<>(null);
		this.preferredWorld = -1;
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
