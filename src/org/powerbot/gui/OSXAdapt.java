package org.powerbot.gui;

import java.awt.Image;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.powerbot.Configuration;
import org.powerbot.bot.ScriptController;

class OSXAdapt implements Runnable, InvocationHandler {
	private final BotChrome chrome;

	public OSXAdapt(final BotChrome chrome) {
		this.chrome = chrome;
	}

	@Override
	public void run() {
		if (Configuration.OS != Configuration.OperatingSystem.MAC) {
			return;
		}

		try {
			final String pkg = "com.apple.eawt";
			final Class<?> cls = Class.forName(pkg + ".Application");
			final Object app = cls.getMethod("getApplication").invoke(null);

			for (String s : new String[]{"About", "Quit", "Preferences"}) {
				s += "Handler";
				final Class<?> t = Class.forName(pkg + "." + s);
				cls.getMethod("set" + s, t).invoke(app, Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{t}, this));
			}

			cls.getMethod("setDockIconImage", Image.class).invoke(app, chrome.window.get().getIconImage());
		} catch (final Exception ignored) {
		}
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) {
		final String name = method.getName();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (name.equals("handleAbout")) {
					if (chrome.menu.get() != null) {
						chrome.menu.get().showAbout();
					}
				} else if (name.startsWith("handleQuit")) {
					chrome.window.get().dispatchEvent(new WindowEvent(chrome.window.get(), WindowEvent.WINDOW_CLOSING));
				} else if (name.equals("handlePreferences")) {
					if (chrome.menu.get() != null && !BotPreferences.loading.get() && !BotPreferences.visible.get() && chrome.bot.get() != null && chrome.bot.get().ctx.client() != null) {
						final ScriptController c = (ScriptController) chrome.bot.get().ctx.controller;
						final boolean active = c.valid() && !c.isStopping();
						boolean show = true;

						if (active) {
							if (JOptionPane.showConfirmDialog(chrome.window.get(), "Would you like to stop the current script?", BotLocale.SCRIPTS, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
								chrome.menu.get().scriptStop();
							} else {
								show = false;
							}
						}

						if (show) {
							new BotPreferences(chrome);
						}
					}
				}
			}
		});
		return null;
	}
}
