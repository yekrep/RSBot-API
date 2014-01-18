package org.powerbot.os.misc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.swing.SwingUtilities;

import org.powerbot.os.Configuration;
import org.powerbot.os.ui.BotChrome;

/**
 * @author Paris
 */
public class OSXAdapt implements Runnable {
	private final BotChrome chrome;

	public OSXAdapt(final BotChrome chrome) {
		this.chrome = chrome;
	}

	@OSXAdapt.OSXAdapterInfo(mode = 1)
	@SuppressWarnings("unused")
	public void about() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				chrome.menu.showAbout();
			}
		});
	}

	@OSXAdapt.OSXAdapterInfo(mode = 2)
	@SuppressWarnings("unused")
	public void quit() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				chrome.close();
			}
		});
	}

	@OSXAdapt.OSXAdapterInfo(mode = 3)
	@SuppressWarnings("unused")
	public void preferences() {
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
					OSXAdapter.setAboutHandler(this, m);
					break;
				case 2:
					OSXAdapter.setQuitHandler(this, m);
					break;
				case 3:
					OSXAdapter.setPreferencesHandler(this, m);
					break;
				}
			}
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface OSXAdapterInfo {
		public int mode() default 0;
	}

	public static class OSXAdapter implements InvocationHandler {
		protected final Object targetObject;
		protected final Method targetMethod;
		protected final String proxySignature;
		protected static Object app;

		public static void setQuitHandler(final Object target, final Method quitHandler) {
			setHandler(new OSXAdapter("handleQuit", target, quitHandler));
		}

		public static void setAboutHandler(final Object target, final Method aboutHandler) {
			final boolean e = (target != null && aboutHandler != null);
			if (e) {
				setHandler(new OSXAdapter("handleAbout", target, aboutHandler));
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
				setHandler(new OSXAdapter("handlePreferences", target, prefsHandler));
			}
			try {
				final Method m = app.getClass().getDeclaredMethod("setEnabledPreferencesMenu", new Class[]{boolean.class});
				m.invoke(app, e);
			} catch (final Exception ignored) {
			}
		}

		public static void setHandler(final OSXAdapter adapter) {
			try {
				final Class<?> c = Class.forName("com.apple.eawt.Application");
				if (app == null) {
					app = c.getConstructor((Class[]) null).newInstance((Object[]) null);
				}
				final Class<?> l = Class.forName("com.apple.eawt.ApplicationListener");
				final Method m = c.getDeclaredMethod("addApplicationListener", new Class[]{l});
				final Object p = Proxy.newProxyInstance(OSXAdapter.class.getClassLoader(), new Class[]{l}, adapter);
				m.invoke(app, p);
			} catch (final Exception ignored) {
			}
		}

		protected OSXAdapter(final String proxySignature, final Object target, final Method handler) {
			this.proxySignature = proxySignature;
			this.targetObject = target;
			this.targetMethod = handler;
		}

		public boolean callTarget(final Object appleEvent) throws InvocationTargetException, IllegalAccessException {
			final Object result = targetMethod.invoke(targetObject, (Object[]) null);
			if (result == null) {
				return true;
			}
			return Boolean.valueOf(result.toString());
		}

		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
			if (isCorrectMethod(method, args)) {
				setApplicationEventHandled(args[0], callTarget(args[0]));
			}
			return null;
		}

		protected boolean isCorrectMethod(final Method method, final Object[] args) {
			return (targetMethod != null && proxySignature.equals(method.getName()) && args.length == 1);
		}

		protected void setApplicationEventHandled(final Object event, final boolean handled) {
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
