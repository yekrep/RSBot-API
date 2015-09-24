package org.powerbot.bot.rt4.client;

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
			ae = new Reflector.FieldCache();

	public Widget(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getX() {
		return reflector.accessInt(this, a);
	}

	public int getY() {
		return reflector.accessInt(this, b);
	}

	public int getWidth() {
		return reflector.accessInt(this, c);
	}

	public int getHeight() {
		return reflector.accessInt(this, d);
	}

	public int getBorderThickness() {
		return reflector.accessInt(this, e);
	}

	public int getType() {
		return reflector.accessInt(this, f);
	}

	public int getId() {
		return reflector.accessInt(this, g);
	}

	public int getParentId() {
		return reflector.accessInt(this, h);
	}

	public Widget[] getChildren() {
		final Object[] arr = reflector.access(this, i, Object[].class);
		final Widget[] arr2 = arr != null ? new Widget[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Widget(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public int getContentType() {
		return reflector.accessInt(this, j);
	}

	public int getModelId() {
		return reflector.accessInt(this, k);
	}

	public int getModelType() {
		return reflector.accessInt(this, l);
	}

	public int getModelZoom() {
		return reflector.accessInt(this, m);
	}

	public String[] getActions() {
		return reflector.access(this, n, String[].class);
	}

	public int getAngleX() {
		return reflector.accessInt(this, o);
	}

	public int getAngleY() {
		return reflector.accessInt(this, p);
	}

	public int getAngleZ() {
		return reflector.accessInt(this, q);
	}

	public String getText() {
		return reflector.accessString(this, r);
	}

	public int getTextColor() {
		return reflector.accessInt(this, s);
	}

	public int getScrollX() {
		return reflector.accessInt(this, t);
	}

	public int getScrollY() {
		return reflector.accessInt(this, u);
	}

	public int getScrollWidth() {
		return reflector.accessInt(this, v);
	}

	public int getScrollHeight() {
		return reflector.accessInt(this, w);
	}

	public int getBoundsIndex() {
		return reflector.accessInt(this, x);
	}

	public int getTextureId() {
		return reflector.accessInt(this, y);
	}

	public int[] getItemIds() {
		return reflector.accessInts(this, z);
	}

	public int[] getItemStackSizes() {
		return reflector.accessInts(this, aa);
	}

	public boolean isHidden() {
		return reflector.accessBool(this, ab);
	}

	public String getTooltip() {
		return reflector.accessString(this, ac);
	}

	public int getItemId() {
		return reflector.accessInt(this, ad);
	}

	public int getItemStackSize() {
		return reflector.accessInt(this, ae);
	}
}
