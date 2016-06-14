package org.powerbot.script.rt6;

import static org.powerbot.script.rt6.CollisionFlag.DEAD_BLOCK;
import static org.powerbot.script.rt6.CollisionFlag.DECORATION_BLOCK;
import static org.powerbot.script.rt6.CollisionFlag.EAST;
import static org.powerbot.script.rt6.CollisionFlag.NORTH;
import static org.powerbot.script.rt6.CollisionFlag.NORTHEAST;
import static org.powerbot.script.rt6.CollisionFlag.NORTHWEST;
import static org.powerbot.script.rt6.CollisionFlag.OBJECT_BLOCK;
import static org.powerbot.script.rt6.CollisionFlag.SOUTH;
import static org.powerbot.script.rt6.CollisionFlag.SOUTHEAST;
import static org.powerbot.script.rt6.CollisionFlag.SOUTHWEST;
import static org.powerbot.script.rt6.CollisionFlag.WEST;

/**
 * CollisionMap
 */
public final class CollisionMap {
	private final CollisionFlag[][] clipping;
	private final int xOff;
	private final int yOff;
	private final int width;
	private final int height;

	public CollisionMap(final int xSize, final int ySize) {
		this.xOff = -1;
		this.yOff = -1;
		this.width = xSize + 6;
		this.height = ySize + 6;
		this.clipping = new CollisionFlag[width][height];
		clear();
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public void clear() {
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				if ((x <= 1) || (y <= 1) || (x >= this.width - 6) || (y >= this.height - 6)) {
					this.clipping[x][y] = CollisionFlag.PADDING;
				} else {
					this.clipping[x][y] = CollisionFlag.createNewMarkable();
				}
			}
		}
	}

	public CollisionFlag flagAt(final int localX, final int localY) {
		return clipping[localX(localX)][localY(localY)];
	}

	public void markDecoration(final int localX, final int localY) {
		_mark(localX(localX), localY(localY), DECORATION_BLOCK);
	}

	public void markInteractive(final int localX, final int localY) {
		_mark(localX(localX), localY(localY), OBJECT_BLOCK);
	}

	public void markDeadBlock(final int localX, final int localY) {
		_mark(localX(localX), localY(localY), DEAD_BLOCK);
	}

	public void markWall(int localX, int localY, final int type, int orientation) {
		localX = localX(localX);
		localY = localY(localY);
		orientation %= 4;
		switch (type) {
		case 0:
			switch (orientation) {
			case 0:
				_mark(localX, localY, WEST);
				_mark(localX - 1, localY, EAST);
				break;
			case 1:
				_mark(localX, localY, NORTH);
				_mark(localX, localY + 1, SOUTH);
				break;
			case 2:
				_mark(localX, localY, EAST);
				_mark(localX + 1, localY, WEST);
				break;
			case 3:
				_mark(localX, localY, SOUTH);
				_mark(localX, localY - 1, NORTH);
				break;
			}
			break;
		case 2:
			switch (orientation) {
			case 0:
				_mark(localX, localY, NORTH.mark(WEST));
				_mark(localX - 1, localY, EAST);
				_mark(localX, 1 + localY, SOUTH);
				break;
			case 1:
				_mark(localX, localY, NORTH.mark(EAST));
				_mark(localX, 1 + localY, SOUTH);
				_mark(localX + 1, localY, WEST);
				break;
			case 2:
				_mark(localX, localY, SOUTH.mark(EAST));
				_mark(localX + 1, localY, WEST);
				_mark(localX, localY - 1, NORTH);
				break;
			case 3:
				_mark(localX, localY, SOUTH.mark(WEST));
				_mark(localX, localY - 1, NORTH);
				_mark(localX - 1, localY, EAST);
				break;
			}
			break;
		case 1:
		case 3:
			switch (orientation) {
			case 0:
				_mark(localX, localY, NORTHWEST);
				_mark(localX - 1, localY + 1, SOUTHEAST);
				break;
			case 1:
				_mark(localX, localY, NORTHEAST);
				_mark(localX + 1, 1 + localY, SOUTHWEST);
				break;
			case 2:
				_mark(localX, localY, SOUTHEAST);
				_mark(localX + 1, localY - 1, NORTHWEST);
				break;
			case 3:
				_mark(localX, localY, SOUTHWEST);
				_mark(localX - 1, localY - 1, NORTHEAST);
				break;
			}
			break;
		}
	}

	private void _mark(final int offsetX, final int offsetY, final CollisionFlag collisionFlag) {
		clipping[offsetX][offsetY].mark(collisionFlag);
	}

	private int localX(final int localX) {
		return localX - xOff;
	}

	private int localY(final int localY) {
		return localY - yOff;
	}
}
