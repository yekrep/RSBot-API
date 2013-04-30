package org.powerbot.script.util;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.powerbot.event.EventMulticaster;

public class EventManager implements Suspendable, Subscribable<EventListener> {
	private final EventMulticaster multicaster;
	private final List<EventListener> queue, added;
	private final AtomicBoolean subscribed;

	public EventManager(final EventMulticaster multicaster) {
		this.multicaster = multicaster;
		queue = new ArrayList<>();
		added = new ArrayList<>();
		subscribed = new AtomicBoolean(false);
	}

	@Override
	public boolean isSuspended() {
		return subscribed.get();
	}

	@Override
	public void suspend() {
		if (!subscribed.compareAndSet(true, false)) {
			return;
		}
		synchronized (multicaster) {
			for (final EventListener e : added) {
				multicaster.removeListener(e);
			}
		}
	}

	@Override
	public void resume() {
		if (!subscribed.compareAndSet(true, false)) {
			return;
		}
		synchronized (multicaster) {
			for (final EventListener e : queue) {
				if (!added.contains(e)) {
					added.add(e);
					multicaster.addListener(e);
				}
			}
		}
	}

	@Override
	public void subscribe(final EventListener e) {
		synchronized (multicaster) {
			if (!queue.contains(e)) {
				queue.add(e);
			}
			if (subscribed.get()) {
				if (!added.contains(e)) {
					added.add(e);
					multicaster.addListener(e);
				}
			}
		}
	}

	public void subscribeAll() {
		synchronized (multicaster) {
			subscribed.set(true);
			for (final EventListener e : queue) {
				if (!added.contains(e)) {
					added.add(e);
					multicaster.addListener(e);
				}
			}
		}
	}

	@Override
	public void unsubscribe(final EventListener e) {
		synchronized (multicaster) {
			if (queue.contains(e)) {
				queue.remove(e);
			}
			if (added.contains(e)) {
				added.remove(e);
				multicaster.removeListener(e);
			}
		}
	}

	public void unsubscribeAll() {
		synchronized (multicaster) {
			subscribed.set(false);
			queue.clear();
			for (final EventListener e : added) {
				multicaster.removeListener(e);
			}
			added.clear();
		}
	}
}
