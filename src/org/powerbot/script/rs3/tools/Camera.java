package org.powerbot.script.rs3.tools;

import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.util.math.Vector3f;

/**
 * Utilities pertaining to the camera.
 *
 */
@SuppressWarnings("deprecation")
public class Camera extends MethodProvider {
	public Vector3f offset;
	public Vector3f center;

	public Camera(final MethodContext factory) {
		super(factory);
		offset = new Vector3f(0, 0, 0);
		center = new Vector3f(0, 0, 0);
	}

	/**
	 * Returns the camera offset on the x-axis.
	 *
	 * @return the offset on the x-axis
	 */
	public int getX() {
		final Tile tile = ctx.game.getMapBase();
		return (int) (offset.x - (tile.getX() << 9));
	}

	/**
	 * Returns the camera offset on the y-axis.
	 *
	 * @return the offset on the y-axis
	 */
	public int getY() {
		final Tile tile = ctx.game.getMapBase();
		return (int) (offset.y - (tile.getY() << 9));
	}

	/**
	 * Returns the camera offset on the z-axis.
	 *
	 * @return the offset on the z-axis
	 */
	public int getZ() {
		return -(int) offset.z;
	}

	/**
	 * Determines the current camera yaw (angle of rotation).
	 *
	 * @return the camera yaw
	 */
	public int getYaw() {
		final float deltaX = offset.x - center.x;
		final float deltaY = offset.y - center.y;
		final float theta = (float) Math.atan2(deltaX, deltaY);
		return (int) (((int) ((Math.PI - theta) * 2607.5945876176133D) & 0x3FFF) / 45.51);
	}

	/**
	 * Determines the current camera pitch.
	 *
	 * @return the camera pitch
	 */
	public final int getPitch() {
		final float deltaX = center.x - offset.x;
		final float deltaY = center.y - offset.y;
		final float deltaZ = center.z - offset.z;
		final float dist = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
		final float theta = (float) Math.atan2(-deltaZ, dist);
		return (int) (((int) (theta * 2607.5945876176133D) & 0x3FFF) / 4096f * 100f);
	}

	/**
	 * Sets the camera pitch to one absolute, up or down.
	 *
	 * @param up <tt>true</tt> to be up; otherwise <tt>false</tt> for down
	 * @return <tt>true</tt> if the absolute was reached; success is normally guaranteed regardless of return of <tt>false</tt>
	 */
	public boolean setPitch(final boolean up) {
		return setPitch(up ? 100 : 0);
	}

	/**
	 * Sets the camera pitch the desired percentage.
	 *
	 * @param percent the percent to set the pitch to
	 * @return <tt>true</tt> if the pitch was reached; otherwise <tt>false</tt>
	 */
	public boolean setPitch(final int percent) {
		int curAlt = getPitch();
		int lastAlt = 0;
		if (curAlt == percent) {
			return true;
		}

		final boolean up = curAlt < percent;
		ctx.keyboard.send(up ? "{VK_UP down}" : "{VK_DOWN down}");
		final Timer timer = new Timer(100);
		while (timer.isRunning()) {
			if (lastAlt != curAlt) {
				timer.reset();
			}

			lastAlt = curAlt;
			sleep(Random.nextInt(5, 10));
			curAlt = getPitch();

			if (up && curAlt >= percent) {
				break;
			} else if (!up && curAlt <= percent) {
				break;
			}
		}

		ctx.keyboard.send(up ? "{VK_UP up}" : "{VK_DOWN up}");
		return curAlt == percent;
	}

	/**
	 * Changes the yaw (angle) of the camera.
	 *
	 * @param direction the direction to set the camera, 'n', 's', 'w', 'e'.     \
	 * @return <tt>true</tt> if the camera was rotated to the angle; otherwise <tt>false</tt>
	 */
	public boolean setAngle(final char direction) {
		switch (direction) {
		case 'n':
			return setAngle(0);
		case 'w':
			return setAngle(90);
		case 's':
			return setAngle(180);
		case 'e':
			return setAngle(270);
		}
		throw new RuntimeException("invalid direction " + direction + ", expecting n,w,s,e");
	}

	/**
	 * Changes the yaw (angle) of the camera.
	 *
	 * @param degrees the degrees to set the camera to
	 * @return <tt>true</tt> if the camera was rotated to the angle; otherwise <tt>false</tt>
	 */
	public boolean setAngle(int degrees) {
		degrees %= 360;
		if (getAngleTo(degrees) > 5) {
			ctx.keyboard.send("{VK_LEFT down}");
			final Timer timer = new Timer(500);
			int ang, prev = -1;
			while ((ang = getAngleTo(degrees)) > 15 && timer.isRunning()) {
				if (ang != prev) {
					timer.reset();
				}
				prev = ang;
				sleep(10, 15);
			}
			ctx.keyboard.send("{VK_LEFT up}");
		} else if (getAngleTo(degrees) < -5) {
			ctx.keyboard.send("{VK_RIGHT down}");
			final Timer timer = new Timer(500);
			int ang, prev = -1;
			while ((ang = getAngleTo(degrees)) < -15 && timer.isRunning()) {
				if (ang != prev) {
					timer.reset();
				}
				prev = ang;
				sleep(10, 15);
			}
			ctx.keyboard.send("{VK_RIGHT up}");
		}
		return Math.abs(getAngleTo(degrees)) < 15;
	}

	/**
	 * Gets the angle change to the specified degrees.
	 *
	 * @param degrees the degrees to compute to
	 * @return the angle change required to be at the provided degrees
	 */
	public int getAngleTo(final int degrees) {
		int ca = getYaw();
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
	 * Turns to the specified {@link Locatable}.
	 *
	 * @param l the {@link Locatable} to turn to
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
			setAngle(a);
		} else {
			setAngle(Random.nextInt(a - dev, a + dev + 1));
		}
	}

	private int getAngleToLocatable(final Locatable mobile) {
		final Player local = ctx.players.local();
		final Tile t1 = local != null ? local.getLocation() : null;
		final Tile t2 = mobile.getLocation();
		return t1 != null && t2 != null ? ((int) Math.toDegrees(Math.atan2(t2.getY() - t1.getY(), t2.getX() - t1.getX()))) - 90 : 0;
	}
}