package org.powerbot.script.lang;

public abstract class ClientAccessor<T extends ClientContext> {
	public final T ctx;

	public ClientAccessor(final T ctx) {
		this.ctx = ctx;
	}
}
