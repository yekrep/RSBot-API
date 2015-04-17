package org.powerbot.bot.rt6.loader;

public interface Bridge {
	void classLoader(ClassLoader loader);

	byte[] classDefined(byte[] bytes);

	void entry(String name);

	void end();

	void instance(Object object);
}
