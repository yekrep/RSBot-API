package org.powerbot.game.bot.handler.input.util;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.ViewportEntity;

public class MouseNode {
	public static final Map<ThreadGroup, Double> speeds = new HashMap<>();

	private final ViewportEntity viewportEntity;
	private final Filter<Point> filter;
	private final Object lock;
	private final double speed;

	private Timer timer;

	private boolean completed;
	private boolean canceled;
	private boolean consumed;

	public MouseNode(final ViewportEntity viewportEntity, final Filter<Point> filter) {
		this.viewportEntity = viewportEntity;
		this.filter = filter;
		lock = new Object();
		this.timer = null;
		completed = false;
		canceled = false;
		consumed = false;

		final Double speed = speeds.get(Thread.currentThread().getThreadGroup());
		this.speed = speed == null ? 1.0 : speed;
	}

	public Timer getTimer() {
		if (timer == null) {
			timer = new Timer(Random.nextInt(6000, 8000));
		}
		return timer;
	}

	public ViewportEntity getViewportEntity() {
		return viewportEntity;
	}

	public Filter<Point> getFilter() {
		return filter;
	}

	public Object getLock() {
		return lock;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void complete() {
		completed = true;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void cancel() {
		canceled = true;
	}

	public void consume() {
		if (consumed) {
			throw new RuntimeException("dual consume");
		}
		consumed = true;
	}

	public void reset() {
		consumed = false;
	}

	public boolean processable() {
		return !canceled && !completed && !consumed;
	}

	public double getSpeed() {
		return speed;
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof MouseNode) {
			final MouseNode mouseNode = (MouseNode) o;
			return viewportEntity.equals(mouseNode.viewportEntity);
		}
		return false;
	}
}
