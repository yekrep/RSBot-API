package org.powerbot.bot.rt6.client;

import java.awt.Canvas;
import java.awt.Rectangle;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Client extends ReflectProxy implements org.powerbot.script.Client {
	public Client(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getLoopCycle() {
		return reflector.accessInt(this);
	}

	public int getPlane() {
		return reflector.accessInt(this);
	}

	public int getDestX() {
		return reflector.accessInt(this);
	}

	public int getDestY() {
		return reflector.accessInt(this);
	}

	public int getCamPosX() {
		return reflector.accessInt(this);
	}

	public int getSubMenuY() {
		return reflector.accessInt(this);
	}

	public int getMinimapSettings() {
		return reflector.accessInt(this);
	}

	public int getRSPlayerCount() {
		return reflector.accessInt(this);
	}

	public boolean isMenuCollapsed() {
		return reflector.accessBool(this);
	}

	public int getMenuY() {
		return reflector.accessInt(this);
	}

	public int getCamPosZ() {
		return reflector.accessInt(this);
	}

	public int getCameraYaw() {
		return reflector.accessInt(this);
	}

	public boolean isMenuOpen() {
		return reflector.accessBool(this);
	}

	public float getMinimapAngle() {
		return reflector.accessFloat(this);
	}

	public int getLoginIndex() {
		return reflector.accessInt(this);
	}

	public RSInfo getRSGroundInfo() {
		return new RSInfo(reflector, reflector.access(this));
	}

	public NodeSubQueue getCollapsedMenuItems() {
		return new NodeSubQueue(reflector, reflector.access(this));
	}

	public int getMinimapScale() {
		return reflector.accessInt(this);
	}

	public RSPlayer[] getRSPlayerArray() {
		final Object[] arr = reflector.access(this, Object[].class);
		final RSPlayer[] arr2 = arr != null ? new RSPlayer[arr.length] : new RSPlayer[0];
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new RSPlayer(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public int[] getRSPlayerIndexArray() {
		return reflector.accessInts(this);
	}

	public PlayerMetaInfo getPlayerMetaInfo() {
		return new PlayerMetaInfo(reflector, reflector.access(this));
	}

	public int getMenuWidth() {
		return reflector.accessInt(this);
	}

	public int getSubMenuWidth() {
		return reflector.accessInt(this);
	}

	public RSPlayer getMyRSPlayer() {
		return new RSPlayer(reflector, reflector.access(this));
	}

	public Rectangle[] getRSInterfaceBoundsArray() {
		return reflector.access(this, Rectangle[].class);
	}

	public NodeDeque getMenuItems() {
		return new NodeDeque(reflector, reflector.access(this));
	}

	public String getCurrentUsername() {
		return reflector.accessString(this);
	}

	public HashTable getRSItemHashTable() {
		return new HashTable(reflector, reflector.access(this));
	}

	public HashTable getRSInterfaceNC() {
		return new HashTable(reflector, reflector.access(this));
	}

	public int getRSNPCCount() {
		return reflector.accessInt(this);
	}

	public int getMenuX() {
		return reflector.accessInt(this);
	}

	public int getGUIRSInterfaceIndex() {
		return reflector.accessInt(this);
	}

	public String getSelectedItemName() {
		return reflector.accessString(this);
	}

	public HashTable getRSNPCNC() {
		return new HashTable(reflector, reflector.access(this));
	}

	public int getSubMenuX() {
		return reflector.accessInt(this);
	}

	public int getMenuHeight() {
		return reflector.accessInt(this);
	}

	public int getCamPosY() {
		return reflector.accessInt(this);
	}

	public int getCameraPitch() {
		return reflector.accessInt(this);
	}

	public boolean isSpellSelected() {
		return reflector.accessBool(this);
	}

	public int[] getRSNPCIndexArray() {
		return reflector.accessInts(this);
	}

	public RSItemDefLoader getRSItemDefLoader() {
		return new RSItemDefLoader(reflector, reflector.access(this));
	}

	public int getMinimapOffset() {
		return reflector.accessInt(this);
	}

	public RSInterfaceBase[] getRSInterfaceCache() {
		final Object[] arr = reflector.access(this, Object[].class);
		final RSInterfaceBase[] arr2 = arr != null ? new RSInterfaceBase[arr.length] : new RSInterfaceBase[0];
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new RSInterfaceBase(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public int getCrossHairType() {
		return reflector.accessInt(this);
	}

	public RSHintArrow[] getRSHintArrows() {
		final Object[] arr = reflector.access(this, Object[].class);
		final RSHintArrow[] arr2 = arr != null ? new RSHintArrow[arr.length] : new RSHintArrow[0];
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new RSHintArrow(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public NodeDeque getProjectileDeque() {
		return new NodeDeque(reflector, reflector.access(this));
	}

	public Canvas getCanvas() {
		return reflector.access(this, Canvas.class);
	}

	public HashTable getItemSlots() {
		return new HashTable(reflector, reflector.access(this));
	}
}
