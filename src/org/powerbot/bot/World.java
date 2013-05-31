package org.powerbot.bot;

import org.powerbot.client.Client;
import org.powerbot.script.methods.Game;

public class World {
	private static final World EMPTY = new World();
	private Client client;
	private Game.Toolkit toolkit;
	private Game.Viewport viewport;

	public World() {
		this.client = null;
		this.toolkit = new Game.Toolkit();
		this.viewport = new Game.Viewport();
	}

	public static World getWorld() {
		final Bot bot = Bot.getInstance();
		return bot != null ? bot.world : EMPTY;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Client getClient() {
		return this.client;
	}

	public Game.Toolkit getToolkit() {
		return this.toolkit;
	}

	public Game.Viewport getViewport() {
		return this.viewport;
	}
}
