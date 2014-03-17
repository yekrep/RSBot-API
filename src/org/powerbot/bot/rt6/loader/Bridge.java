package org.powerbot.bot.rt6.loader;

public interface Bridge {
	public void classLoader(ClassLoader loader);

	public byte[] classDefined(byte[] bytes);

	public void entry(String name);

	public void end();

	public void instance(Object object);
}
