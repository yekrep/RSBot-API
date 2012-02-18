package org.powerbot.event;

import org.powerbot.concurrent.ContainedTask;

public interface ActionComposite {
	public ContainedTask createTask();
}
