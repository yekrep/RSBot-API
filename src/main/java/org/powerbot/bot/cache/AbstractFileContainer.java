package org.powerbot.bot.cache;

public abstract class AbstractFileContainer {

	public abstract byte[] unpack();

	public abstract int getCRC();
}
