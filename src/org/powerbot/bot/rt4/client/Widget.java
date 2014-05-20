package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class Widget extends ContextAccessor {
	public Widget(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getX() {
		return engine.accessInt(this);
	}

	public int getY() {
		return engine.accessInt(this);
	}

	public int getWidth() {
		return engine.accessInt(this);
	}

	public int getHeight() {
		return engine.accessInt(this);
	}

	public int getBorderThickness() {
		return engine.accessInt(this);
	}

	public int getType() {
		return engine.accessInt(this);
	}

	public int getId() {
		return engine.accessInt(this);
	}

	public int getParentId() {
		return engine.accessInt(this);
	}

	public Widget[] getChildren() {
		final Object[] arr = engine.access(this, Object[].class);
		final Widget[] arr2 = arr != null ? new Widget[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Widget(engine, arr[i]);
			}
		}
		return arr2;
	}

	public int getContentType() {
		return engine.accessInt(this);
	}

	public int getModelId() {
		return engine.accessInt(this);
	}

	public int getModelType() {
		return engine.accessInt(this);
	}

	public int getModelZoom() {
		return engine.accessInt(this);
	}

	public String[] getActions() {
		return engine.access(this, String[].class);
	}

	public int getAngleX() {
		return engine.accessInt(this);
	}

	public int getAngleY() {
		return engine.accessInt(this);
	}

	public int getAngleZ() {
		return engine.accessInt(this);
	}

	public String getText() {
		return engine.access(this, String.class);
	}

	public int getTextColor() {
		return engine.accessInt(this);
	}

	public int getScrollX() {
		return engine.accessInt(this);
	}

	public int getScrollY() {
		return engine.accessInt(this);
	}

	public int getScrollWidth() {
		return engine.accessInt(this);
	}

	public int getScrollHeight() {
		return engine.accessInt(this);
	}

	public int getBoundsIndex() {
		return engine.accessInt(this);
	}

	public int getTextureId() {
		return engine.accessInt(this);
	}

	public int[] getItemIds() {
		return engine.access(this, int[].class);
	}

	public int[] getItemStackSizes() {
		return engine.access(this, int[].class);
	}

	public boolean isHidden() {
		return engine.accessBool(this);
	}

	public String getTooltip() {
		return engine.access(this, String.class);
	}

	public int getItemId() {
		return engine.accessInt(this);
	}

	public int getItemStackSize() {
		return engine.accessInt(this);
	}
}
