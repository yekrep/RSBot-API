package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Widget extends ReflectProxy {
	public Widget(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String getTooltip() {
		return reflector.accessString(this);
	}

	public int getComponentId() {
		return reflector.accessInt(this);
	}

	public int getZRotation() {
		return reflector.accessInt(this);
	}

	public int getWidth() {
		return reflector.accessInt(this);
	}

	public int getXRotation() {
		return reflector.accessInt(this);
	}

	public int getModelType() {
		return reflector.accessInt(this);
	}

	public int getTextureId() {
		return reflector.accessInt(this);
	}

	public Widget[] getComponents() {
		final Object[] arr = reflector.access(this, Object[].class);
		final Widget[] arr2 = arr != null ? new Widget[arr.length] : new Widget[0];
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Widget(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public String getComponentName() {
		return reflector.accessString(this);
	}

	public int getX() {
		return reflector.accessInt(this);
	}

	public int getHorizontalScrollbarPosition() {
		return reflector.accessInt(this);
	}

	public int getSpecialType() {
		return reflector.accessInt(this);
	}

	public int getY() {
		return reflector.accessInt(this);
	}

	public int getParentId() {
		return reflector.accessInt(this);
	}

	public String getText() {
		return reflector.accessString(this);
	}

	public int getVerticalScrollbarThumbSize() {
		return reflector.accessInt(this);
	}

	public int getComponentStackSize() {
		return reflector.accessInt(this);
	}

	public int getVerticalScrollbarPosition() {
		return reflector.accessInt(this);
	}

	public String[] getActions() {
		return reflector.access(this, String[].class);
	}

	public int getVerticalScrollbarSize() {
		return reflector.accessInt(this);
	}

	public int getComponentIndex() {
		return reflector.accessInt(this);
	}

	public int getType() {
		return reflector.accessInt(this);
	}

	public int getModelId() {
		return reflector.accessInt(this);
	}

	public int getModelZoom() {
		return reflector.accessInt(this);
	}

	public String getSelectedActionName() {
		return reflector.accessString(this);
	}

	public int getHorizontalScrollbarSize() {
		return reflector.accessInt(this);
	}

	public int getHorizontalScrollbarThumbSize() {
		return reflector.accessInt(this);
	}

	public boolean isVerticallyFlipped() {
		return reflector.accessBool(this);
	}

	public int getTextColor() {
		return reflector.accessInt(this);
	}

	public int getYRotation() {
		return reflector.accessInt(this);
	}

	public int getBoundsArrayIndex() {
		return reflector.accessInt(this);
	}

	public int getShadowColor() {
		return reflector.accessInt(this);
	}

	public boolean isHorizontallyFlipped() {
		return reflector.accessBool(this);
	}

	public boolean isHidden() {
		return reflector.accessBool(this);
	}

	public boolean isInventoryInterface() {
		return reflector.accessBool(this);
	}

	public int getHeight() {
		return reflector.accessInt(this);
	}

	public int getId() {
		return reflector.accessInt(this);
	}

	public boolean isVisible() {
		return reflector.accessBool(this);
	}

	public int getBorderThinkness() {
		return reflector.accessInt(this);
	}
}
