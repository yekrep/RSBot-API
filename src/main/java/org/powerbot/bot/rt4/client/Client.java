package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;
import org.powerbot.script.ClientContext;

import javax.security.auth.PrivateCredentialPermission;
import java.security.AccessController;

public class Client extends ReflectProxy implements org.powerbot.script.Client {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache(),
			e = new Reflector.FieldCache(),
			f = new Reflector.FieldCache(),
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
			bb = new Reflector.FieldCache(),
			bc = new Reflector.FieldCache(),
			bd = new Reflector.FieldCache(),
			be = new Reflector.FieldCache(),
			bf = new Reflector.FieldCache(),
			bg = new Reflector.FieldCache(),
			bh = new Reflector.FieldCache(),
			bi = new Reflector.FieldCache(),
			bj = new Reflector.FieldCache(),
			bk = new Reflector.FieldCache(),
			bl = new Reflector.FieldCache(),
			bm = new Reflector.FieldCache(),
			bn = new Reflector.FieldCache(),
			bo = new Reflector.FieldCache(),
			bp = new Reflector.FieldCache();

	public Client(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public NodeDeque getProjectiles() {
		return new NodeDeque(reflector, reflector.access(this, bp));
	}

	public boolean isMembers() {
		return reflector.accessBool(this, bo);
	}

	public int getCameraX() {
		return reflector.accessInt(this, a);
	}

	public int getCameraY() {
		return reflector.accessInt(this, b);
	}

	public int getCameraZ() {
		return reflector.accessInt(this, c);
	}

	public int getCameraYaw() {
		return reflector.accessInt(this, d);
	}

	public int getCameraPitch() {
		return reflector.accessInt(this, e);
	}

	public int getMinimapAngle() {
		return reflector.accessInt(this, f);
	}

	public Player getPlayer() {
		return new Player(reflector, reflector.access(this, i));
	}

	public Player[] getPlayers() {
		final Object[] arr = reflector.access(this, j, Object[].class);
		final Player[] arr2 = arr != null ? new Player[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Player(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public int[] getPlayerIndices() {
		return reflector.accessInts(this, k);
	}

	public Npc[] getNpcs() {
		final Object[] arr = reflector.access(this, l, Object[].class);
		final Npc[] arr2 = arr != null ? new Npc[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Npc(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public int[] getNpcIndices() {
		return reflector.accessInts(this, m);
	}

	public int getOffsetX() {
		return reflector.accessInt(this, n);
	}

	public int getOffsetY() {
		return reflector.accessInt(this, o);
	}

	public int getFloor() {
		return reflector.accessInt(this, p);
	}

	public Landscape getLandscape() {
		return new Landscape(reflector, reflector.access(this, q));
	}

	public byte[][][] getLandscapeMeta() {
		return reflector.access(this, r, byte[][][].class);
	}

	public int[][][] getTileHeights() {
		return reflector.access(this, s, int[][][].class);
	}

	public boolean isMenuOpen() {
		return reflector.accessBool(this, t);
	}

	public int getMenuX() {
		return reflector.accessInt(this, u);
	}

	public int getMenuY() {
		return reflector.accessInt(this, v);
	}

	public int getMenuWidth() {
		return reflector.accessInt(this, w);
	}

	public int getMenuHeight() {
		return reflector.accessInt(this, x);
	}

	public int getMenuCount() {
		return reflector.accessInt(this, y);
	}

	public String[] getMenuActions() {
		return reflector.access(this, z, String[].class);
	}

	public String[] getMenuOptions() {
		return reflector.access(this, aa, String[].class);
	}

	public int[] getWidgetBoundsX() {
		return reflector.accessInts(this, ab);
	}

	public int[] getWidgetBoundsY() {
		return reflector.accessInts(this, ac);
	}

	public int[] getWidgetBoundsWidth() {
		return reflector.accessInts(this, ad);
	}

	public int[] getWidgetBoundsHeight() {
		return reflector.accessInts(this, ae);
	}

	public int getDestinationX() {
		return reflector.accessInt(this, af);
	}

	public int getDestinationY() {
		return reflector.accessInt(this, ag);
	}

	public Widget[][] getWidgets() {
		final Object[][] arr = reflector.access(this, ah, Object[][].class);
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
		return new HashTable(reflector, reflector.access(this, ai));
	}

	public NodeDeque[][][] getGroundItems() {
		final Object[][][] arr = reflector.access(this, aj, Object[][][].class);
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
		final Object[] arr = reflector.access(this, ak, Object[].class);
		final CollisionMap[] arr2 = arr != null ? new CollisionMap[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new CollisionMap(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public int[] getVarpbits() {
		return reflector.accessInts(this, al);
	}

	public int getClientState() {
		return reflector.accessInt(this, am);
	}

	public int getCrosshairIndex() {
		return reflector.accessInt(this, an);
	}

	public Cache getVarbitCache() {
		return new Cache(reflector, reflector.access(this, ao));
	}

	public Cache getNpcConfigCache() {
		return new Cache(reflector, reflector.access(this, ap));
	}

	public Cache getObjectConfigCache() {
		return new Cache(reflector, reflector.access(this, aq));
	}

	public Cache getItemConfigCache() {
		return new Cache(reflector, reflector.access(this, ar));
	}

	public int[] getSkillLevels1() {
		return reflector.accessInts(this, as);
	}

	public int[] getSkillLevels2() {
		return reflector.accessInts(this, at);
	}

	public int[] getSkillExps() {
		return reflector.accessInts(this, au);
	}

	public int getCycle() {
		return reflector.accessInt(this, av);
	}

	public int getHintArrowNpcUid() {
		return reflector.accessInt(this, aw);
	}

	public int getHintArrowPlayerUid() {
		return reflector.accessInt(this, ax);
	}

	public int getHintArrowType() {
		return reflector.accessInt(this, ay);
	}

	public int getHintArrowX() {
		return reflector.accessInt(this, az);
	}

	public int getHintArrowY() {
		return reflector.accessInt(this, ba);
	}

	public int getSelectionType() {
		return reflector.accessInt(this, bb);
	}

	public int getSelectionIndex() {
		return reflector.accessInt(this, bc);
	}

	public String getUsername() {
		final SecurityManager s = System.getSecurityManager();
		if (s != null) {
			s.checkPermission(new PrivateCredentialPermission("rt4 u \"*\"", "read"));
		}
		return reflector.accessString(this, bd);
	}

	public String getPassword() {
		final SecurityManager s = System.getSecurityManager();
		if (s != null) {
			s.checkPermission(new PrivateCredentialPermission("rt4 p \"*\"", "read"));
		}
		return reflector.accessString(this, be);
	}

	public int getPlayerIndex() {
		return reflector.accessInt(this, bf);
	}

	public int getRunPercentage() {
		return reflector.accessInt(this, bg);
	}

	public EntryList getLoggerEntries() {
		return new EntryList(reflector, reflector.access(this, bh));
	}

	public int getLoginState() {
		return reflector.accessInt(this, bi);
	}

	public int getLoginField() {
		return reflector.accessInt(this, bj);
	}

	public boolean isWorldSelectionUp() {
		return reflector.accessBool(this, bk);
	}

	public int getTileSize() {
		return reflector.accessInt(this, bl);
	}

	public int getNpcCount() {
		return reflector.accessInt(this, bm);
	}

	public int getPlayerCount() {
		return reflector.accessInt(this, bn);
	}
}