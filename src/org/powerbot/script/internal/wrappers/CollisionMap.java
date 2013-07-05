package org.powerbot.script.internal.wrappers;

import static org.powerbot.script.internal.wrappers.CollisionFlag.*;

public final class CollisionMap {
	public CollisionMap(final int localXSize, final int localYSize) {
		this.xOff = -1;
		this.yOff = -1;
		this.width = localXSize + 6;
		this.height = localYSize + 6;
		this.clipping = new CollisionFlag[width][height];
		clear();
	}

	private final CollisionFlag[][] clipping;
	private final int xOff;
	private final int yOff;
	private final int width;
	private final int height;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
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

	public CollisionFlag getClippingValueAtLocal(final int localX, final int localY) {
		return clipping[offsetLocalX(localX)][offsetLocalY(localY)];
	}

	public int offsetLocalX(final int localX) {
		return localX - xOff;
	}

	public int offsetLocalY(final int localY) {
		return localY - yOff;
	}

	public void markDecoration(int localX, int localY) {
		_mark(offsetLocalX(localX), offsetLocalY(localY), DECORATION_BLOCK);
	}

	private void _mark(final int offsetX, final int offsetY, final CollisionFlag collisionFlag) {
		clipping[offsetX][offsetY].mark(collisionFlag);
	}

	public void markInteractiveArea(int localX, int localY, final int xSize, final int ySize, final boolean allowsRanged) {
		CollisionFlag collisionFlag = OBJECT_BLOCK;
		if (allowsRanged) {
			collisionFlag = collisionFlag.mark(OBJECT_ALLOW_RANGE);
		}
		localX = offsetLocalX(localX);
		localY = offsetLocalY(localY);
		for (int xPos = localX; xPos < localX + xSize; xPos++) {
			if ((xPos < 0) || (xPos >= this.width)) {
				continue;
			}
			for (int yPos = localY; yPos < ySize + localY; yPos++) {
				if ((yPos < 0) || (yPos >= this.height)) {
					continue;
				}
				_mark(xPos, yPos, collisionFlag);
			}
		}
	}

	public void markInteractive(int localX, int localY, final boolean allowsRanged) {
		CollisionFlag collisionFlag = OBJECT_BLOCK;
		if (allowsRanged) {
			collisionFlag = collisionFlag.mark(OBJECT_ALLOW_RANGE);
		}
		_mark(offsetLocalX(localX), offsetLocalY(localY), collisionFlag);
	}

	public void markDeadBlock(int localX, int localY) {
		_mark(offsetLocalX(localX), offsetLocalY(localY), DEAD_BLOCK);
	}

	public void markWall(int localX, int localY, final int type, int orientation, final boolean objectAllowsRanged) {
		localX = offsetLocalX(localX);
		localY = offsetLocalY(localY);
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

		//TODO: scrap / integrate
		if (objectAllowsRanged) {
			if (0 == type) {
				if (0 == orientation) {
					_mark(localX, localY, ALLOW_RANGE_WEST);
					_mark(localX - 1, localY, ALLOW_RANGE_EAST);
				} else if (1 == orientation) {
					_mark(localX, localY, ALLOW_RANGE_NORTH);
					_mark(localX, localY + 1, ALLOW_RANGE_SOUTH);
				} else if (orientation == 2) {
					_mark(localX, localY, ALLOW_RANGE_EAST);
					_mark(1 + localX, localY, ALLOW_RANGE_WEST);
				} else if (3 == orientation) {
					_mark(localX, localY, ALLOW_RANGE_SOUTH);
					_mark(localX, localY - 1, ALLOW_RANGE_NORTH);
				}
			} else if ((type == 1) || (3 == type)) {
				if (0 == orientation) {
					_mark(localX, localY, ALLOW_RANGE_NORTHWEST);
					_mark(localX - 1, 1 + localY, ALLOW_RANGE_SOUTHEAST);
				} else if (1 == orientation) {
					_mark(localX, localY, ALLOW_RANGE_NORTHEAST);
					_mark(1 + localX, localY + 1, ALLOW_RANGE_SOUTHWEST);
				} else if (2 == orientation) {
					_mark(localX, localY, ALLOW_RANGE_SOUTHEAST);
					_mark(1 + localX, localY - 1, ALLOW_RANGE_NORTHWEST);
				} else if (orientation == 3) {
					_mark(localX, localY, ALLOW_RANGE_SOUTHWEST);
					_mark(localX - 1, localY - 1, ALLOW_RANGE_NORTHEAST);
				}
			} else if (2 == type) {
				if (orientation == 0) {
					_mark(localX, localY, ALLOW_RANGE_NORTH.mark(ALLOW_RANGE_WEST));
					_mark(localX - 1, localY, ALLOW_RANGE_EAST);
					_mark(localX, localY + 1, ALLOW_RANGE_SOUTH);
				} else if (1 == orientation) {
					_mark(localX, localY, ALLOW_RANGE_NORTH.mark(ALLOW_RANGE_EAST));
					_mark(localX, 1 + localY, ALLOW_RANGE_SOUTH);
					_mark(1 + localX, localY, ALLOW_RANGE_WEST);
				} else if (2 == orientation) {
					_mark(localX, localY, ALLOW_RANGE_SOUTH.mark(ALLOW_RANGE_EAST));
					_mark(1 + localX, localY, ALLOW_RANGE_WEST);
					_mark(localX, localY - 1, ALLOW_RANGE_NORTH);
				} else if (orientation == 3) {
					_mark(localX, localY, ALLOW_RANGE_SOUTH.mark(ALLOW_RANGE_WEST));
					_mark(localX, localY - 1, ALLOW_RANGE_NORTH);
					_mark(localX - 1, localY, ALLOW_RANGE_EAST);
				}
			}
		}
	}
}
