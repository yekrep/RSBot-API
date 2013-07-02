package org.powerbot.script.internal.wrappers;

import static org.powerbot.script.internal.wrappers.ClippingValue.*;

public final class ClippingMap {
	public ClippingMap(final int localXSize, final int localYSize) {
		this.xReadableOffset = -1;
		this.yReadableOffset = -1;
		this.xSize = localXSize + 6;
		this.ySize = localYSize + 6;
		this.clipping = new ClippingValue[xSize][ySize];
		clear();
	}

	private final ClippingValue[][] clipping;
	private final int xReadableOffset;
	private final int yReadableOffset;
	private final int xSize;
	private final int ySize;

	public int getSizeX() {
		return xSize;
	}

	public int getSizeY() {
		return ySize;
	}

	public void clear() {
		for (int x = 0; x < this.xSize; x++) {
			for (int y = 0; y < this.ySize; y++) {
				if ((x <= 1) || (y <= 1) || (x >= this.xSize - 6) || (y >= this.ySize - 6)) {
					this.clipping[x][y] = ClippingValue.PADDING;
				} else {
					this.clipping[x][y] = ClippingValue.createNewMarkable();
				}
			}
		}
	}

	public ClippingValue getClippingValueAtLocal(final int localX, final int localY) {
		return clipping[offsetLocalX(localX)][offsetLocalY(localY)];
	}

	public int offsetLocalX(final int localX) {
		return localX - xReadableOffset;
	}

	public int offsetLocalY(final int localY) {
		return localY - yReadableOffset;
	}

	public void markDecoration(int localX, int localY) {
		_mark(offsetLocalX(localX), offsetLocalY(localY), DECORATION_BLOCK);
	}

	private void _mark(final int offsetX, final int offsetY, final ClippingValue clippingValue) {
		clipping[offsetX][offsetY].mark(clippingValue);
	}

	public void markInteractiveArea(
			int localX, int localY, final int xSize, final int ySize, final boolean allowsRanged) {
		ClippingValue clippingValue = OBJECT_TILE;
		if (allowsRanged) {
			clippingValue = clippingValue.mark(OBJECT_ALLOW_RANGE);
		}
		localX = offsetLocalX(localX);
		localY = offsetLocalY(localY);
		for (int xPos = localX; xPos < localX + xSize; xPos++) {
			if ((xPos < 0) || (xPos >= this.xSize)) {
				continue;
			}
			for (int yPos = localY; yPos < ySize + localY; yPos++) {
				if ((yPos < 0) || (yPos >= this.ySize)) {
					continue;
				}
				_mark(xPos, yPos, clippingValue);
			}
		}
	}

	public void markInteractive(
			int localX, int localY, final boolean allowsRanged) {
		ClippingValue clippingValue = OBJECT_TILE;
		if (allowsRanged) {
			clippingValue = clippingValue.mark(OBJECT_ALLOW_RANGE);
		}
		_mark(offsetLocalX(localX), offsetLocalY(localY), clippingValue);
	}

	public void markDeadBlock(int localX, int localY) {
		_mark(offsetLocalX(localX), offsetLocalY(localY), OBJECT_BLOCK);
	}

	public void markWall(
			int localX, int localY, final int type, int orientation, final boolean objectAllowsRanged) {
		localX = offsetLocalX(localX);
		localY = offsetLocalY(localY);
		orientation %= 4;
		if (0 == type) {
			if (orientation == 0) {
				_mark(localX, localY, WEST);
				_mark(localX - 1, localY, EAST);
			} else if (1 == orientation) {
				_mark(localX, localY, NORTH);
				_mark(localX, localY + 1, SOUTH);
			} else if (2 == orientation) {
				_mark(localX, localY, EAST);
				_mark(localX + 1, localY, WEST);
			} else if (3 == orientation) {
				_mark(localX, localY, SOUTH);
				_mark(localX, localY - 1, NORTH);
			}
		} else if ((1 == type) || (type == 3)) {
			if (orientation == 0) {
				_mark(localX, localY, NORTHWEST);
				_mark(localX - 1, localY + 1, SOUTHEAST);
			} else if (1 == orientation) {
				_mark(localX, localY, NORTHEAST);
				_mark(localX + 1, 1 + localY, SOUTHWEST);
			} else if (2 == orientation) {
				_mark(localX, localY, SOUTHEAST);
				_mark(localX + 1, localY - 1, NORTHWEST);
			} else if (orientation == 3) {
				_mark(localX, localY, SOUTHWEST);
				_mark(localX - 1, localY - 1, NORTHEAST);
			}
		} else if (2 == type) {
			if (orientation == 0) {
				_mark(localX, localY, NORTH.mark(WEST));
				_mark(localX - 1, localY, EAST);
				_mark(localX, 1 + localY, SOUTH);
			} else if (orientation == 1) {
				_mark(localX, localY, NORTH.mark(EAST));
				_mark(localX, 1 + localY, SOUTH);
				_mark(localX + 1, localY, WEST);
			} else if (orientation == 2) {
				_mark(localX, localY, SOUTH.mark(EAST));
				_mark(localX + 1, localY, WEST);
				_mark(localX, localY - 1, NORTH);
			} else if (orientation == 3) {
				_mark(localX, localY, SOUTH.mark(WEST));
				_mark(localX, localY - 1, NORTH);
				_mark(localX - 1, localY, EAST);
			}
		}
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
