package org.powerbot.script.rt6;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;

import org.powerbot.bot.rt6.client.AbstractModel;
import org.powerbot.bot.rt6.client.ModelCapture;
import org.powerbot.script.Random;

public abstract class Model extends ClientAccessor {
	private final int height;
	public final int[] yPoints;
	public final short[] faceA;
	public final short[] faceB;
	public final short[] faceC;
	public final int numFaces;
	public final int numVertices;
	public int[] xPoints;
	public int[] zPoints;

	public Model(final ClientContext ctx, final AbstractModel abstractModel) {
		this(ctx, abstractModel, 0);
	}

	public Model(final ClientContext ctx, final AbstractModel abstractModel, final int height) {
		super(ctx);
		final ModelCapture model;
		if (abstractModel instanceof ModelCapture) {
			model = (ModelCapture) abstractModel;
		} else {
			model = (ModelCapture) ModelCapture.updateModel(abstractModel, null);
		}
		this.height = height;
		xPoints = model.getXPoints();
		yPoints = model.getYPoints();
		zPoints = model.getZPoints();
		faceA = model.getIndices1();
		faceB = model.getIndices2();
		faceC = model.getIndices3();
		numVertices = model.getVertices();
		numFaces = model.getFaces();
	}

	public abstract int x();

	public abstract int z();

	public abstract byte floor();

	public abstract void update();

	public int nextTriangle() {
		update();
		final int mark = Random.nextInt(0, numFaces);
		final int index = firstInViewportIndex(mark, numFaces);
		return index != -1 ? index : firstInViewportIndex(0, mark);
	}

	public Point centroid(final int index) {
		if (index < 0 || index >= numFaces) {
			return null;
		}
		update();
		final int x = x();
		final int y = z();
		final int plane = floor();
		final int height = ctx.game.tileHeight(x, y, plane) + this.height;
		final Point localPoint = ctx.game.worldToScreen(
				x + (xPoints[faceA[index]] + xPoints[faceB[index]] + xPoints[faceC[index]]) / 3,
				height + (yPoints[faceA[index]] + yPoints[faceB[index]] + yPoints[faceC[index]]) / 3,
				y + (zPoints[faceA[index]] + zPoints[faceB[index]] + zPoints[faceC[index]]) / 3
		);
		return ctx.game.inViewport(localPoint) ? localPoint : new Point(-1, -1);
	}

	public Point centerPoint() {
		if (numFaces < 1) {
			return new Point(-1, -1);
		}
		update();

		int totalXAverage = 0;
		int totalYAverage = 0;
		int totalHeightAverage = 0;
		int index = 0;

		final int x = x();
		final int y = z();
		final int plane = floor();
		final int height = ctx.game.tileHeight(x, y, plane) + this.height;

		while (index < numFaces) {
			totalXAverage += (xPoints[faceA[index]] + xPoints[faceB[index]] + xPoints[faceC[index]]) / 3;
			totalYAverage += (zPoints[faceA[index]] + zPoints[faceB[index]] + zPoints[faceC[index]]) / 3;
			totalHeightAverage += (yPoints[faceA[index]] + yPoints[faceB[index]] + yPoints[faceC[index]]) / 3;
			index++;
		}

		final Point averagePoint = ctx.game.worldToScreen(
				x + totalXAverage / numFaces,
				height + totalHeightAverage / numFaces,
				y + totalYAverage / numFaces
		);

		if (ctx.game.inViewport(averagePoint)) {
			return averagePoint;
		}
		return new Point(-1, -1);
	}

	public Point nextPoint() {
		update();
		final int mark = Random.nextInt(0, numFaces);
		Point point = firstInViewportCentroid(mark, numFaces);
		return point != null ? point : (point = firstInViewportCentroid(0, mark)) != null ? point : new Point(-1, -1);
	}

	public Polygon[] triangles() {
		final int[][] points = projectVertices();
		final ArrayList<Polygon> polygons = new ArrayList<Polygon>(numFaces);
		for (int index = 0; index < numFaces; index++) {
			final int index1 = faceA[index];
			final int index2 = faceB[index];
			final int index3 = faceC[index];

			final int[] xPoints = new int[3];
			final int[] yPoints = new int[3];

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

	public boolean contains(final Point point) {
		final int x = point.x;
		final int y = point.y;
		final int[][] points = projectVertices();
		int index = 0;
		while (index < numFaces) {
			final int index1 = faceA[index];
			final int index2 = faceB[index];
			final int index3 = faceC[index];
			if (points[index1][2] + points[index2][2] + points[index3][2] == 3 &&
					barycentric(x, y, points[index1][0], points[index1][1], points[index2][0], points[index2][1], points[index3][0], points[index3][1])) {
				return true;
			}
			++index;
		}
		return false;
	}

	public void drawWireFrame(final Graphics render) {
		final int[][] screen = projectVertices();
		for (int index = 0; index < numFaces; index++) {
			final int index1 = faceA[index];
			final int index2 = faceB[index];
			final int index3 = faceC[index];

			final int point1X = screen[index1][0];
			final int point1Y = screen[index1][1];
			final int point2X = screen[index2][0];
			final int point2Y = screen[index2][1];
			final int point3X = screen[index3][0];
			final int point3Y = screen[index3][1];

			if (screen[index1][2] + screen[index2][2] + screen[index3][2] == 3) {
				render.drawLine(point1X, point1Y, point2X, point2Y);
				render.drawLine(point2X, point2Y, point3X, point3Y);
				render.drawLine(point3X, point3Y, point1X, point1Y);
			}
		}
	}

	private int firstInViewportIndex(final int pos, final int length) {
		final int x = x();
		final int y = z();
		final int plane = floor();
		final int h = ctx.game.tileHeight(x, y, plane) + height;
		int index = pos;
		while (index < length) {
			final Point point = ctx.game.worldToScreen(
					x + (xPoints[faceA[index]] + xPoints[faceB[index]] + xPoints[faceC[index]]) / 3,
					h + (yPoints[faceA[index]] + yPoints[faceB[index]] + yPoints[faceC[index]]) / 3,
					y + (zPoints[faceA[index]] + zPoints[faceB[index]] + zPoints[faceC[index]]) / 3
			);
			if (ctx.game.inViewport(point.x, point.y)) {
				return index;
			}
			++index;
		}
		return -1;
	}

	private Point firstInViewportCentroid(final int pos, final int length) {
		final int index = firstInViewportIndex(pos, length);
		return index != -1 ? centroid(index) : null;
	}

	private boolean barycentric(final int x, final int y, final int aX, final int aY, final int bX, final int bY, final int cX, final int cY) {
		final int v00 = cX - aX;
		final int v01 = cY - aY;
		final int v10 = bX - aX;
		final int v11 = bY - aY;
		final int v20 = x - aX;
		final int v21 = y - aY;
		final int d00 = v00 * v00 + v01 * v01;
		final int d01 = v00 * v10 + v01 * v11;
		final int d02 = v00 * v20 + v01 * v21;
		final int d11 = v10 * v10 + v11 * v11;
		final int d12 = v10 * v20 + v11 * v21;
		final float denom = 1.0f / (d00 * d11 - d01 * d01);
		final float u = (d11 * d02 - d01 * d12) * denom;
		final float v = (d00 * d12 - d01 * d02) * denom;
		return u >= 0 && v >= 0 && u + v < 1;
	}

	private int[][] projectVertices() {
		final Game.Toolkit toolkit = ctx.game.toolkit;
		final Game.Viewport viewport = ctx.game.viewport;

		update();
		final int locX = x();
		final int locY = z();
		final int plane = floor();
		final int height = ctx.game.tileHeight(locX, locY, plane) + this.height;

		final int[][] screen = new int[numVertices][3];
		for (int index = 0; index < numVertices; index++) {
			final int x = xPoints[index] + locX;
			final int y = yPoints[index] + height;
			final int z = zPoints[index] + locY;

			final float _z = (viewport.zOff + (viewport.zX * x + viewport.zY * y + viewport.zZ * z));
			final float _x = (viewport.xOff + (viewport.xX * x + viewport.xY * y + viewport.xZ * z));
			final float _y = (viewport.yOff + (viewport.yX * x + viewport.yY * y + viewport.yZ * z));

			if (_x >= -_z && _x <= _z && _y >= -_z && _y <= _z) {
				screen[index][0] = Math.round(toolkit.absoluteX + (toolkit.xMultiplier * _x) / _z);
				screen[index][1] = Math.round(toolkit.absoluteY + (toolkit.yMultiplier * _y) / _z);
				screen[index][2] = 1;
				if (ctx.game.inViewport(screen[index][0], screen[index][1])) {
					continue;
				}
			}
			screen[index][0] = -1;
			screen[index][1] = -1;
			screen[index][2] = 0;
		}
		return screen;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(faceA);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Model)) {
			return false;
		}
		final Model model = (Model) o;
		return Arrays.equals(faceA, model.faceA) &&
				Arrays.equals(xPoints, model.xPoints) && Arrays.equals(yPoints, model.yPoints) && Arrays.equals(zPoints, model.zPoints);
	}

	@Override
	public String toString() {
		return "[faces=" + numFaces + "vertices=" + numVertices + "] " + Arrays.toString(faceA);
	}
}
