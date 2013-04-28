package org.powerbot.script.xenon.wrappers;

import java.awt.Point;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSGroundData;
import org.powerbot.game.client.RSInfo;

public class CollisionMap {
	private final int plane;

	public CollisionMap(final int plane) {
		this.plane = plane;
	}

	public Point getPosition() {
		final Client client = Bot.client();
		if (client == null) return null;
		final RSInfo info = client.getRSGroundInfo();
		final RSGroundData[] grounds;
		RSGroundData ground = null;
		if (info != null && (grounds = info.getGroundData()) != null && plane < grounds.length) ground = grounds[plane];
		return ground != null ? new Point(ground.getX(), ground.getY()) : null;
	}

	public int[][] getMeta() {
		final Client client = Bot.client();
		if (client == null) return null;
		final RSInfo info = client.getRSGroundInfo();
		final RSGroundData[] grounds;
		RSGroundData ground = null;
		if (info != null && (grounds = info.getGroundData()) != null && plane < grounds.length) ground = grounds[plane];
		return ground != null ? ground.getBlocks() : null;
	}
}
