package org.powerbot;

import org.powerbot.concurrent.RunnableTask;
import org.powerbot.concurrent.TaskContainer;
import org.powerbot.concurrent.TaskProcessor;
import org.powerbot.event.Action;
import org.powerbot.event.ActionComposite;
import org.powerbot.event.ActionDispatcher;
import org.powerbot.event.ActionManager;
import org.powerbot.lang.Activator;

public class ActionTest implements Runnable {
	public ActionTest() {

	}

	public static void main(String[] params) {
		new ActionTest().run();
	}

	public void run() {
		long start = System.currentTimeMillis();

		TaskProcessor processor = new TaskContainer();
		ActionManager actionDispatcher = new ActionDispatcher(processor);
		Action printHi = new Action(new Activator() {
			public boolean dispatch() {
				return true;
			}
		}, new ActionComposite(new RunnableTask() {
			public void run() {
				System.out.println("hi");
				try {
					Thread.sleep(100);
				} catch (InterruptedException ignored) {
				}
			}
		}));
		Action sleepAction = new Action(new Activator() {
			public boolean dispatch() {
				return true;
			}
		}, new ActionComposite(new RunnableTask() {
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ignored) {
				}
			}
		}));
		sleepAction.requireLock = false;
		actionDispatcher.handle(printHi);
		actionDispatcher.handle(sleepAction);
		actionDispatcher.listen();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		actionDispatcher.destroy();
		System.out.println("execution time = " + (System.currentTimeMillis() - start));
	}
}
