package org.powerbot.os.api;

import org.powerbot.os.client.Client;

import java.util.concurrent.atomic.AtomicReference;

public class MethodContext {
	private final AtomicReference<Client> client;

	public MethodContext(final Client client) {
		this.client = new AtomicReference<Client>(client);
	}

	public Client getClient() {
		return client.get();
	}
}
