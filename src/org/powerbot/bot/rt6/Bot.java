package org.powerbot.bot.rt6;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import org.powerbot.Configuration;
import org.powerbot.bot.SelectiveEventQueue;
import org.powerbot.bot.rt6.activation.EventDispatcher;
import org.powerbot.gui.BotLauncher;
import org.powerbot.script.Condition;
import org.powerbot.script.rt6.ClientContext;

public final class Bot extends org.powerbot.script.Bot<ClientContext> {

	public Bot(final BotLauncher launcher) {
		super(launcher, new EventDispatcher());
	}

	@Override
	protected ClientContext newContext() {
		return ClientContext.newContext(this);
	}

	@Override
	public void run() {

		final boolean jre6 = System.getProperty("java.version").startsWith("1.6");
		if ((Configuration.OS == Configuration.OperatingSystem.MAC && !jre6) || (Configuration.OS != Configuration.OperatingSystem.MAC && jre6)) {
			new Thread(threadGroup, new SafeMode()).start();
		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				display();
			}
		});
	}

	private final class SafeMode implements Runnable {
		@Override
		public void run() {
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					final java.awt.Component c = ctx.client().getCanvas();
					return c != null && c.getKeyListeners().length > 0;//TODO: ??
				}
			})) {
				final SelectiveEventQueue queue = SelectiveEventQueue.getInstance();
				final boolean b = queue.isBlocking();
				queue.setBlocking(true);
				ctx.keyboard.send("s");
				queue.setBlocking(b);
			}
		}
	}
}
