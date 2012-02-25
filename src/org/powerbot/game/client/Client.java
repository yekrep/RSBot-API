package org.powerbot.game.client;

public interface Client {
	public Model getRSObjectModel(Object object);

	public void setCallback(Callback callback);

	public int getRSObjectID(Object object);

	public Callback getCallback();
}
