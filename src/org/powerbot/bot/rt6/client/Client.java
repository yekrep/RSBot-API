package org.powerbot.bot.rt6.client;

import java.awt.Canvas;
import java.awt.Rectangle;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;
import org.powerbot.script.ClientContext;

public class Client extends ReflectProxy implements org.powerbot.script.Client {
	public Client(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public TransformMatrix getViewMatrix() {
		return new TransformMatrix(reflector, reflector.access(this));
	}

	public Matrix4f getProjMatrix() {
		return new Matrix4f(reflector, reflector.access(this));
	}

	public int getCycle() {
		return reflector.accessInt(this);
	}

	public int getFloor() {
		return reflector.accessInt(this);
	}

	public int getDestinationX() {
		return reflector.accessInt(this);
	}

	public int getDestinationY() {
		return reflector.accessInt(this);
	}

	public int getCameraX() {
		return reflector.accessInt(this);
	}

	public int getSubMenuY() {
		return reflector.accessInt(this);
	}

	public int getMinimapSettings() {
		return reflector.accessInt(this);
	}

	public int getPlayerCount() {
		return reflector.accessInt(this);
	}

	public boolean isMenuCollapsed() {
		return reflector.accessBool(this);
	}

	public int getMenuY() {
		return reflector.accessInt(this);
	}

	public int getCameraZ() {
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

	public World getWorld() {
		return new World(reflector, reflector.access(this));
	}

	public NodeSubQueue getCollapsedMenuItems() {
		return new NodeSubQueue(reflector, reflector.access(this));
	}

	public int getMinimapScale() {
		return reflector.accessInt(this);
	}

	public Player[] getPlayers() {
		final Object[] arr = reflector.access(this, Object[].class);
		final Player[] arr2 = arr != null ? new Player[arr.length] : new Player[0];
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

	public PlayerFacade getPlayerFacade() {
		return new PlayerFacade(reflector, reflector.access(this));
	}

	public int getMenuWidth() {
		return reflector.accessInt(this);
	}

	public int getSubMenuWidth() {
		return reflector.accessInt(this);
	}

	public Player getPlayer() {
		return new Player(reflector, reflector.access(this));
	}

	public Rectangle[] getWidgetBoundsArray() {
		return reflector.access(this, Rectangle[].class);
	}

	public NodeDeque getMenuItems() {
		return new NodeDeque(reflector, reflector.access(this));
	}

	public String getCurrentUsername() {
		return reflector.accessString(this);
	}

	public String getCurrentPassword() {
		return reflector.accessString(this);
	}

	public HashTable getItemTable() {
		return new HashTable(reflector, reflector.access(this));
	}

	public HashTable getWidgetTable() {
		return new HashTable(reflector, reflector.access(this));
	}

	public int getNpcCount() {
		return reflector.accessInt(this);
	}

	public int getMenuX() {
		return reflector.accessInt(this);
	}

	public int getWidgetIndex() {
		return reflector.accessInt(this);
	}

	public String getSelectedItemName() {
		return reflector.accessString(this);
	}

	public HashTable getNpcTable() {
		return new HashTable(reflector, reflector.access(this));
	}

	public int getSubMenuX() {
		return reflector.accessInt(this);
	}

	public int getMenuHeight() {
		return reflector.accessInt(this);
	}

	public int getCameraY() {
		return reflector.accessInt(this);
	}

	public int getCameraPitch() {
		return reflector.accessInt(this);
	}

	public boolean isSpellSelected() {
		return reflector.accessBool(this);
	}

	public int[] getNpcIndices() {
		return reflector.accessInts(this);
	}

	public Bundler getItemBundler() {
		return new Bundler(reflector, reflector.access(this));
	}

	public Bundler getNpcBundler() {
		return new Bundler(reflector, reflector.access(this));
	}

	public int getMinimapOffset() {
		return reflector.accessInt(this);
	}

	public ComponentContainer[] getWidgets() {
		final Object[] arr = reflector.access(this, Object[].class);
		final ComponentContainer[] arr2 = arr != null ? new ComponentContainer[arr.length] : new ComponentContainer[0];
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new ComponentContainer(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public int getCrossHairType() {
		return reflector.accessInt(this);
	}

	public HintArrow[] getHintArrows() {
		final Object[] arr = reflector.access(this, Object[].class);
		final HintArrow[] arr2 = arr != null ? new HintArrow[arr.length] : new HintArrow[0];
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new HintArrow(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public NodeDeque getProjectileDeque() {
		return new NodeDeque(reflector, reflector.access(this));
	}

	public Canvas getCanvas() {
		System.getSecurityManager().checkPermission(ClientContext.INTERNAL_API_ACCESS);
		return reflector.access(this, Canvas.class);
	}

	public String getUsername() {
		System.getSecurityManager().checkPermission(ClientContext.INTERNAL_API_ACCESS);
		return reflector.accessString(this);
	}

	public String getPassword() {
		System.getSecurityManager().checkPermission(ClientContext.INTERNAL_API_ACCESS);
		return reflector.accessString(this);
	}

	public HashTable getItemSlots() {
		return new HashTable(reflector, reflector.access(this));
	}

	public NodeSubQueue getLoggerEntries() {
		return new NodeSubQueue(reflector, reflector.access(this));
	}
}
