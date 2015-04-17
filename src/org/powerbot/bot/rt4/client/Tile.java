package org.powerbot.bot.rt4.client;

public interface Tile {
	ItemPile getItemPile();

	BoundaryObject getBoundaryObject();

	WallObject getWallObject();

	FloorObject getFloorObject();

	GameObject[] getGameObjects();

	int getGameObjectLength();
}
