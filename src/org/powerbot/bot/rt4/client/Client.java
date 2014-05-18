package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class Client extends ContextAccessor implements org.powerbot.script.Client {
	public Client(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getCameraX() {
		return engine.accessInt(this);
	}

	public int getCameraY() {
		return engine.accessInt(this);
	}

	public int getCameraZ() {
		return engine.accessInt(this);
	}

	public int getCameraYaw() {
		return engine.accessInt(this);
	}

	public int getCameraPitch() {
		return engine.accessInt(this);
	}

	public int getMinimapAngle() {
		return engine.accessInt(this);
	}

	public int getMinimapOffset() {
		return engine.accessInt(this);
	}

	public int getMinimapScale() {
		return engine.accessInt(this);
	}

	public Player getPlayer() {
		return new Player(engine, engine.access(this));
	}

	public Player[] getPlayers() {
		return engine.access(this, Player[].class);
	}

	public int[] getPlayerIndices() {
		return engine.access(this, int[].class);
	}

	public Npc[] getNpcs() {
		return engine.access(this, Npc[].class);
	}

	public int[] getNpcIndices() {
		return engine.access(this, int[].class);
	}

	public int getOffsetX() {
		return engine.accessInt(this);
	}

	public int getOffsetY() {
		return engine.accessInt(this);
	}

	public int getFloor() {
		return engine.accessInt(this);
	}

	public Landscape getLandscape() {
		return new Landscape(engine, engine.access(this));
	}

	public byte[][][] getLandscapeMeta() {
		return engine.access(this, byte[][][].class);
	}

	public int[][][] getTileHeights() {
		return engine.access(this, int[][][].class);
	}

	public boolean isMenuOpen() {
		return engine.accessBool(this);
	}

	public int getMenuX() {
		return engine.accessInt(this);
	}

	public int getMenuY() {
		return engine.accessInt(this);
	}

	public int getMenuWidth() {
		return engine.accessInt(this);
	}

	public int getMenuHeight() {
		return engine.accessInt(this);
	}

	public int getMenuCount() {
		return engine.accessInt(this);
	}

	public String[] getMenuActions() {
		return engine.access(this, String[].class);
	}

	public String[] getMenuOptions() {
		return engine.access(this, String[].class);
	}

	public int[] getWidgetBoundsX() {
		return engine.access(this, int[].class);
	}

	public int[] getWidgetBoundsY() {
		return engine.access(this, int[].class);
	}

	public int[] getWidgetBoundsWidth() {
		return engine.access(this, int[].class);
	}

	public int[] getWidgetBoundsHeight() {
		return engine.access(this, int[].class);
	}

	public int getDestinationX() {
		return engine.accessInt(this);
	}

	public int getDestinationY() {
		return engine.accessInt(this);
	}

	public Widget[][] getWidgets() {
		return engine.access(this, Widget[][].class);
	}

	public HashTable getWidgetTable() {
		return new HashTable(engine, engine.access(this));
	}

	public NodeDeque[][][] getGroundItems() {
		return engine.access(this, NodeDeque[][][].class);
	}

	public CollisionMap[] getCollisionMaps() {
		return engine.access(this, CollisionMap[].class);
	}

	public int[] getVarpbits() {
		return engine.access(this, int[].class);
	}

	public int getClientState() {
		return engine.accessInt(this);
	}

	public int getCrosshairIndex() {
		return engine.accessInt(this);
	}

	public Cache getVarbitCache() {
		return new Cache(engine, engine.access(this));
	}

	public Cache getNpcConfigCache() {
		return new Cache(engine, engine.access(this));
	}

	public Cache getObjectConfigCache() {
		return new Cache(engine, engine.access(this));
	}

	public Cache getItemConfigCache() {
		return new Cache(engine, engine.access(this));
	}

	public int[] getSkillLevels1() {
		return engine.access(this, int[].class);
	}

	public int[] getSkillLevels2() {
		return engine.access(this, int[].class);
	}

	public int[] getSkillExps() {
		return engine.access(this, int[].class);
	}

	public int getCycle() {
		return engine.accessInt(this);
	}

	public int getHintArrowNpcUid() {
		return engine.accessInt(this);
	}

	public int getHintArrowPlayerUid() {
		return engine.accessInt(this);
	}

	public int getHintArrowType() {
		return engine.accessInt(this);
	}

	public int getHintArrowX() {
		return engine.accessInt(this);
	}

	public int getHintArrowY() {
		return engine.accessInt(this);
	}

	public int getSelectionType() {
		return engine.accessInt(this);
	}

	public int getSelectionIndex() {
		return engine.accessInt(this);
	}

	public String getUsername() {
		return engine.access(this, String.class);
	}

	public String getPassword() {
		return engine.access(this, String.class);
	}

	public int getPlayerIndex() {
		return engine.accessInt(this);
	}
}
