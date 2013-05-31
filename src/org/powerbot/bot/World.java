package org.powerbot.bot;

import org.powerbot.client.Client;

public class World {
	private static final World EMPTY = new World();
	private Client client;

	public World() {
		this.client = null;
	}

	public static World getWorld() {
		final Bot bot = Bot.getInstance();
		return bot != null ? bot.world : EMPTY;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Client getClient() {
		return client;
	}
}
