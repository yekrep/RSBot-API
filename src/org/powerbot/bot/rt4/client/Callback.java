package org.powerbot.bot.rt4.client;

public interface Callback {
	void onMessage(int type, String sender, String message);

	void notifyConfig(final Object object);
}
