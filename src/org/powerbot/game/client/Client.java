package org.powerbot.game.client;

import org.powerbot.game.client.input.Mouse;

import java.awt.*;

public interface Client {
	public int getSubMenuY();

	public int getCamPosY();

	public int getMenuOptionsCountCollapsed();

	public int getCamPosX();

	public int getCamPosZ();

	public int getMinimapSetting();

	public Object getDetailInfoNode();

	public int[] getRSNPCIndexArray();

	public boolean isSpellSelected();

	public Object getMyRSPlayer();

	public boolean isMenuOpen();

	public Object getCurrentMenuGroupNode();

	public int[] getSkillLevelMaxes();

	public Object[] getRSInterfaceCache();

	public int getPlane();

	public int[] getSkillLevels();

	public Object getMenuItems();

	public Mouse getMouse();

	public Object getCollapsedMenuItems();

	public Object getRSItemHashTable();

	public String getSelectedItemName();

	public int getMenuOptionsCount();

	public int getLoopCycle();

	public int getDestY();

	public int[] getRSPlayerIndexArray();

	public boolean[] getValidRSInterfaceArray();

	public int getMenuHeight();

	public int[] getSkillExperienceMaxes();

	public int getDestX();

	public Callback getCallback();

	public Object getRSNPCNC();

	public int getSubMenuX();

	public int getCameraYaw();

	public int isItemSelected();

	public int[] getSkillExperiences();

	public Object[] getRSPlayerArray();

	public int getMenuY();

	public Object getRSInterfaceNC();

	public String getCurrentUsername();

	public int getCameraPitch();

	public double getMinimapAngle();

	public int getLoginIndex();

	public Rectangle[] getRSInterfaceBoundsArray();

	public boolean isMenuCollapsed();

	public int getRSPlayerCount();

	public Object getRSItemDefLoader();

	public int getMenuWidth();

	public int getSubMenuWidth();

	public int getMinimapScale();

	public Object getSettingArray();

	public int getRSNPCCount();

	public int getMenuX();

	public int getGUIRSInterfaceIndex();

	public int getPlayerCount();

	public int getMinimapOffset();

	public String getCurrentPassword();

	public Canvas getCanvas();

	public int getRSObjectID(Object object);

	public void setCallback(Callback callback);

	public Model getRSObjectModel(Object object);
}