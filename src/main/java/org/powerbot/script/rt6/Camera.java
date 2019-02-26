package org.powerbot.script.rt6;

import org.powerbot.script.Condition;
import org.powerbot.script.Locatable;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;

/**
 * Camera
 * Utilities pertaining to the camera.
 */
public class Camera extends ClientAccessor {
	public Camera(final ClientContext factory) {
		super(factory);
	}

	public float rotation() {
		float yaw;
		final Game.Matrix4f matrix = new Game.Matrix4f();
		Game.Matrix4f.inversion(ctx.game.getViewMatrix(), matrix);
		if (matrix.m10 > 0.998) {
			yaw = (float) Math.atan2(matrix.m02, matrix.m22);
		} else if (matrix.m10 < -0.998) {
			yaw = (float) Math.atan2(matrix.m02, matrix.m22);
		} else {
			yaw = (float) Math.atan2(-matrix.m20, matrix.m00);
		}
		if (yaw > 0) {
			yaw -= 6.2831855f;
		}
		return -yaw;
	}

	/**
	 * Determines the current camera yaw (angle of rotation).
	 *
	 * @return the camera yaw
	 */
	public int yaw() {
		float yaw = rotation();
		yaw *= 180.0 / Math.PI;
		return Math.round(yaw) % 360;
	}

	/**
	 * Determines the current camera pitch.
	 *
	 * @return the camera pitch
	 */
	public int pitch() {
		float pitch;
		final Game.Matrix4f matrix = new Game.Matrix4f();
		Game.Matrix4f.inversion(ctx.game.getViewMatrix(), matrix);
		if (matrix.m10 > 0.998) {
			pitch = 1.5707964f;
		} else if (matrix.m10 < -0.998) {
			pitch = -1.5707964f;
		} else {
			pitch = (float) Math.atan2(-matrix.m12, matrix.m11);
		}
		if (pitch > 0) {
			pitch -= 6.2831855f;
		}
		pitch *= -1;
		return (int) Math.round(pitch * 100d / (Math.PI / 2));
	}

	/**
	 * Sets the camera pitch to one absolute, up or down.
	 *
	 * @param up {@code true} to be up; otherwise {@code false} for down
	 * @return {@code true} if the absolute was reached; success is normally guaranteed regardless of return of {@code false}
	 */
	public boolean pitch(final boolean up) {
		return pitch(up ? 100 : 0);
	}
	
	/**
	 * Sets the camera pitch to one absolute, up or down.
	 *
	 * @param up {@code true} to be up; otherwise {@code false} for down
	 * @param wasd    use wasd or directional keys
	 * @return {@code true} if the absolute was reached; success is normally guaranteed regardless of return of {@code false}
	 */
	public boolean pitch(final boolean up, final boolean wasd) {
		return pitch(up ? 100 : 0, wasd);
	}

	/**
	 * Sets the camera pitch the desired percentage.
	 *
	 * @param percent the percent to set the pitch to
	 * @return {@code true} if the pitch was reached; otherwise {@code false}
	 */
	public boolean pitch(final int percent) {
		return pitch(percent, false);
	}
	
	/**
     	* Sets the camera pitch the desired percentage.
     	*
     	* @param percent the percent to set the pitch to
     	* @param wasd    use wasd or directional keys
     	* @return {@code true} if the pitch was reached; otherwise {@code false}
     	*/
    	public boolean pitch(final int percent, final boolean wasd) {
		boolean useWasd = wasd;
		if (wasd) {
			if (ctx.game.chatAlwaysOn()) {
				useWasd = false;	
			}
		}
		if (percent == pitch()) {
			return true;
		}
		final boolean up = pitch() < percent;
		String stringToSend;
        	if (up) {
            		stringToSend = useWasd ? "{VK_W down}" : "{VK_UP down}";
        	} else {
            		stringToSend = useWasd ? "{VK_S down}" : "{VK_DOWN down}";
        	}
        	ctx.input.send(stringToSend);
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
		String s;
        	if (up) {
            		s = useWasd ? "{VK_W up}" : "{VK_UP up}";
        	} else {
            		s = useWasd ? "{VK_S up}" : "{VK_DOWN up}";
        	}
        	ctx.input.send(s);
		return Math.abs(percent - pitch()) <= 8;
	}

	/**
	 * Changes the yaw (angle) of the camera.
	 *
	 * @param direction the direction to set the camera, 'n', 's', 'w', 'e'.     \
	 * @return {@code true} if the camera was rotated to the angle; otherwise {@code false}
	 */
	public boolean angle(final char direction) {
		return angle(direction, false);
	}
	
	/**
	 * Changes the yaw (angle) of the camera.
	 *
	 * @param direction the direction to set the camera, 'n', 's', 'w', 'e'.
	 * @param wasd use wasd or directional keys
	 * @return {@code true} if the camera was rotated to the angle; otherwise {@code false}
	 */
	public boolean angle(final char direction, final boolean wasd) {
		switch (direction) {
		case 'n':
			return angle(0, wasd);
		case 'w':
			return angle(90, wasd);
		case 's':
			return angle(180, wasd);
		case 'e':
			return angle(270, wasd);
		}
		throw new RuntimeException("invalid direction " + direction + ", expecting n,w,s,e");
	}

	/**
	 * Changes the yaw (angle) of the camera.
	 *
	 * @param degrees the degrees to set the camera to
	 * @return {@code true} if the camera was rotated to the angle; otherwise {@code false}
	 */
	public boolean angle(final int degrees) {
		return angle(degrees, false);
	}
	
	/**
	 * Changes the yaw (angle) of the camera.
	 *
	 * @param degrees the degrees to set the camera to
	 * @param wasd use wasd or directional keys
	 * @return {@code true} if the camera was rotated to the angle; otherwise {@code false}
	 */
	public boolean angle(final int degrees, final boolean wasd) {
		boolean useWasd = wasd;
		if (wasd) {
			if (ctx.game.chatAlwaysOn()) {
				useWasd = false;	
			}
		}
		final int d = degrees % 360;
		final int a = angleTo(d);
		if (Math.abs(a) <= 8) {
			return true;
		}
		final boolean l = a > 8;

		String stringToSend;
        	if (l) {
            		stringToSend = useWasd ? "{VK_A down}" : "{VK_LEFT down}";
        	} else {
            		stringToSend = useWasd ? "{VK_D down}" : "{VK_RIGHT down}";
        	}
        	ctx.input.send(stringToSend);
		final int dir = (int) Math.signum(angleTo(d));
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
		if (l) {
            		stringToSend = useWasd ? "{VK_A up}" : "{VK_LEFT up}";
        	} else {
            		stringToSend = useWasd ? "{VK_D up}" : "{VK_RIGHT up}";
        	}
        	ctx.input.send(stringToSend);
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
		turnTo(l, 0, false);
	}

	/**
	 * Turns to the specified {@link Locatable} with the provided deviation.
	 *
	 * @param l   the {@link Locatable} to turn to
	 * @param dev the yaw deviation
	 */
	public void turnTo(final Locatable l, final int dev) {
		turnTo(l, dev, false);
	}
	
	/**
	 * Turns to the specified {@link org.powerbot.script.Locatable}.
	 *
	 * @param l the {@link org.powerbot.script.Locatable} to turn to
	 * @param wasd use wasd or directional keys
	 */
	public void turnTo(final Locatable l, final boolean wasd) {
		turnTo(l, 0, wasd);
	}
	
	/**
	 * Turns to the specified {@link Locatable} with the provided deviation.
	 *
	 * @param l   the {@link Locatable} to turn to
	 * @param dev the yaw deviation
	 * @param wasd use wasd or directional keys
	 */
	public void turnTo(final Locatable l, final int dev, final boolean wasd) {
		boolean useWasd = wasd;
		if (wasd) {
			if (ctx.game.chatAlwaysOn()) {
				useWasd = false;	
			}
		}
		final int a = getAngleToLocatable(l);
		if (dev == 0) {
			angle(a, useWasd);
		} else {
			angle(Random.nextInt(a - dev, a + dev + 1), useWasd);
		}
	}

	private int getAngleToLocatable(final Locatable mobile) {
		final Player local = ctx.players.local();
		final Tile t1 = local != null ? local.tile() : null;
		final Tile t2 = mobile.tile();
		return t1 != null && t2 != null ? ((int) Math.toDegrees(Math.atan2(t2.y() - t1.y(), t2.x() - t1.x()))) - 90 : 0;
	}
}
