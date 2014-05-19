package org.powerbot.bot.rt6.client;

import java.awt.Canvas;
import java.awt.Rectangle;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class Client extends ContextAccessor implements org.powerbot.script.Client {
	public Client(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getLoopCycle() {
		return engine.accessInt(this);
	}

	public int getPlane() {
		return engine.accessInt(this);
	}

	public int getDestX() {
		return engine.accessInt(this);
	}

	public int getDestY() {
		return engine.accessInt(this);
	}

	public int getCamPosX() {
		return engine.accessInt(this);
	}

	public int getSubMenuY() {
		return engine.accessInt(this);
	}

	public int getMinimapSettings() {
		return engine.accessInt(this);
	}

	public int getRSPlayerCount() {
		return engine.accessInt(this);
	}

	public boolean isMenuCollapsed() {
		return engine.accessBool(this);
	}

	public int getMenuY() {
		return engine.accessInt(this);
	}

	public int getCamPosZ() {
		return engine.accessInt(this);
	}

	public int getCameraYaw() {
		return engine.accessInt(this);
	}

	public boolean isMenuOpen() {
		return engine.accessBool(this);
	}

	public float getMinimapAngle() {
		return engine.accessFloat(this);
	}

	public int getLoginIndex() {
		return engine.accessInt(this);
	}

	public RSInfo getRSGroundInfo() {
		return new RSInfo(engine, engine.access(this));
	}

	public NodeSubQueue getCollapsedMenuItems() {
		return new NodeSubQueue(engine, engine.access(this));
	}

	public int getMinimapScale() {
		return engine.accessInt(this);
	}

	public RSPlayer[] getRSPlayerArray() {
		return engine.access(this, RSPlayer[].class);//TODO: fix
	}

	public int[] getRSPlayerIndexArray() {
		return engine.access(this, int[].class);
	}

	public PlayerMetaInfo getPlayerMetaInfo() {
		return new PlayerMetaInfo(engine, engine.access(this));
	}

	public int getMenuWidth() {
		return engine.accessInt(this);
	}

	public int getSubMenuWidth() {
		return engine.accessInt(this);
	}

	public RSPlayer getMyRSPlayer() {
		return new RSPlayer(engine, engine.access(this));
	}

	public Rectangle[] getRSInterfaceBoundsArray() {
		return engine.access(this, Rectangle[].class);
	}

	public NodeDeque getMenuItems() {
		return new NodeDeque(engine, engine.access(this));
	}

	public String getCurrentUsername() {
		return engine.access(this, String.class);
	}

	public HashTable getRSItemHashTable() {
		return new HashTable(engine, engine.access(this));
	}

	public HashTable getRSInterfaceNC() {
		return new HashTable(engine, engine.access(this));
	}

	public int getRSNPCCount() {
		return engine.accessInt(this);
	}

	public int getMenuX() {
		return engine.accessInt(this);
	}

	public int getGUIRSInterfaceIndex() {
		return engine.accessInt(this);
	}

	public String getSelectedItemName() {
		return engine.access(this, String.class);
	}

	public HashTable getRSNPCNC() {
		return new HashTable(engine, engine.access(this));
	}

	public int getSubMenuX() {
		return engine.accessInt(this);
	}

	public int getMenuHeight() {
		return engine.accessInt(this);
	}

	public int getCamPosY() {
		return engine.accessInt(this);
	}

	public int getCameraPitch() {
		return engine.accessInt(this);
	}

	public boolean isSpellSelected() {
		return engine.accessBool(this);
	}

	public int[] getRSNPCIndexArray() {
		return engine.access(this, int[].class);
	}

	public RSItemDefLoader getRSItemDefLoader() {
		return new RSItemDefLoader(engine, engine.access(this));
	}

	public int getMinimapOffset() {
		return engine.accessInt(this);
	}

	public RSInterfaceBase[] getRSInterfaceCache() {
		return engine.access(this, RSInterfaceBase[].class);//TODO: fix
	}

	public int getCrossHairType() {
		return engine.accessInt(this);
	}

	public RSHintArrow[] getRSHintArrows() {
		return engine.access(this, RSHintArrow[].class);//TODO: fix
	}

	public NodeDeque getProjectileDeque() {
		return new NodeDeque(engine, engine.access(this));
	}

	public Canvas getCanvas() {
		return engine.access(this, Canvas.class);
	}

	public HashTable getItemSlots() {
		return new HashTable(engine, engine.access(this));
	}
}
