package org.powerbot.bot.rt6.client;

import java.awt.Canvas;
import java.awt.Rectangle;

public interface Client extends org.powerbot.script.Client {
	Callback getCallback();

	int getLoopCycle();

	int getPlane();

	int getDestX();

	int getDestY();

	int getCamPosX();

	int getSubMenuY();

	int getMinimapSettings();

	int getRSPlayerCount();

	boolean isMenuCollapsed();

	int getMenuY();

	int getCamPosZ();

	int getCameraYaw();

	boolean isMenuOpen();

	float getMinimapAngle();

	int getLoginIndex();

	RSInfo getRSGroundInfo();

	NodeSubQueue getCollapsedMenuItems();

	int getMinimapScale();

	RSPlayer[] getRSPlayerArray();

	int[] getRSPlayerIndexArray();

	PlayerMetaInfo getPlayerMetaInfo();

	int getMenuWidth();

	int getSubMenuWidth();

	RSPlayer getMyRSPlayer();

	Rectangle[] getRSInterfaceBoundsArray();

	NodeDeque getMenuItems();

	String getUsername();

	//public String getPassword();

	HashTable getRSItemHashTable();

	HashTable getRSInterfaceNC();

	int getRSNPCCount();

	int getMenuX();

	int getGUIRSInterfaceIndex();

	String getSelectedItemName();

	HashTable getRSNPCNC();

	int getSubMenuX();

	int getMenuHeight();

	int getCamPosY();

	int getCameraPitch();

	boolean isSpellSelected();

	int[] getRSNPCIndexArray();

	Bundler getItemBundler();

	Bundler getNPCBundler();

	int getMinimapOffset();

	RSInterfaceBase[] getRSInterfaceCache();

	int getCrossHairType();

	RSHintArrow[] getRSHintArrows();

	NodeDeque getProjectileDeque();

	void setCallback(Callback callback);

	Canvas getCanvas();

	HashTable getItemSlots();
}
