package org.powerbot.concurrent;

public class GroupedThread extends Thread {
	private final String group;

	public GroupedThread(final ThreadGroup threadGroup, final Runnable r, final String name, final String group) {
		super(threadGroup, r, name);
		this.group = group;
	}

	public String getGroup() {
		return group;
	}
}
