package org.powerbot.bot.nloader;

public interface Bridge {
	public byte[] onDefine(byte[] bytes);

	public void notifyClass(String name);
}
