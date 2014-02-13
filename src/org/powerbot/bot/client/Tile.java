package org.powerbot.bot.client;

public interface Tile {
	public ItemPile getItemPile();

	public BoundaryObject getBoundaryObject();

	public WallObject getWallObject();

	public FloorObject getFloorObject();

	public GameObject[] getGameObjects();

	public int getGameObjectLength();
}
