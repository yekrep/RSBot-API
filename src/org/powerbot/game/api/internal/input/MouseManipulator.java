package org.powerbot.game.api.internal.input;

import java.awt.Canvas;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.powerbot.concurrent.Task;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.bot.Bot;

/**
 * @author Timer
 */
public class MouseManipulator implements Task {
	private final List<ForceModifier> forceModifiers = new ArrayList<ForceModifier>(5);
	private final Vector velocity = new Vector();
	private final long timeout;
	private boolean running;
	private final Locatable locatable;
	private final Filter<Point> filter;
	private boolean accepted = false;
	private final org.powerbot.game.client.input.Mouse clientMouse;

	public MouseManipulator(final Locatable locatable, final Filter<Point> filter) {
		this.timeout = Random.nextInt(4000, 7000);
		this.running = false;
		this.locatable = locatable;
		this.filter = filter;
		this.clientMouse = Bot.resolve().getClient().getMouse();
	}

	public void run() {
		this.running = true;
		configureModifiers();
		final long start = System.currentTimeMillis();
		Point targetPoint = new Point(-1, -1);
		while (running && System.currentTimeMillis() - start < timeout && locatable.validate()) {
			if (!locatable.contains(targetPoint)) {
				final Point viewPortPoint = locatable.getNextViewportPoint();
				if (!Mouse.isOnCanvas(viewPortPoint.x, viewPortPoint.y)) {
					Time.sleep(Random.nextInt(25, 51));
					continue;
				}
				targetPoint.setLocation(viewPortPoint);
			} else if (!Mouse.isOnCanvas(targetPoint.x, targetPoint.y)) {
				targetPoint.setLocation(-1, -1);
				Time.sleep(Random.nextInt(100, 200));
				continue;
			}
			final Point currentPoint = clientMouse.getLocation();
			if (targetPoint.distance(currentPoint) < 3 && locatable.contains(currentPoint) && filter.accept(currentPoint)) {
				accepted = true;
				break;
			}
			final double deltaTime = Random.nextDouble(8D, 10D) / 1000D;
			final Vector forceVector = new Vector();
			for (ForceModifier modifier : forceModifiers) {
				final Vector f = modifier.apply(deltaTime, targetPoint);
				if (f == null) {
					continue;
				}
				forceVector.add(f);
			}

			if (Double.isNaN(forceVector.xUnits) || Double.isNaN(forceVector.yUnits)) {
				return;
			}
			velocity.add(forceVector.multiply(deltaTime));

			final Vector deltaPosition = velocity.multiply(deltaTime);
			if (deltaPosition.xUnits != 0 && deltaPosition.yUnits != 0) {
				int x = (int) currentPoint.getX() + (int) deltaPosition.xUnits;
				int y = (int) currentPoint.getY() + (int) deltaPosition.yUnits;
				if (!Mouse.isOnCanvas(x, y)) {
					final Canvas canvas = Bot.resolve().getCanvas();
					switch (Mouse.getSide()) {
					case 1:
						x = 1;
						y = Random.nextInt(0, canvas.getHeight());
						break;
					case 2:
						x = Random.nextInt(0, canvas.getWidth());
						y = canvas.getHeight() + 1;
						break;
					case 3:
						x = canvas.getWidth() + 1;
						y = Random.nextInt(0, canvas.getHeight());
						break;
					case 4:
					default:
						x = Random.nextInt(0, canvas.getWidth());
						y = 1;
						break;
					}
				}
				Mouse.hop(x, y);
			}
			try {
				Thread.sleep((long) (deltaTime * 1000));
			} catch (InterruptedException e) {
				return;
			}
		}
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void stop() {
		running = false;
	}

	private void configureModifiers() {
		forceModifiers.add(new ForceModifier() {
			//Target tracking
			public Vector apply(final double deltaTime, final Point pTarget) {
				final Point currentLocation = clientMouse.getLocation();
				final Vector targetVector = new Vector();
				targetVector.xUnits = pTarget.x - currentLocation.getX();
				targetVector.yUnits = pTarget.y - currentLocation.getY();
				if (targetVector.xUnits == 0 && targetVector.yUnits == 0) {
					return null;
				}
				final double angle = targetVector.getAngle();
				final double acceleration = Random.nextInt(2500, 3000);
				final Vector force = new Vector();
				force.xUnits = Math.cos(angle) * acceleration;
				force.yUnits = Math.sin(angle) * acceleration;
				return force;
			}
		});

		forceModifiers.add(new ForceModifier() {
			//Friction
			public Vector apply(final double deltaTime, final Point pTarget) {
				return velocity.multiply(-1);
			}
		});

		forceModifiers.add(new ForceModifier() {
			//Velocity killer on destination (prevent loop-back)
			public Vector apply(final double deltaTime, final Point pTarget) {
				final Point currentLocation = clientMouse.getLocation();
				final Vector targetVector = new Vector();
				targetVector.xUnits = pTarget.x - currentLocation.getX();
				targetVector.yUnits = pTarget.y - currentLocation.getY();
				if (targetVector.xUnits > -3 && targetVector.xUnits < 3 &&
						targetVector.yUnits > -3 && targetVector.yUnits < -3) {
					velocity.xUnits = 0;
					velocity.yUnits = 0;
				}
				return null;
			}
		});

		forceModifiers.add(new ForceModifier() {
			//Target noise
			public Vector apply(final double deltaTime, final Point pTarget) {
				final Point currentLocation = clientMouse.getLocation();
				final Vector targetVector = new Vector();
				targetVector.xUnits = pTarget.x - currentLocation.getX();
				targetVector.yUnits = pTarget.y - currentLocation.getY();
				final double targetMagnitude = targetVector.getMagnitude();
				if (targetMagnitude > Random.nextInt(10, 20)) {
					final double angle = Random.nextDouble(-Math.PI, Math.PI);
					final Vector force = new Vector();
					final int acceleration = targetMagnitude > Random.nextInt(120, 200) ? Random.nextInt(3000, 4000) : Random.nextInt(100, 300);
					force.xUnits = Math.cos(angle) * acceleration;
					force.yUnits = Math.sin(angle) * acceleration;
					return force;
				}
				return null;
			}
		});

		forceModifiers.add(new ForceModifier() {
			//Pass near-target fix (high-velocity curve)
			public Vector apply(final double deltaTime, final Point pTarget) {
				final Point currentLocation = clientMouse.getLocation();
				final Vector targetVector = new Vector();
				targetVector.xUnits = pTarget.x - currentLocation.getX();
				targetVector.yUnits = pTarget.y - currentLocation.getY();
				final double targetMagnitude = targetVector.getMagnitude();
				if (targetMagnitude < Random.nextInt(120, 200)) {
					final double velocityMagnitude = velocity.getMagnitude();
					final double velocityLength = Math.pow(velocityMagnitude, 2);
					final double targetLength = Math.pow(targetMagnitude, 2);
					if (targetLength == 0) {
						return null;
					}
					final double computedLength = Math.sqrt(velocityLength / targetLength);
					Vector adjustedToTarget = targetVector.multiply(computedLength);

					Vector force = new Vector();
					force.xUnits = (adjustedToTarget.xUnits - velocity.xUnits) / (deltaTime);
					force.yUnits = (adjustedToTarget.yUnits - velocity.yUnits) / (deltaTime);

					final double adjustmentFactor = 8D / targetMagnitude;
					if (adjustmentFactor < 1D) {
						force = force.multiply(adjustmentFactor);
					}
					if (targetMagnitude < 10D) {
						force = force.multiply(0.5D);
					}
					return force;
				}
				return null;
			}
		});
	}

	private interface ForceModifier {
		public Vector apply(double deltaTime, Point pTarget);
	}

	private class Vector {
		public double xUnits;
		public double yUnits;

		public void add(final Vector vector) {
			xUnits += vector.xUnits;
			yUnits += vector.yUnits;
		}

		public Vector multiply(final double factor) {
			final Vector out = new Vector();
			out.xUnits = xUnits * factor;
			out.yUnits = yUnits * factor;
			return out;
		}

		public double getMagnitude() {
			return Math.sqrt(xUnits * xUnits + yUnits * yUnits);
		}

		public double getAngle() {
			return Math.atan2(yUnits, xUnits);
		}
	}
}
