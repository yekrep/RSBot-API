package org.powerbot.game.bot;

import org.powerbot.concurrent.TaskContainer;
import org.powerbot.concurrent.TaskProcessor;
import org.powerbot.game.GameDefinition;

public class Bot extends GameDefinition {
	private TaskProcessor taskProcessor;

	public Bot() {
		this.taskProcessor = new TaskContainer();
	}
}
