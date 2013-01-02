package org.powerbot.game.client;

public interface RSInfo {
	public RSGroundBytes getGroundBytes();

	public RSGroundData[] getGroundData();

	public BaseInfo getBaseInfo();

	public RSGroundInfo getRSGroundInfo();

	public RSObjectDefLoader getRSObjectDefLoaders();
}
