package org.powerbot.bot.rt4.client;

public interface Callback {
	public void onMessage(int type, String sender, String message);

	public void notifyConfig(final Object object);
}
