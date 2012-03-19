package org.powerbot.game.bot.input;

import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.powerbot.concurrent.Task;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.Locatable;

/**
 * @author Timer
 */
public class MouseManipulator extends Task {
	private final List<ForceModifier> forceModifiers = new ArrayList<ForceModifier>(5);
	private final Vector velocity = new Vector();
	private final long timeout;
	private boolean running;
	private final Locatable locatable;
	private final Filter<Point> filter;
	private boolean accepted = false;

	public MouseManipulator(final Locatable locatable, final Filter<Point> filter) {
		this.timeout = Random.nextInt(4000, 7000);
		this.running = false;
		this.locatable = locatable;
		this.filter = filter;
	}

	public void run() {
		this.running = true;
		configureModifiers();
		final long start = System.currentTimeMillis();
		Point lastCentral = null;
		Point lastTargetPoint = null;
		while (running && System.currentTimeMillis() - start < timeout) {
			final Point centralPoint = locatable.getCentralPoint();
			final Point targetPoint = new Point(-1, -1);
			if (lastTargetPoint != null) {
				targetPoint.x = lastTargetPoint.x;
				targetPoint.y = lastTargetPoint.y;
			}
			if (lastCentral == null || lastTargetPoint == null || !lastCentral.equals(centralPoint)) {
				final Point viewPortPoint = locatable.getNextViewportPoint();
				if (viewPortPoint.x == -1 || viewPortPoint.y == -1) {
					Time.sleep(Random.nextInt(25, 51));
					continue;
				}
				if (centralPoint.x == -1 || centralPoint.y == -1) {
					centralPoint.setLocation(viewPortPoint);
				}
				lastCentral = centralPoint;
			}
			if (!locatable.contains(targetPoint)) {
				lastTargetPoint = null;
				continue;
			}
			lastTargetPoint = targetPoint;
			final Point currentPoint = Mouse.getLocation();
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
				if (Mouse.isOnCanvas(x, y)) {
					Mouse.hop(x, y);
				}
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
				final Point currentLocation = MouseInfo.getPointerInfo().getLocation();
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
				final Point currentLocation = MouseInfo.getPointerInfo().getLocation();
				final Vector targetVector = new Vector();
				targetVector.xUnits = pTarget.x - currentLocation.getX();
				targetVector.yUnits = pTarget.y - currentLocation.getY();
				if (targetVector.xUnits > -2 && targetVector.xUnits < 2 &&
						targetVector.yUnits > -2 && targetVector.yUnits < -2) {
					velocity.xUnits = 0;
					velocity.yUnits = 0;
				}
				return null;
			}
		});

		forceModifiers.add(new ForceModifier() {
			//Target noise
			public Vector apply(final double deltaTime, final Point pTarget) {
				final Point currentLocation = MouseInfo.getPointerInfo().getLocation();
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
			public Vector apply(final double deltaTime, final Point pTarget) {
				return null;
			}
		});

		forceModifiers.add(new ForceModifier() {
			//Pass near-target fix (high-velocity curve)
			public Vector apply(final double deltaTime, final Point pTarget) {
				final Point currentLocation = MouseInfo.getPointerInfo().getLocation();
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

		public Vector sum(final Vector vector) {
			final Vector out = new Vector();
			out.xUnits = xUnits + vector.xUnits;
			out.yUnits = xUnits + vector.yUnits;
			return out;
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
