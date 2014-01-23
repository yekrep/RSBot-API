package org.powerbot.os.api;

import org.powerbot.os.api.wrappers.Tile;
import org.powerbot.os.client.Client;

import java.awt.Point;

public class Calculations extends MethodProvider {
	private static final int[] ARRAY_SIN = new int[2048];
	private static final int[] ARRAY_COS = new int[2048];

	static {
		for (int i = 0; i < 2048; i++) {
			ARRAY_SIN[i] = (int) (65536.0d * Math.sin(i * 0.0030679615d));
			ARRAY_COS[i] = (int) (65536.0d * Math.cos(i * 0.0030679615d));
		}
	}

	public Calculations(final MethodContext ctx) {
		super(ctx);
	}

	private Point tileToMap(final Tile tile) {
		final Point r = new Point(-1, -1);
		final Client client = ctx.getClient();
		if (client == null) return r;
		final int gx = tile.x, gy = tile.y;
		final int rx = gx - client.getOffsetX(), ry = gy - client.getOffsetY();

		final int angle = client.getMinimapScale() + client.getMinimapAngle() & 0x7ff;
		int sin = ARRAY_SIN[angle], cos = ARRAY_COS[angle];
		final int offset = client.getMinimapOffset();
		sin = sin * 256 / (offset + 256);
		cos = cos * 256 / (offset + 256);

		final int rotated_x = rx * cos + sin * ry >> 16;
		final int rotated_y = ry * cos - sin * rx >> 16;
		return new Point(643 + (rotated_x * 4) - 2, 83 + (rotated_y * 4) - 2);
	}

	private int getHeight(final int relativeX, final int relativeY, int floor) {
		final Client client = ctx.getClient();
		int x = relativeX >> 7;
		int y = relativeY >> 7;
		if (client == null ||
				x < 0 || y < 0 || x > 103 || y > 103 ||
				floor < 0 || floor > 3) {
			return 0;
		}
		final byte[][][] meta = client.getLandscapeMeta();
		if (meta == null) return 0;
		if (floor < 3 && (meta[1][x][y] & 0x2) == 2) {
			floor++;
		}

		x &= 0x7f;
		y &= 0x7f;
		final int heightStart = x * meta[floor][(1 + x)][y] + meta[floor][x][y] * (128 - x) >> 7;
		final int heightEnd = (128 - x) * meta[floor][x][(1 + y)] + x * meta[floor][(1 + x)][(y + 1)] >> 7;
		return y * heightEnd + heightStart * (128 - y) >> 7;
	}

	private Point worldToScreen(final int relativeX, final int relativeY, final int height) {
		final Client client = ctx.getClient();
		final Point r = new Point(-1, -1);
		if (relativeX < 128 || relativeX > 13056 ||
				relativeY < 128 || relativeY > 13056) {
			return r;
		}
		final int floor = client.getFloor();
		if (floor < 0) return r;
		final int averageHeight = getHeight(relativeX, relativeY, floor);
		final int worldHeight = averageHeight - height;
		final int projectedX = relativeX - client.getCameraX(), projectedZ = relativeY - client.getCameraZ(),
				projectedY = worldHeight - client.getCameraY();
		final int pitch = client.getCameraPitch(), yaw = client.getCameraYaw();
		final int pitchSin = ARRAY_SIN[pitch], pitchCos = ARRAY_COS[pitch];
		final int yawSin = ARRAY_SIN[yaw], yawCos = ARRAY_COS[yaw];

		final int rotatedX = yawSin * projectedZ + yawCos * projectedX >> 16;
		final int rotatedZ = yawCos * projectedZ - yawSin * projectedX >> 16;
		final int rolledY = pitchCos * projectedY - yawSin * rotatedZ >> 16;
		final int rolledZ = pitchCos * rotatedZ + pitchSin * projectedY >> 16;
		if (rolledZ >= 50) {
			return new Point(
					(rotatedX << 9) / rolledZ + 256,
					(167 + (rolledY << 9) / rolledZ)
			);
		}
		return r;
	}
}
