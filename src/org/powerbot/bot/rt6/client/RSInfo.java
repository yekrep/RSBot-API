package org.powerbot.bot.rt6.client;

public interface RSInfo {
	RSGroundBytes getGroundBytes();

	BaseInfo getBaseInfo();

	RSGroundInfo getRSGroundInfo();

	Bundler getSceneryBundler();
}
