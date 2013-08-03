package org.powerbot.script.wrappers;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;

import org.powerbot.client.AbstractModel;
import org.powerbot.client.ModelCapture;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;
import org.powerbot.script.util.Random;

public abstract class Model extends MethodProvider {
	private final int height;
	protected final int[] yPoints;
	protected final short[] faceA;
	protected final short[] faceB;
	protected final short[] faceC;
	protected final int numFaces;
	protected final int numVertices;
	protected int[] xPoints;
	protected int[] zPoints;

	public Model(MethodContext ctx, AbstractModel abstractModel) {
		this(ctx, abstractModel, 0);
	}

	public Model(MethodContext ctx, AbstractModel abstractModel, int height) {
		super(ctx);
		ModelCapture model;
		if (abstractModel instanceof ModelCapture) {
			model = (ModelCapture) abstractModel;
		} else {
			model = (ModelCapture) ModelCapture.updateModel(abstractModel, null);
		}
		this.height = height;
		xPoints = model.getXPoints();
		yPoints = model.getYPoints();
		zPoints = model.getZPoints();
		faceA = model.getFaceA();
		faceB = model.getFaceB();
		faceC = model.getFaceC();
		numVertices = model.getNumVertices();
		numFaces = model.getNumFaces();
	}

	public abstract int getX();

	public abstract int getY();

	public abstract byte getPlane();

	public abstract void update();

	public int nextTriangle() {
		update();
		int mark = Random.nextInt(0, numFaces);
		int index = firstOnScreenIndex(mark, numFaces);
		return index != -1 ? index : firstOnScreenIndex(0, mark);
	}

	public Point getCentroid(int index) {
		if (index < 0 || index >= numFaces) {
			return null;
		}
		update();
		int x = getX();
		int y = getY();
		int plane = getPlane();
		int height = ctx.game.tileHeight(x, y, plane) + this.height;
		Point localPoint = ctx.game.worldToScreen(
				x + (this.xPoints[this.faceA[index]] + this.xPoints[this.faceB[index]] + this.xPoints[this.faceC[index]]) / 3,
				height + (this.yPoints[this.faceA[index]] + this.yPoints[this.faceB[index]] + this.yPoints[this.faceC[index]]) / 3,
				y + (this.zPoints[this.faceA[index]] + this.zPoints[this.faceB[index]] + this.zPoints[this.faceC[index]]) / 3
		);
		return ctx.game.isPointOnScreen(localPoint) ? localPoint : new Point(-1, -1);
	}

	public Point getCenterPoint() {
		if (numFaces < 1) {
			return new Point(-1, -1);
		}
		update();

		int totalXAverage = 0;
		int totalYAverage = 0;
		int totalHeightAverage = 0;
		int index = 0;

		int x = getX();
		int y = getY();
		int plane = getPlane();
		int height = ctx.game.tileHeight(x, y, plane) + this.height;

		while (index < numFaces) {
			totalXAverage += (xPoints[faceA[index]] + xPoints[faceB[index]] + xPoints[faceC[index]]) / 3;
			totalYAverage += (zPoints[faceA[index]] + zPoints[faceB[index]] + zPoints[faceC[index]]) / 3;
			totalHeightAverage += (yPoints[faceA[index]] + yPoints[faceB[index]] + yPoints[faceC[index]]) / 3;
			index++;
		}

		Point averagePoint = ctx.game.worldToScreen(
				x + totalXAverage / numFaces,
				height + totalHeightAverage / numFaces,
				y + totalYAverage / numFaces
		);

		if (ctx.game.isPointOnScreen(averagePoint)) {
			return averagePoint;
		}
		return new Point(-1, -1);
	}

	public Point getNextPoint() {
		update();
		int mark = Random.nextInt(0, numFaces);
		Point point = firstOnScreenCentroid(mark, numFaces);
		return point != null ? point : (point = firstOnScreenCentroid(0, mark)) != null ? point : new Point(-1, -1);
	}

	public short[] getFaceA() {
		return faceA;
	}

	public Polygon[] getTriangles() {
		int[][] points = projectVertices();
		ArrayList<Polygon> polygons = new ArrayList<>(numFaces);
		for (int index = 0; index < numFaces; index++) {
			int index1 = faceA[index];
			int index2 = faceB[index];
			int index3 = faceC[index];

			int xPoints[] = new int[3];
			int yPoints[] = new int[3];

			xPoints[0] = points[index1][0];
			yPoints[0] = points[index1][1];
			xPoints[1] = points[index2][0];
			yPoints[1] = points[index2][1];
			xPoints[2] = points[index3][0];
			yPoints[2] = points[index3][1];

			if (points[index1][2] + points[index2][2] + points[index3][2] == 3) {
				polygons.add(new Polygon(xPoints, yPoints, 3));
			}
		}
		return polygons.toArray(new Polygon[polygons.size()]);
	}

	public boolean contains(Point point) {
		int x = point.x, y = point.y;
		int[][] points = projectVertices();
		int index = 0;
		while (index < numFaces) {
			int index1 = faceA[index];
			int index2 = faceB[index];
			int index3 = faceC[index];
			if (points[index1][2] + points[index2][2] + points[index3][2] == 3 &&
					barycentric(x, y, points[index1][0], points[index1][1], points[index2][0], points[index2][1], points[index3][0], points[index3][1])) {
				return true;
			}
			++index;
		}
		return false;
	}

	public void drawWireFrame(Graphics render) {
		int[][] screen = projectVertices();

		for (int index = 0; index < numFaces; index++) {
			int index1 = faceA[index];
			int index2 = faceB[index];
			int index3 = faceC[index];

			int point1X = screen[index1][0];
			int point1Y = screen[index1][1];
			int point2X = screen[index2][0];
			int point2Y = screen[index2][1];
			int point3X = screen[index3][0];
			int point3Y = screen[index3][1];

			if (screen[index1][2] + screen[index2][2] + screen[index3][2] == 3) {
				render.drawLine(point1X, point1Y, point2X, point2Y);
				render.drawLine(point2X, point2Y, point3X, point3Y);
				render.drawLine(point3X, point3Y, point1X, point1Y);
			}
		}
	}

	private int firstOnScreenIndex(int pos, int length) {
		int x = getX();
		int y = getY();
		int plane = getPlane();
		int h = ctx.game.tileHeight(x, y, plane) + this.height;
		int index = pos;
		while (index < length) {
			Point point = ctx.game.worldToScreen(
					x + (this.xPoints[this.faceA[index]] + this.xPoints[this.faceB[index]] + this.xPoints[this.faceC[index]]) / 3,
					h + (this.yPoints[this.faceA[index]] + this.yPoints[this.faceB[index]] + this.yPoints[this.faceC[index]]) / 3,
					y + (this.zPoints[this.faceA[index]] + this.zPoints[this.faceB[index]] + this.zPoints[this.faceC[index]]) / 3
			);
			if (point.x != -1 && point.y != -1) {
				return index;
			}
			++index;
		}
		return -1;
	}

	private Point firstOnScreenCentroid(int pos, int length) {
		int index = firstOnScreenIndex(pos, length);
		return index != -1 ? getCentroid(index) : null;
	}

	private boolean barycentric(int x, int y, int aX, int aY, int bX, int bY, int cX, int cY) {
		int v00 = cX - aX, v01 = cY - aY;
		int v10 = bX - aX, v11 = bY - aY;
		int v20 = x - aX, v21 = y - aY;
		int d00 = v00 * v00 + v01 * v01, d01 = v00 * v10 + v01 * v11, d02 = v00 * v20 + v01 * v21;
		int d11 = v10 * v10 + v11 * v11, d12 = v10 * v20 + v11 * v21;
		float denom = 1.0f / (d00 * d11 - d01 * d01);
		float u = (d11 * d02 - d01 * d12) * denom;
		float v = (d00 * d12 - d01 * d02) * denom;
		return u >= 0 && v >= 0 && u + v < 1;
	}

	private int[][] projectVertices() {
		Game.Toolkit toolkit = ctx.game.toolkit;
		Game.Viewport viewport = ctx.game.viewport;

		update();
		int locX = getX();
		int locY = getY();
		int plane = getPlane();
		int height = ctx.game.tileHeight(locX, locY, plane) + this.height;

		int[][] screen = new int[numVertices][3];
		for (int index = 0; index < numVertices; index++) {
			int x = xPoints[index] + locX;
			int y = yPoints[index] + height;
			int z = zPoints[index] + locY;

			float _z = (viewport.zOff + (viewport.zX * x + viewport.zY * y + viewport.zZ * z));
			float _x = (viewport.xOff + (viewport.xX * x + viewport.xY * y + viewport.xZ * z));
			float _y = (viewport.yOff + (viewport.yX * x + viewport.yY * y + viewport.yZ * z));

			if (_x >= -_z && _x <= _z && _y >= -_z && _y <= _z) {
				screen[index][0] = Math.round(toolkit.absoluteX + (toolkit.xMultiplier * _x) / _z);
				screen[index][1] = Math.round(toolkit.absoluteY + (toolkit.yMultiplier * _y) / _z);
				screen[index][2] = 1;
			} else {
				screen[index][0] = -1;
				screen[index][1] = -1;
				screen[index][2] = 0;
			}
		}
		return screen;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(faceA);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Model)) {
			return false;
		}
		Model model = (Model) o;
		return Arrays.equals(faceA, model.faceA) &&
				Arrays.equals(xPoints, model.xPoints) && Arrays.equals(yPoints, model.yPoints) && Arrays.equals(zPoints, model.zPoints);
	}

	@Override
	public String toString() {
		return "[faces=" + numFaces + "vertices=" + numVertices + "] " + Arrays.toString(faceA);
	}
}
