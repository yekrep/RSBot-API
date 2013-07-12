package org.powerbot.bot.nloader;

public interface Bridge {
	public byte[] classDefined(byte[] bytes);

	public void entry(String name);
}
