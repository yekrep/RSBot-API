package org.powerbot.gui;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.powerbot.Configuration;
import org.powerbot.bot.ScriptController;

class OSXAdapt implements Runnable {
	private final BotLauncher launcher;

	public OSXAdapt(final BotLauncher launcher) {
		this.launcher = launcher;
	}

	@OSXAdapt.OSXAdapterInfo(mode = 1)
	public void about() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				launcher.menu.get().showAbout();
			}
		});
	}

	@OSXAdapt.OSXAdapterInfo(mode = 2)
	public void quit() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				launcher.close();
			}
		});
	}

	@OSXAdapt.OSXAdapterInfo(mode = 3)
	public void preferences() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (BotPreferences.loading.get() || BotPreferences.visible.get() || launcher.bot.get() == null || launcher.bot.get().ctx.client() == null) {
					return;
				}

				final ScriptController c = (ScriptController) launcher.bot.get().ctx.controller;
				final boolean active = c.valid() && !c.isStopping();

				if (active) {
					if (JOptionPane.showConfirmDialog(launcher.window.get(), "Would you like to stop the current script?", BotLocale.SCRIPTS, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
						launcher.menu.get().scriptStop();
					} else {
						return;
					}
				}

				new BotPreferences(launcher);
			}
		});
	}

	@Override
	public void run() {
		if (Configuration.OS != Configuration.OperatingSystem.MAC) {
			return;
		}

		for (final Method m : getClass().getDeclaredMethods()) {
			if (m.isAnnotationPresent(OSXAdapterInfo.class)) {
				switch (m.getAnnotation(OSXAdapterInfo.class).mode()) {
				case 1:
					OSXReflectionAdapter.setAboutHandler(this, m);
					break;
				case 2:
					OSXReflectionAdapter.setQuitHandler(this, m);
					break;
				case 3:
					OSXReflectionAdapter.setPreferencesHandler(this, m);
					break;
				}
			}
		}

		OSXReflectionAdapter.setDockIconImage(Toolkit.getDefaultToolkit().getImage(Configuration.getResource("icon.png")));
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface OSXAdapterInfo {
		public int mode() default 0;
	}

	private static final class OSXReflectionAdapter implements InvocationHandler {
		final Object targetObject;
		final Method targetMethod;
		final String proxySignature;
		static Object app;

		public static void setDockIconImage(final Image img) {
			try {
				final Method m = app.getClass().getDeclaredMethod("setDockIconImage", new Class[]{Image.class});
				m.invoke(app, img);
			} catch (final Exception ignored) {
				ignored.printStackTrace();
			}
		}

		public static void setQuitHandler(final Object target, final Method quitHandler) {
			setHandler(new OSXReflectionAdapter("handleQuit", target, quitHandler));
		}

		public static void setAboutHandler(final Object target, final Method aboutHandler) {
			final boolean e = (target != null && aboutHandler != null);
			if (e) {
				setHandler(new OSXReflectionAdapter("handleAbout", target, aboutHandler));
			}
			try {
				final Method m = app.getClass().getDeclaredMethod("setEnabledAboutMenu", new Class[]{boolean.class});
				m.invoke(app, e);
			} catch (final Exception ignored) {
			}
		}

		public static void setPreferencesHandler(final Object target, final Method prefsHandler) {
			final boolean e = (target != null && prefsHandler != null);
			if (e) {
				setHandler(new OSXReflectionAdapter("handlePreferences", target, prefsHandler));
			}
			try {
				final Method m = app.getClass().getDeclaredMethod("setEnabledPreferencesMenu", new Class[]{boolean.class});
				m.invoke(app, e);
			} catch (final Exception ignored) {
			}
		}

		public static void setHandler(final OSXReflectionAdapter adapter) {
			try {
				final Class<?> c = Class.forName("com.apple.eawt.Application");
				if (app == null) {
					app = c.getConstructor((Class[]) null).newInstance((Object[]) null);
				}
				final Class<?> l = Class.forName("com.apple.eawt.ApplicationListener");
				final Method m = c.getDeclaredMethod("addApplicationListener", new Class[]{l});
				final Object p = Proxy.newProxyInstance(OSXReflectionAdapter.class.getClassLoader(), new Class[]{l}, adapter);
				m.invoke(app, p);
			} catch (final Exception ignored) {
			}
		}

		OSXReflectionAdapter(final String proxySignature, final Object target, final Method handler) {
			this.proxySignature = proxySignature;
			this.targetObject = target;
			this.targetMethod = handler;
		}

		public boolean callTarget() throws InvocationTargetException, IllegalAccessException {
			final Object result = targetMethod.invoke(targetObject, (Object[]) null);
			if (result == null) {
				return true;
			}
			return Boolean.valueOf(result.toString());
		}

		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
			if (isCorrectMethod(method, args)) {
				setApplicationEventHandled(args[0], callTarget());
			}
			return null;
		}

		boolean isCorrectMethod(final Method method, final Object[] args) {
			return (targetMethod != null && proxySignature.equals(method.getName()) && args.length == 1);
		}

		void setApplicationEventHandled(final Object event, final boolean handled) {
			if (event != null) {
				try {
					final Method m = event.getClass().getDeclaredMethod("setHandled", new Class[]{boolean.class});
					m.invoke(event, handled);
				} catch (final Exception ignored) {
				}
			}
		}
	}
}
