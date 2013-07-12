package org.powerbot.bot.nloader;

public interface Bridge {
	public void classLoader(ClassLoader loader);

	public byte[] classDefined(ClassLoader loader, byte[] bytes);

	public void entry(String name);
}
