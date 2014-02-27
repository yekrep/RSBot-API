package org.powerbot.bot.rs3.client;

public interface RSInfo {
	public RSGroundBytes getGroundBytes();

	public BaseInfo getBaseInfo();

	public RSGroundInfo getRSGroundInfo();

	public RSObjectDefLoader getRSObjectDefLoaders();
}
