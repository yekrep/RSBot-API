package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class RSInterface extends ContextAccessor {
	public RSInterface(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public String getTooltip() {
		return engine.access(this, String.class);
	}

	public int getComponentID() {
		return engine.accessInt(this);
	}

	public int getZRotation() {
		return engine.accessInt(this);
	}

	public int getWidth() {
		return engine.accessInt(this);
	}

	public int getXRotation() {
		return engine.accessInt(this);
	}

	public int getModelType() {
		return engine.accessInt(this);
	}

	public int getTextureID() {
		return engine.accessInt(this);
	}

	public RSInterface[] getComponents() {
		final Object[] arr = engine.access(this, Object[].class);
		final RSInterface[] arr2 = arr != null ? new RSInterface[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new RSInterface(engine, arr[i]);
			}
		}
		return arr2;
	}

	public String getComponentName() {
		return engine.access(this, String.class);
	}

	public int getX() {
		return engine.accessInt(this);
	}

	public int getHorizontalScrollbarPosition() {
		return engine.accessInt(this);
	}

	public int getSpecialType() {
		return engine.accessInt(this);
	}

	public int getY() {
		return engine.accessInt(this);
	}

	public int getParentID() {
		return engine.accessInt(this);
	}

	public String getText() {
		return engine.access(this, String.class);
	}

	public int getVerticalScrollbarThumbSize() {
		return engine.accessInt(this);
	}

	public int getComponentStackSize() {
		return engine.accessInt(this);
	}

	public int getVerticalScrollbarPosition() {
		return engine.accessInt(this);
	}

	public String[] getActions() {
		return engine.access(this, String[].class);
	}

	public int getVerticalScrollbarSize() {
		return engine.accessInt(this);
	}

	public int getComponentIndex() {
		return engine.accessInt(this);
	}

	public int getType() {
		return engine.accessInt(this);
	}

	public int getModelID() {
		return engine.accessInt(this);
	}

	public int getModelZoom() {
		return engine.accessInt(this);
	}

	public String getSelectedActionName() {
		return engine.access(this, String.class);
	}

	public int getHorizontalScrollbarSize() {
		return engine.accessInt(this);
	}

	public int getHorizontalScrollbarThumbSize() {
		return engine.accessInt(this);
	}

	public boolean isVerticallyFlipped() {
		return engine.accessBool(this);
	}

	public int getTextColor() {
		return engine.accessInt(this);
	}

	public int getYRotation() {
		return engine.accessInt(this);
	}

	public int getBoundsArrayIndex() {
		return engine.accessInt(this);
	}

	public int getShadowColor() {
		return engine.accessInt(this);
	}

	public boolean isHorizontallyFlipped() {
		return engine.accessBool(this);
	}

	public boolean isHidden() {
		return engine.accessBool(this);
	}

	public boolean isInventoryInterface() {
		return engine.accessBool(this);
	}

	public int getHeight() {
		return engine.accessInt(this);
	}

	public int getID() {
		return engine.accessInt(this);
	}

	public boolean isVisible() {
		return engine.accessBool(this);
	}

	public int getBorderThinkness() {
		return engine.accessInt(this);
	}
}
