package org.powerbot.game.client;

import java.awt.Canvas;
import java.awt.Rectangle;

import org.powerbot.game.client.input.Keyboard;
import org.powerbot.game.client.input.Mouse;

public interface Client {
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

	public Mouse getMouse();

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

	public String getCurrentUsername();

	public HashTable getRSItemHashTable();

	public HashTable getRSInterfaceNC();

	public int getRSNPCCount();

	public int getMenuX();

	public int getGUIRSInterfaceIndex();

	public Keyboard getKeyboard();

	public String getSelectedItemName();

	public HashTable getRSNPCNC();

	public int getSubMenuX();

	public int getMenuHeight();

	public int getCamPosY();

	public int getCameraPitch();

	public boolean isSpellSelected();

	public int[] getRSNPCIndexArray();

	public RSItemDefLoader getRSItemDefLoader();

	public int getMinimapOffset();

	public RSInterfaceBase[] getRSInterfaceCache();

	public void setCallback(Callback callback);

	public Canvas getCanvas();
}
