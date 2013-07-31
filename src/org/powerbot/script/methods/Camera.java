package org.powerbot.script.methods;

import org.powerbot.client.Client;
import org.powerbot.script.lang.Locatable;
import org.powerbot.script.util.Delay;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

public class Camera extends MethodProvider {
	public Camera(MethodContext factory) {
		super(factory);
	}

	public int getYaw() {
		Client client = ctx.getClient();
		return client != null ? (int) (ctx.game.mapAngle / 45.51) : -1;
	}

	public void setAngle(final char direction) {
		switch (direction) {
		case 'n':
			setAngle(0);
			break;
		case 'w':
			setAngle(90);
			break;
		case 's':
			setAngle(180);
			break;
		case 'e':
			setAngle(270);
			break;
		default:
			throw new RuntimeException("invalid direction " + direction + ", expecting n,w,s,e");
		}
	}

	public void setAngle(int degrees) {
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
				Delay.sleep(10, 15);
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
				Delay.sleep(10, 15);
			}
			ctx.keyboard.send("{VK_RIGHT up}");
		}
	}

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

	public void turnTo(final Locatable l) {
		turnTo(l, 0);
	}

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