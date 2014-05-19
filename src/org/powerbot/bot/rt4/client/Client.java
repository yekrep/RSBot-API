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
		final Object[] arr = engine.access(this, Object[].class);
		final Player[] arr2 = arr != null ? new Player[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Player(engine, arr[i]);
			}
		}
		return arr2;
	}

	public int[] getPlayerIndices() {
		return engine.access(this, int[].class);
	}

	public Npc[] getNpcs() {
		final Object[] arr = engine.access(this, Object[].class);
		final Npc[] arr2 = arr != null ? new Npc[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Npc(engine, arr[i]);
			}
		}
		return arr2;
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
		final Object[][] arr = engine.access(this, Object[][].class);
		if (arr == null) {
			return null;
		}
		final Widget[][] arr2 = new Widget[arr.length][];
		for (int i = 0; i < arr.length; i++) {
			final Object[] sub = arr[i];
			if (sub == null) {
				arr2[i] = null;
				continue;
			}
			final Widget[] sub2 = new Widget[sub.length];
			arr2[i] = sub2;
			for (int i2 = 0; i2 < sub.length; i2++) {
				sub2[i2] = new Widget(engine, sub[i2]);
			}
		}
		return arr2;
	}

	public HashTable getWidgetTable() {
		return new HashTable(engine, engine.access(this));
	}

	public NodeDeque[][][] getGroundItems() {
		final Object[][][] arr = engine.access(this, Object[][][].class);
		if (arr == null) {
			return null;
		}
		final NodeDeque[][][] arr2 = new NodeDeque[arr.length][][];
		for (int i = 0; i < arr.length; i++) {
			final Object[][] sub = arr[i];
			if (sub == null) {
				arr2[i] = null;
				continue;
			}
			final NodeDeque[][] sub2 = new NodeDeque[sub.length][];
			arr2[i] = sub2;
			for (int i2 = 0; i2 < sub.length; i2++) {
				final Object[] sub2_1 = sub[i2];
				if (sub2_1 == null) {
					sub2[i] = null;
					continue;
				}
				final NodeDeque[] sub2_2 = new NodeDeque[sub2_1.length];
				sub2[i2] = sub2_2;
				for (int i3 = 0; i3 < sub2_1.length; i3++) {
					sub2_2[i3] = new NodeDeque(engine, sub2_1[i3]);
				}
			}
		}
		return arr2;
	}

	public CollisionMap[] getCollisionMaps() {
		final Object[] arr = engine.access(this, Object[].class);
		final CollisionMap[] arr2 = arr != null ? new CollisionMap[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new CollisionMap(engine, arr[i]);
			}
		}
		return arr2;
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
