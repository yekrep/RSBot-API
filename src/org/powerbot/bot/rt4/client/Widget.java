package org.powerbot.bot.rt4.client;

public interface Widget {
	int getX();

	int getY();

	int getWidth();

	int getHeight();

	int getBorderThickness();

	int getType();

	int getId();

	int getParentId();

	Widget[] getChildren();

	int getContentType();

	int getModelId();

	int getModelType();

	int getModelZoom();

	String[] getActions();

	int getAngleX();

	int getAngleY();

	int getAngleZ();

	String getText();

	int getTextColor();

	int getScrollX();

	int getScrollY();

	int getScrollWidth();

	int getScrollHeight();

	int getBoundsIndex();

	int getTextureId();

	int[] getItemIds();

	int[] getItemStackSizes();

	boolean isHidden();

	String getTooltip();

	int getItemId();

	int getItemStackSize();
}
