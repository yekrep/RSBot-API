package org.powerbot.game.client;

import java.awt.Canvas;
import java.awt.Rectangle;

import org.powerbot.game.client.input.Mouse;

public interface Client {
	public void setCallback(Callback cb);

	public int getMenuOptionsCount();

	public int getMinimapScale();

	public Object getMenuItems();

	public int getRSPlayerCount();

	public int getSubMenuWidth();

	public Object getMyRSPlayer();

	public Object getCurrentMenuGroupNode();

	public String getSelectedItemName();

	public int[] getSkillExperiences();

	public int getPlane();

	public int[] getSkillLevelMaxes();

	public int getDestX();

	public boolean isMenuOpen();

	public Object getRSItemDefLoader();

	public int getCamPosZ();

	public int getGUIRSInterfaceIndex();

	public int getMenuX();

	public String getCurrentPassword();

	public int getCameraPitch();

	public int getMinimapOffset();

	public int getSubMenuX();

	public int getMenuWidth();

	public int getLoginIndex();

	public Object getSettingArray();

	public Object getDetailInfoNode();

	public int isItemSelected();

	public int getLoopCycle();

	public int[] getSkillLevels();

	public int getMenuOptionsCountCollapsed();

	public Object getRSInterfaceNC();

	public int getPlayerCount();

	public float getMinimapAngle();

	public Object[] getRSPlayerArray();

	public int getCameraYaw();

	public Object getRSItemHashTable();

	public int getMenuHeight();

	public boolean isSpellSelected();

	public Rectangle[] getRSInterfaceBoundsArray();

	public Object[] getRSInterfaceCache();

	public int getMinimapSetting();

	public int getMenuY();

	public int[] getRSPlayerIndexArray();

	public boolean isMenuCollapsed();

	public int getRSNPCCount();

	public Object getRSNPCNC();

	public int getCamPosY();

	public String getCurrentUsername();

	public Mouse getMouse();

	public int[] getRSNPCIndexArray();

	public Callback getCallback();

	public int getCamPosX();

	public int getDestY();

	public int getSubMenuY();

	public Canvas getCanvas();

	public int[] getSkillExperienceMaxes();

	public boolean[] getValidRSInterfaceArray();

	public Object getCollapsedMenuItems();
}