package org.powerbot.os.client;

public interface Client {
	public int getCameraX();

	public int getCameraY();

	public int getCameraZ();

	public int getCameraYaw();

	public int getCameraPitch();

	public int getMinimapAngle();

	public int getMinimapOffset();

	public int getMinimapScale();

	public Player getPlayer();

	public Player[] getPlayers();

	public int[] getPlayerIndicies();

	public Npc[] getNpcs();

	public int[] getNpcIndicies();

	public int getOffsetX();

	public int getOffsetY();

	public int getFloor();

	public Landscape getLandscape();

	public byte[][][] getLandscapeMeta();

	public int[][][] getTileHeights();

	public int getMenuX();

	public int getMenuY();

	public int getMenuWidth();

	public int getMenuHeight();

	public int getMenuCount();

	public String[] getMenuActions();

	public String[] getMenuOptions();

	public int[] getWidgetBoundsX();

	public int[] getWidgetBoundsY();

	public int[] getWidgetBoundsWidth();

	public int[] getWidgetBoundsHeight();

	public int getDestinationX();

	public int getDestinationY();

	public Widget[][] getWidgets();

	public HashTable getWidgetTable();

	public NodeDeque getGroundItems();

	public int[] getVarpbits();
}
