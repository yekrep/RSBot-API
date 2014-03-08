package org.powerbot.script;

import java.applet.Applet;
import java.io.Closeable;

import org.powerbot.bot.EventDispatcher;
import org.powerbot.gui.BotChrome;

public abstract class Bot implements Runnable, Closeable {
	protected final BotChrome chrome;
	public final EventDispatcher dispatcher;
	public final ThreadGroup threadGroup;
	public Applet applet;

	public Bot(final BotChrome chrome, final EventDispatcher dispatcher) {
		this.chrome = chrome;
		this.dispatcher = dispatcher;
		threadGroup = new ThreadGroup("game"); // TODO: mask in live mode
	}
}
