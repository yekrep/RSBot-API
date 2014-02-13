package org.powerbot.bot.client;

public interface Widget {
	public int getX();

	public int getY();

	public int getWidth();

	public int getHeight();

	public int getBorderThickness();

	public int getType();

	public int getId();

	public int getParentId();

	public Widget[] getChildren();

	public int getContentType();

	public int getModelId();

	public int getModelType();

	public int getModelZoom();

	public String[] getActions();

	public int getAngleX();

	public int getAngleY();

	public int getAngleZ();

	public String getText();

	public int getTextColor();

	public int getScrollX();

	public int getScrollY();

	public int getScrollWidth();

	public int getScrollHeight();

	public int getBoundsIndex();

	public int getTextureId();
}
