package org.powerbot.os.api;

import org.powerbot.os.api.wrappers.HintArrow;
import org.powerbot.os.api.wrappers.RelativePosition;
import org.powerbot.os.api.wrappers.Tile;
import org.powerbot.os.client.Client;

import java.awt.*;

public class Game extends MethodProvider {
	private static final int[] ARRAY_SIN = new int[2048];
	private static final int[] ARRAY_COS = new int[2048];

	static {
		for (int i = 0; i < 2048; i++) {
			ARRAY_SIN[i] = (int) (65536.0d * Math.sin(i * 0.0030679615d));
			ARRAY_COS[i] = (int) (65536.0d * Math.cos(i * 0.0030679615d));
		}
	}

	public Game(final MethodContext ctx) {
		super(ctx);
	}

	public HintArrow getHintArrow() {
		final HintArrow r = new HintArrow();
		final Client client = ctx.getClient();
		if (client == null) return r;
		return r;
	}

	private Point tileToMap(final Tile tile) {
		final Point r = new Point(-1, -1);
		final Client client = ctx.getClient();
		if (client == null) return r;
		final RelativePosition rel = ctx.players.getLocal().getRelativePosition();
		final int angle = client.getMinimapScale() + client.getMinimapAngle() & 0x7ff;
		final int[] d = {tile.x, tile.y, ARRAY_SIN[angle], ARRAY_COS[angle]};
		d[0] = (d[0] - client.getOffsetX()) * 4 + 2 - rel.x / 32;
		d[1] = (d[1] - client.getOffsetY()) * 4 + 2 - rel.z / 32;
		final int offset = client.getMinimapOffset();
		d[2] = d[2] * 256 / (offset + 256);
		d[3] = d[3] * 256 / (offset + 256);
		d[0] = d[0] * d[3] + d[2] * d[1] >> 16;
		d[1] = d[1] * d[3] - d[2] * d[0] >> 16;
		return new Point(643 + d[0], 83 + d[1]);
	}

	private int getHeight(final int relativeX, final int relativeY, int floor) {
		final Client client = ctx.getClient();
		int x = relativeX >> 7;
		int y = relativeY >> 7;
		if (client == null || x < 0 || y < 0 || x > 103 || y > 103 ||
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

	private Point worldToScreen(final int relativeX, final int relativeY, final int h) {
		final Client client = ctx.getClient();
		final Point r = new Point(-1, -1);
		if (relativeX < 128 || relativeX > 13056 ||
				relativeY < 128 || relativeY > 13056) {
			return r;
		}
		final int floor = client.getFloor();
		if (floor < 0) return r;
		final int height = getHeight(relativeX, relativeY, floor) - h;
		final int projectedX = relativeX - client.getCameraX(), projectedZ = relativeY - client.getCameraZ(),
				projectedY = height - client.getCameraY();
		final int pitch = client.getCameraPitch(), yaw = client.getCameraYaw();
		final int[] c = {ARRAY_SIN[yaw], ARRAY_COS[yaw], ARRAY_SIN[pitch], ARRAY_COS[pitch]};
		final int rotatedX = c[0] * projectedZ + c[1] * projectedX >> 16;
		final int rotatedZ = c[1] * projectedZ - c[0] * projectedX >> 16;
		final int rolledY = c[3] * projectedY - c[2] * rotatedZ >> 16;
		final int rolledZ = c[3] * rotatedZ + c[2] * projectedY >> 16;
		if (rolledZ >= 50) {
			return new Point(
					(rotatedX << 9) / rolledZ + 256,
					(rolledY << 9) / rolledZ + 167
			);
		}
		return r;
	}
}
