package org.powerbot.client;

public interface GLModel extends AbstractModel {
	public int[] getXPoints();

	public int[] getYPoints();

	public int[] getZPoints();

	public GLTriangle[] getTriangles();
}
