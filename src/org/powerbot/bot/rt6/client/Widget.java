package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Widget extends ReflectProxy {
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
			am = new Reflector.FieldCache();

	public Widget(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String getTooltip() {
		return reflector.accessString(this, a);
	}

	public int getComponentId() {
		return reflector.accessInt(this, b);
	}

	public int getZRotation() {
		return reflector.accessInt(this, c);
	}

	public int getWidth() {
		return reflector.accessInt(this, d);
	}

	public int getXRotation() {
		return reflector.accessInt(this, e);
	}

	public int getModelType() {
		return reflector.accessInt(this, f);
	}

	public int getTextureId() {
		return reflector.accessInt(this, g);
	}

	public Object[] getComponents() {
		final Object[] arr = reflector.access(this, h, Object[].class);
		if (arr == null) {
			return new Object[0];
		}
		return arr;
	}

	public Widget[] getComponentsW() {
		final Object[] arr = reflector.access(this, h, Object[].class);
		final Widget[] arr2 = arr != null ? new Widget[arr.length] : new Widget[0];
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Widget(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public String getComponentName() {
		return reflector.accessString(this, i);
	}

	public int getX() {
		return reflector.accessInt(this, j);
	}

	public int getHorizontalScrollbarPosition() {
		return reflector.accessInt(this, k);
	}

	public int getSpecialType() {
		return reflector.accessInt(this, l);
	}

	public int getY() {
		return reflector.accessInt(this, m);
	}

	public int getParentId() {
		return reflector.accessInt(this, n);
	}

	public String getText() {
		return reflector.accessString(this, o);
	}

	public int getVerticalScrollbarThumbSize() {
		return reflector.accessInt(this, p);
	}

	public int getComponentStackSize() {
		return reflector.accessInt(this, q);
	}

	public int getVerticalScrollbarPosition() {
		return reflector.accessInt(this, r);
	}

	public String[] getActions() {
		return reflector.access(this, s, String[].class);
	}

	public int getVerticalScrollbarSize() {
		return reflector.accessInt(this, t);
	}

	public int getComponentIndex() {
		return reflector.accessInt(this, u);
	}

	public int getType() {
		return reflector.accessInt(this, v);
	}

	public int getModelId() {
		return reflector.accessInt(this, w);
	}

	public int getModelZoom() {
		return reflector.accessInt(this, x);
	}

	public String getSelectedActionName() {
		return reflector.accessString(this, y);
	}

	public int getHorizontalScrollbarSize() {
		return reflector.accessInt(this, z);
	}

	public int getHorizontalScrollbarThumbSize() {
		return reflector.accessInt(this, aa);
	}

	public boolean isVerticallyFlipped() {
		return reflector.accessBool(this, ab);
	}

	public int getTextColor() {
		return reflector.accessInt(this, ac);
	}

	public int getYRotation() {
		return reflector.accessInt(this, ad);
	}

	public int getBoundsArrayIndex() {
		return reflector.accessInt(this, ae);
	}

	public int getShadowColor() {
		return reflector.accessInt(this, af);
	}

	public boolean isHorizontallyFlipped() {
		return reflector.accessBool(this, ag);
	}

	public boolean isHidden() {
		return reflector.accessBool(this, ah);
	}

	public boolean isInventoryInterface() {
		return reflector.accessBool(this, ai);
	}

	public int getHeight() {
		return reflector.accessInt(this, aj);
	}

	public int getId() {
		return reflector.accessInt(this, ak);
	}

	public boolean isVisible() {
		return reflector.accessBool(this, al);
	}

	public int getBorderThinkness() {
		return reflector.accessInt(this, am);
	}
}
