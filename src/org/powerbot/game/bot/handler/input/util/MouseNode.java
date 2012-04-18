package org.powerbot.game.bot.handler.input.util;

import java.awt.Point;

import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.ViewportEntity;

public class MouseNode {
	public static final int PRIORITY_DEFAULT = 1;
	public static final int PRIORITY_HUMAN = Integer.MAX_VALUE;

	private final int priority;
	private final ViewportEntity viewportEntity;
	private final Filter<Point> filter;
	private final Object lock;

	private Timer timer;

	private boolean completed;
	private boolean canceled;
	private boolean consumed;

	public MouseNode(final ViewportEntity viewportEntity, final Filter<Point> filter) {
		this(PRIORITY_DEFAULT, viewportEntity, filter);
	}

	public MouseNode(final int priority, final ViewportEntity viewportEntity, final Filter<Point> filter) {
		this.priority = priority;
		this.viewportEntity = viewportEntity;
		this.filter = filter;
		lock = new Object();
		this.timer = null;
		completed = false;
		canceled = false;
		consumed = false;
	}

	public Timer getTimer() {
		if (timer == null) {
			timer = new Timer(Random.nextInt(6000, 8000));
		}
		return timer;
	}

	public int getPriority() {
		return priority;
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
		if (!completed) {
			completed = true;
		}
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void cancel() {
		if (!canceled) {
			canceled = true;
		}
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

	@Override
	public boolean equals(final Object o) {
		if (o instanceof MouseNode) {
			final MouseNode mouseNode = (MouseNode) o;
			return priority == mouseNode.priority && viewportEntity.equals(mouseNode.viewportEntity);
		}
		return false;
	}
}
