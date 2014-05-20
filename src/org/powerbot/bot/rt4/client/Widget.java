package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Widget extends ReflectProxy {
	public Widget(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getX() {
		return reflector.accessInt(this);
	}

	public int getY() {
		return reflector.accessInt(this);
	}

	public int getWidth() {
		return reflector.accessInt(this);
	}

	public int getHeight() {
		return reflector.accessInt(this);
	}

	public int getBorderThickness() {
		return reflector.accessInt(this);
	}

	public int getType() {
		return reflector.accessInt(this);
	}

	public int getId() {
		return reflector.accessInt(this);
	}

	public int getParentId() {
		return reflector.accessInt(this);
	}

	public Widget[] getChildren() {
		final Object[] arr = reflector.access(this, Object[].class);
		final Widget[] arr2 = arr != null ? new Widget[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Widget(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public int getContentType() {
		return reflector.accessInt(this);
	}

	public int getModelId() {
		return reflector.accessInt(this);
	}

	public int getModelType() {
		return reflector.accessInt(this);
	}

	public int getModelZoom() {
		return reflector.accessInt(this);
	}

	public String[] getActions() {
		return reflector.access(this, String[].class);
	}

	public int getAngleX() {
		return reflector.accessInt(this);
	}

	public int getAngleY() {
		return reflector.accessInt(this);
	}

	public int getAngleZ() {
		return reflector.accessInt(this);
	}

	public String getText() {
		return reflector.accessString(this);
	}

	public int getTextColor() {
		return reflector.accessInt(this);
	}

	public int getScrollX() {
		return reflector.accessInt(this);
	}

	public int getScrollY() {
		return reflector.accessInt(this);
	}

	public int getScrollWidth() {
		return reflector.accessInt(this);
	}

	public int getScrollHeight() {
		return reflector.accessInt(this);
	}

	public int getBoundsIndex() {
		return reflector.accessInt(this);
	}

	public int getTextureId() {
		return reflector.accessInt(this);
	}

	public int[] getItemIds() {
		return reflector.accessInts(this);
	}

	public int[] getItemStackSizes() {
		return reflector.accessInts(this);
	}

	public boolean isHidden() {
		return reflector.accessBool(this);
	}

	public String getTooltip() {
		return reflector.accessString(this);
	}

	public int getItemId() {
		return reflector.accessInt(this);
	}

	public int getItemStackSize() {
		return reflector.accessInt(this);
	}
}
