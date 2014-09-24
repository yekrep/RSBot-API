package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Client extends ReflectProxy implements org.powerbot.script.Client {
	public Client(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getCameraX() {
		return reflector.accessInt(this);
	}

	public int getCameraY() {
		return reflector.accessInt(this);
	}

	public int getCameraZ() {
		return reflector.accessInt(this);
	}

	public int getCameraYaw() {
		return reflector.accessInt(this);
	}

	public int getCameraPitch() {
		return reflector.accessInt(this);
	}

	public int getMinimapAngle() {
		return reflector.accessInt(this);
	}

	public int getMinimapOffset() {
		return reflector.accessInt(this);
	}

	public int getMinimapScale() {
		return reflector.accessInt(this);
	}

	public Player getPlayer() {
		return new Player(reflector, reflector.access(this));
	}

	public Player[] getPlayers() {
		final Object[] arr = reflector.access(this, Object[].class);
		final Player[] arr2 = arr != null ? new Player[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Player(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public int[] getPlayerIndices() {
		return reflector.accessInts(this);
	}

	public Npc[] getNpcs() {
		final Object[] arr = reflector.access(this, Object[].class);
		final Npc[] arr2 = arr != null ? new Npc[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Npc(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public int[] getNpcIndices() {
		return reflector.accessInts(this);
	}

	public int getOffsetX() {
		return reflector.accessInt(this);
	}

	public int getOffsetY() {
		return reflector.accessInt(this);
	}

	public int getFloor() {
		return reflector.accessInt(this);
	}

	public Landscape getLandscape() {
		return new Landscape(reflector, reflector.access(this));
	}

	public byte[][][] getLandscapeMeta() {
		return reflector.access(this, byte[][][].class);
	}

	public int[][][] getTileHeights() {
		return reflector.access(this, int[][][].class);
	}

	public boolean isMenuOpen() {
		return reflector.accessBool(this);
	}

	public int getMenuX() {
		return reflector.accessInt(this);
	}

	public int getMenuY() {
		return reflector.accessInt(this);
	}

	public int getMenuWidth() {
		return reflector.accessInt(this);
	}

	public int getMenuHeight() {
		return reflector.accessInt(this);
	}

	public int getMenuCount() {
		return reflector.accessInt(this);
	}

	public String[] getMenuActions() {
		return reflector.access(this, String[].class);
	}

	public String[] getMenuOptions() {
		return reflector.access(this, String[].class);
	}

	public int[] getWidgetBoundsX() {
		return reflector.accessInts(this);
	}

	public int[] getWidgetBoundsY() {
		return reflector.accessInts(this);
	}

	public int[] getWidgetBoundsWidth() {
		return reflector.accessInts(this);
	}

	public int[] getWidgetBoundsHeight() {
		return reflector.accessInts(this);
	}

	public int getDestinationX() {
		return reflector.accessInt(this);
	}

	public int getDestinationY() {
		return reflector.accessInt(this);
	}

	public Widget[][] getWidgets() {
		final Object[][] arr = reflector.access(this, Object[][].class);
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
				sub2[i2] = new Widget(reflector, sub[i2]);
			}
		}
		return arr2;
	}

	public HashTable getWidgetTable() {
		return new HashTable(reflector, reflector.access(this));
	}

	public NodeDeque[][][] getGroundItems() {
		final Object[][][] arr = reflector.access(this, Object[][][].class);
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
					sub2_2[i3] = new NodeDeque(reflector, sub2_1[i3]);
				}
			}
		}
		return arr2;
	}

	public CollisionMap[] getCollisionMaps() {
		final Object[] arr = reflector.access(this, Object[].class);
		final CollisionMap[] arr2 = arr != null ? new CollisionMap[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new CollisionMap(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public int[] getVarpbits() {
		return reflector.accessInts(this);
	}

	public int getClientState() {
		return reflector.accessInt(this);
	}

	public int getCrosshairIndex() {
		return reflector.accessInt(this);
	}

	public Cache getVarbitCache() {
		return new Cache(reflector, reflector.access(this));
	}

	public Cache getNpcConfigCache() {
		return new Cache(reflector, reflector.access(this));
	}

	public Cache getObjectConfigCache() {
		return new Cache(reflector, reflector.access(this));
	}

	public Cache getItemConfigCache() {
		return new Cache(reflector, reflector.access(this));
	}

	public int[] getSkillLevels1() {
		return reflector.accessInts(this);
	}

	public int[] getSkillLevels2() {
		return reflector.accessInts(this);
	}

	public int[] getSkillExps() {
		return reflector.accessInts(this);
	}

	public int getCycle() {
		return reflector.accessInt(this);
	}

	public int getHintArrowNpcUid() {
		return reflector.accessInt(this);
	}

	public int getHintArrowPlayerUid() {
		return reflector.accessInt(this);
	}

	public int getHintArrowType() {
		return reflector.accessInt(this);
	}

	public int getHintArrowX() {
		return reflector.accessInt(this);
	}

	public int getHintArrowY() {
		return reflector.accessInt(this);
	}

	public int getSelectionType() {
		return reflector.accessInt(this);
	}

	public int getSelectionIndex() {
		return reflector.accessInt(this);
	}

	public String getUsername() {
		return reflector.accessString(this);
	}

	public String getPassword() {
		return reflector.accessString(this);
	}

	public int getPlayerIndex() {
		return reflector.accessInt(this);
	}

	public int getRunPercentage() {
		return reflector.accessInt(this);
	}
}
