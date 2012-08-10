package org.powerbot.game.bot.randoms;

import org.powerbot.game.api.AntiRandom;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Tile;

@Manifest(name = "Maze", authors = {"Timer"}, version = 1.0)
public class Maze extends AntiRandom {
	private static final Tile TILE_CENTER = new Tile(2911, 4576, 0);

	@Override
	public boolean validate() {
		return Calculations.distanceTo(TILE_CENTER) < 100 && SceneEntities.getNearest(3626, 3649) != null;
	}

	@Override
	public void run() {
		Time.sleep(Random.nextInt(1000, 2500));
	}
}
