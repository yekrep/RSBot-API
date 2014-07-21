package org.powerbot.script.rt6;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.Locatable;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;

/**
 * Utilities pertaining to the camera.
 */
@SuppressWarnings("deprecation")
public class Camera extends ClientAccessor {
	public float[] offset, center;

	public Camera(final ClientContext factory) {
		super(factory);
		offset = new float[3];
		center = new float[3];
	}

	/**
	 * Returns the camera offset on the x-axis.
	 *
	 * @return the offset on the x-axis
	 */
	public int x() {
		final Tile tile = ctx.game.mapOffset();
		return (int) (offset[0] - (tile.x() << 9));
	}

	/**
	 * Returns the camera offset on the y-axis.
	 *
	 * @return the offset on the y-axis
	 */
	public int y() {
		final Tile tile = ctx.game.mapOffset();
		return (int) (offset[1] - (tile.y() << 9));
	}

	/**
	 * Returns the camera offset on the z-axis.
	 *
	 * @return the offset on the z-axis
	 */
	public int z() {
		return -(int) offset[2];
	}

	/**
	 * Determines the current camera yaw (angle of rotation).
	 *
	 * @return the camera yaw
	 */
	public int yaw() {
		final float dx = offset[0] - center[0];
		final float dy = offset[1] - center[1];
		final float t = (float) Math.atan2(dx, dy);
		return (int) (((int) ((Math.PI - t) * 2607.5945876176133D) & 0x3fff) / 45.51);
	}

	/**
	 * Determines the current camera pitch.
	 *
	 * @return the camera pitch
	 */
	public final int pitch() {
		final float dx = center[0] - offset[0];
		final float dy = center[1] - offset[1];
		final float dz = center[2] - offset[2];
		final float s = (float) Math.sqrt(dx * dx + dy * dy);
		final float t = (float) Math.atan2(-dz, s);
		return (int) (((int) (t * 2607.5945876176133D) & 0x3fff) / 4096f * 100f);
	}

	/**
	 * Sets the camera pitch to one absolute, up or down.
	 *
	 * @param up <tt>true</tt> to be up; otherwise <tt>false</tt> for down
	 * @return <tt>true</tt> if the absolute was reached; success is normally guaranteed regardless of return of <tt>false</tt>
	 */
	public boolean pitch(final boolean up) {
		return pitch(up ? 100 : 0);
	}

	/**
	 * Sets the camera pitch the desired percentage.
	 *
	 * @param percent the percent to set the pitch to
	 * @return <tt>true</tt> if the pitch was reached; otherwise <tt>false</tt>
	 */
	public boolean pitch(final int percent) {
		if (percent == pitch()) {
			return true;
		}
		final boolean up = pitch() < percent;
		ctx.input.send(up ? "{VK_UP down}" : "{VK_DOWN down}");
		for (; ; ) {
			final int tp = pitch();
			if (!Condition.wait(new Condition.Check() {
				@Override
				public boolean poll() {
					return pitch() != tp;
				}
			}, 10, 10)) {
				break;
			}
			final int p = pitch();
			if (up && p >= percent) {
				break;
			} else if (!up && p <= percent) {
				break;
			}
		}
		ctx.input.send(up ? "{VK_UP up}" : "{VK_DOWN up}");
		return Math.abs(percent - pitch()) <= 8;
	}

	/**
	 * Changes the yaw (angle) of the camera.
	 *
	 * @param direction the direction to set the camera, 'n', 's', 'w', 'e'.     \
	 * @return <tt>true</tt> if the camera was rotated to the angle; otherwise <tt>false</tt>
	 */
	public boolean angle(final char direction) {
		switch (direction) {
		case 'n':
			return angle(0);
		case 'w':
			return angle(90);
		case 's':
			return angle(180);
		case 'e':
			return angle(270);
		}
		throw new RuntimeException("invalid direction " + direction + ", expecting n,w,s,e");
	}

	/**
	 * Changes the yaw (angle) of the camera.
	 *
	 * @param degrees the degrees to set the camera to
	 * @return <tt>true</tt> if the camera was rotated to the angle; otherwise <tt>false</tt>
	 */
	public boolean angle(final int degrees) {
		final int d = degrees % 360;
		final int a = angleTo(d);
		if (Math.abs(a) <= 8) {
			return true;
		}
		final boolean l = a > 8;

		ctx.input.send(l ? "{VK_LEFT down}" : "{VK_RIGHT down}");
		final float dir = Math.signum(angleTo(d));
		for (; ; ) {
			final int a2 = angleTo(d);
			if (!Condition.wait(new Condition.Check() {
				@Override
				public boolean poll() {
					return angleTo(d) != a2;
				}
			}, 10, 10)) {
				break;
			}
			final int at = angleTo(d);
			if (Math.abs(at) <= 15 || Math.signum(at) != dir) {
				break;
			}
		}
		ctx.input.send(l ? "{VK_LEFT up}" : "{VK_RIGHT up}");
		return Math.abs(angleTo(d)) <= 15;
	}

	/**
	 * Gets the angle change to the specified degrees.
	 *
	 * @param degrees the degrees to compute to
	 * @return the angle change required to be at the provided degrees
	 */
	public int angleTo(final int degrees) {
		int ca = yaw();
		if (ca < degrees) {
			ca += 360;
		}
		int da = ca - degrees;
		if (da > 180) {
			da -= 360;
		}
		return da;
	}

	/**
	 * Turns to the specified {@link org.powerbot.script.Locatable}.
	 *
	 * @param l the {@link org.powerbot.script.Locatable} to turn to
	 */
	public void turnTo(final Locatable l) {
		turnTo(l, 0);
	}

	/**
	 * Turns to the specified {@link Locatable} with the provided deviation.
	 *
	 * @param l   the {@link Locatable} to turn to
	 * @param dev the yaw deviation
	 */
	public void turnTo(final Locatable l, final int dev) {
		final int a = getAngleToLocatable(l);
		if (dev == 0) {
			angle(a);
		} else {
			angle(Random.nextInt(a - dev, a + dev + 1));
		}
	}

	private int getAngleToLocatable(final Locatable mobile) {
		final Player local = ctx.players.local();
		final Tile t1 = local != null ? local.tile() : null;
		final Tile t2 = mobile.tile();
		return t1 != null && t2 != null ? ((int) Math.toDegrees(Math.atan2(t2.y() - t1.y(), t2.x() - t1.x()))) - 90 : 0;
	}
}
