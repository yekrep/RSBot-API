package org.powerbot.bot.rt6.client;

import java.awt.Rectangle;
import java.security.AccessController;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;
import org.powerbot.script.ClientContext;

import javax.security.auth.PrivateCredentialPermission;

public class Client extends ReflectProxy implements org.powerbot.script.Client {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache(),
			e = new Reflector.FieldCache(),
			f = new Reflector.FieldCache(),
			g = new Reflector.FieldCache(),
			h = new Reflector.FieldCache(),
			i = new Reflector.FieldCache(),
			j = new Reflector.FieldCache(),
			k = new Reflector.FieldCache(),
			l = new Reflector.FieldCache(),
			m = new Reflector.FieldCache(),
			n = new Reflector.FieldCache(),
			o = new Reflector.FieldCache(),
			p = new Reflector.FieldCache(),
			q = new Reflector.FieldCache(),
			r = new Reflector.FieldCache(),
			s = new Reflector.FieldCache(),
			t = new Reflector.FieldCache(),
			u = new Reflector.FieldCache(),
			v = new Reflector.FieldCache(),
			w = new Reflector.FieldCache(),
			x = new Reflector.FieldCache(),
			y = new Reflector.FieldCache(),
			z = new Reflector.FieldCache(),
			aa = new Reflector.FieldCache(),
			ab = new Reflector.FieldCache(),
			ac = new Reflector.FieldCache(),
			ad = new Reflector.FieldCache(),
			ae = new Reflector.FieldCache(),
			af = new Reflector.FieldCache(),
			ag = new Reflector.FieldCache(),
			ah = new Reflector.FieldCache(),
			ai = new Reflector.FieldCache(),
			aj = new Reflector.FieldCache(),
			ak = new Reflector.FieldCache(),
			al = new Reflector.FieldCache(),
			am = new Reflector.FieldCache(),
			an = new Reflector.FieldCache(),
			ao = new Reflector.FieldCache(),
			ap = new Reflector.FieldCache(),
			aq = new Reflector.FieldCache(),
			ar = new Reflector.FieldCache(),
			as = new Reflector.FieldCache(),
			at = new Reflector.FieldCache(),
			au = new Reflector.FieldCache(),
			av = new Reflector.FieldCache(),
			aw = new Reflector.FieldCache(),
			ax = new Reflector.FieldCache(),
			ay = new Reflector.FieldCache(),
			az = new Reflector.FieldCache(),
			ba = new Reflector.FieldCache(),
			bb = new Reflector.FieldCache();

	public Client(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public TransformMatrix getViewMatrix() {
		return new TransformMatrix(reflector, reflector.access(this, a));
	}

	public Matrix4f getProjMatrix() {
		return new Matrix4f(reflector, reflector.access(this, b));
	}

	public int getCycle() {
		return reflector.accessInt(this, c);
	}

	public int getFloor() {
		return reflector.accessInt(this, d);
	}

	public int getDestinationX() {
		return reflector.accessInt(this, e);
	}

	public int getDestinationY() {
		return reflector.accessInt(this, f);
	}

	public int getCameraX() {
		return reflector.accessInt(this, g);
	}

	public int getSubMenuY() {
		return reflector.accessInt(this, h);
	}

	public int getMinimapSettings() {
		return reflector.accessInt(this, i);
	}

	public int getPlayerCount() {
		return reflector.accessInt(this, j);
	}

	public boolean isMenuCollapsed() {
		return reflector.accessBool(this, k);
	}

	public int getMenuY() {
		return reflector.accessInt(this, l);
	}

	public int getCameraZ() {
		return reflector.accessInt(this, m);
	}

	public int getCameraYaw() {
		return reflector.accessInt(this, n);
	}

	public boolean isMenuOpen() {
		return reflector.accessBool(this, o);
	}

	public float getMinimapAngle() {
		return reflector.accessFloat(this, p);
	}

	public int getClientState() {
		return reflector.accessInt(this, q);
	}

	public World getWorld() {
		return new World(reflector, reflector.access(this, r));
	}

	public NodeSubQueue getCollapsedMenuItems() {
		return new NodeSubQueue(reflector, reflector.access(this, s));
	}

	public int getMinimapScale() {
		return reflector.accessInt(this, t);
	}

	public Player[] getPlayers() {
		final Object[] arr = reflector.access(this, u, Object[].class);
		final Player[] arr2 = arr != null ? new Player[arr.length] : new Player[0];
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Player(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public int[] getPlayerIndices() {
		return reflector.accessInts(this, v);
	}

	public PlayerFacade getPlayerFacade() {
		return new PlayerFacade(reflector, reflector.access(this, w));
	}

	public int getMenuWidth() {
		return reflector.accessInt(this, x);
	}

	public int getSubMenuWidth() {
		return reflector.accessInt(this, y);
	}

	public Player getPlayer() {
		return new Player(reflector, reflector.access(this, z));
	}

	public Rectangle[] getWidgetBoundsArray() {
		return reflector.access(this, aa, Rectangle[].class);
	}

	public NodeDeque getMenuItems() {
		return new NodeDeque(reflector, reflector.access(this, ab));
	}

	public HashTable getItemTable() {
		return new HashTable(reflector, reflector.access(this, ae));
	}

	public HashTable getWidgetTable() {
		return new HashTable(reflector, reflector.access(this, af));
	}

	public int getNpcCount() {
		return reflector.accessInt(this, ag);
	}

	public int getMenuX() {
		return reflector.accessInt(this, ah);
	}

	public int getWidgetIndex() {
		return reflector.accessInt(this, ai);
	}

	public String getSelectedItemName() {
		return reflector.accessString(this, aj);
	}

	public HashTable getNpcTable() {
		return new HashTable(reflector, reflector.access(this, ak));
	}

	public int getSubMenuX() {
		return reflector.accessInt(this, al);
	}

	public int getMenuHeight() {
		return reflector.accessInt(this, am);
	}

	public int getCameraY() {
		return reflector.accessInt(this, an);
	}

	public int getCameraPitch() {
		return reflector.accessInt(this, ao);
	}

	public boolean isSpellSelected() {
		return reflector.accessBool(this, ap);
	}

	public int[] getNpcIndices() {
		return reflector.accessInts(this, aq);
	}

	public Bundler getItemBundler() {
		return new Bundler(reflector, reflector.access(this, ar));
	}

	public Bundler getNpcBundler() {
		return new Bundler(reflector, reflector.access(this, as));
	}

	public int getMinimapOffset() {
		return reflector.accessInt(this, at);
	}

	public Object[] getWidgets() {
		final Object[] arr = reflector.access(this, au, Object[].class);
		return arr != null ? arr : new Object[0];
	}

	public int getCrossHairType() {
		return reflector.accessInt(this, av);
	}

	public HintArrow[] getHintArrows() {
		final Object[] arr = reflector.access(this, aw, Object[].class);
		final HintArrow[] arr2 = arr != null ? new HintArrow[arr.length] : new HintArrow[0];
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new HintArrow(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public NodeDeque getProjectileDeque() {
		return new NodeDeque(reflector, reflector.access(this, ax));
	}

	public String getUsername() {
		final SecurityManager s = System.getSecurityManager();
		if (s != null) {
			s.checkPermission(new PrivateCredentialPermission("rt6 u \"*\"", "read"));
		}
		return reflector.accessString(this, ay);
	}

	public String getPassword() {
		final SecurityManager s = System.getSecurityManager();
		if (s != null) {
			s.checkPermission(new PrivateCredentialPermission("rt6 p \"*\"", "read"));
		}
		return reflector.accessString(this, az);
	}

	public HashTable getItemSlots() {
		return new HashTable(reflector, reflector.access(this, ba));
	}

	public NodeSubQueue getLoggerEntries() {
		return new NodeSubQueue(reflector, reflector.access(this, bb));
	}
}
