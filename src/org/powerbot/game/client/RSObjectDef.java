package org.powerbot.game.client;

public interface RSObjectDef {
	public String getName();

	public String[] getActions();

	public RSObjectDefLoader getLoader();

	public int getID();
}
