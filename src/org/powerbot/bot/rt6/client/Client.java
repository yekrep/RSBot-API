package org.powerbot.bot.rt6.client;

import java.awt.Canvas;
import java.awt.Rectangle;

public interface Client extends org.powerbot.script.Client {
	public Callback getCallback();

	public int getLoopCycle();

	public int getPlane();

	public int getDestX();

	public int getDestY();

	public int getCamPosX();

	public int getSubMenuY();

	public int getMinimapSettings();

	public int getRSPlayerCount();

	public boolean isMenuCollapsed();

	public int getMenuY();

	public int getCamPosZ();

	public int getCameraYaw();

	public boolean isMenuOpen();

	public float getMinimapAngle();

	public int getLoginIndex();

	public RSInfo getRSGroundInfo();

	public NodeSubQueue getCollapsedMenuItems();

	public int getMinimapScale();

	public RSPlayer[] getRSPlayerArray();

	public int[] getRSPlayerIndexArray();

	public PlayerMetaInfo getPlayerMetaInfo();

	public int getMenuWidth();

	public int getSubMenuWidth();

	public RSPlayer getMyRSPlayer();

	public Rectangle[] getRSInterfaceBoundsArray();

	public NodeDeque getMenuItems();

	public String getUsername();

	public String getPassword();

	public HashTable getRSItemHashTable();

	public HashTable getRSInterfaceNC();

	public int getRSNPCCount();

	public int getMenuX();

	public int getGUIRSInterfaceIndex();

	public String getSelectedItemName();

	public HashTable getRSNPCNC();

	public int getSubMenuX();

	public int getMenuHeight();

	public int getCamPosY();

	public int getCameraPitch();

	public boolean isSpellSelected();

	public int[] getRSNPCIndexArray();

	public Bundler getItemBundler();

	public Bundler getNPCBundler();

	public int getMinimapOffset();

	public RSInterfaceBase[] getRSInterfaceCache();

	public int getCrossHairType();

	public RSHintArrow[] getRSHintArrows();

	public NodeDeque getProjectileDeque();

	public void setCallback(Callback callback);

	public Canvas getCanvas();

	public HashTable getItemSlots();
}
