package org.powerbot.bot.rt4.client;

public interface Client extends org.powerbot.script.Client {
	int getCameraX();

	int getCameraY();

	int getCameraZ();

	int getCameraYaw();

	int getCameraPitch();

	int getMinimapAngle();

	int getMinimapOffset();

	int getMinimapScale();

	Player getPlayer();

	Player[] getPlayers();

	int[] getPlayerIndices();

	Npc[] getNpcs();

	int[] getNpcIndices();

	int getOffsetX();

	int getOffsetY();

	int getFloor();

	Landscape getLandscape();

	byte[][][] getLandscapeMeta();

	int[][][] getTileHeights();

	boolean isMenuOpen();

	int getMenuX();

	int getMenuY();

	int getMenuWidth();

	int getMenuHeight();

	int getMenuCount();

	String[] getMenuActions();

	String[] getMenuOptions();

	int[] getWidgetBoundsX();

	int[] getWidgetBoundsY();

	int[] getWidgetBoundsWidth();

	int[] getWidgetBoundsHeight();

	int getDestinationX();

	int getDestinationY();

	Widget[][] getWidgets();

	HashTable getWidgetTable();

	NodeDeque[][][] getGroundItems();

	CollisionMap[] getCollisionMaps();

	int[] getVarpbits();

	int getClientState();

	int getCrosshairIndex();

	Cache getVarbitCache();

	Cache getNpcConfigCache();

	Cache getObjectConfigCache();

	Cache getItemConfigCache();

	int[] getSkillLevels1();

	int[] getSkillLevels2();

	int[] getSkillExps();

	int getCycle();

	int getHintArrowNpcUid();

	int getHintArrowPlayerUid();

	int getHintArrowType();

	int getHintArrowX();

	int getHintArrowY();

	void setCallback(Callback callback);

	String getUsername();

	//String getPassword();

	int getSelectionType();

	int getSelectionIndex();

	int getPlayerIndex();

	int getRunPercentage();

	int getLoginState();

	int getLoginField();

	boolean isWorldSelectionUp();
}
