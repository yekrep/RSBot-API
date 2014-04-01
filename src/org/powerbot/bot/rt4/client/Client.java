package org.powerbot.bot.rt4.client;

public interface Client extends org.powerbot.script.Client {
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

	public int[] getPlayerIndices();

	public Npc[] getNpcs();

	public int[] getNpcIndices();

	public int getOffsetX();

	public int getOffsetY();

	public int getFloor();

	public Landscape getLandscape();

	public byte[][][] getLandscapeMeta();

	public int[][][] getTileHeights();

	public boolean isMenuOpen();

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

	public NodeDeque[][][] getGroundItems();

	public CollisionMap[] getCollisionMaps();

	public int[] getVarpbits();

	public int getClientState();

	public int getCrosshairIndex();

	public MRUCache getVarbitCache();

	public MRUCache getNpcConfigCache();

	public MRUCache getObjectConfigCache();

	public MRUCache getItemConfigCache();

	public int[] getSkillLevels1();

	public int[] getSkillLevels2();

	public int[] getSkillExps();

	public int getCycleCount();

	public int getHintArrowNpcUid();

	public int getHintArrowPlayerUid();

	public int getHintArrowType();

	public int getHintArrowX();

	public int getHintArrowY();
}
