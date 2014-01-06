package org.powerbot.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.powerbot.Configuration;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.component.BotMenuBar;

/**
 * @author Paris
 */
public class OSXAdapt implements Runnable {
	private final BotChrome chrome;

	public OSXAdapt(final BotChrome chrome) {
		this.chrome = chrome;
	}

	@OSXAdapt.OSXAdapterInfo(mode = 1)
	public void about() {
		chrome.menuBar.showDialog(BotMenuBar.Action.ABOUT);
	}

	@OSXAdapt.OSXAdapterInfo(mode = 2)
	public void quit() {
		chrome.close();
	}

	@OSXAdapt.OSXAdapterInfo(mode = 3)
	public void signin() {
		chrome.menuBar.showDialog(BotMenuBar.Action.SIGNIN);
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

	@SuppressWarnings({"unchecked"})
	public static class OSXAdapter implements InvocationHandler {

		protected final Object targetObject;
		protected final Method targetMethod;
		protected final String proxySignature;

		static Object macOSXApplication;

		// Pass this method an Object and Method equipped to perform application shutdown logic
		// The method passed should return a boolean stating whether or not the quit should occur
		public static void setQuitHandler(final Object target, final Method quitHandler) {
			setHandler(new OSXAdapter("handleQuit", target, quitHandler));
		}

		// Pass this method an Object and Method equipped to display application info
		// They will be called when the About menu item is selected from the application menu
		public static void setAboutHandler(final Object target, final Method aboutHandler) {
			final boolean enableAboutMenu = (target != null && aboutHandler != null);
			if (enableAboutMenu) {
				setHandler(new OSXAdapter("handleAbout", target, aboutHandler));
			}
			// If we're setting a handler, enable the About menu item by calling
			// com.apple.eawt.Application reflectively
			try {
				final Method enableAboutMethod = macOSXApplication.getClass().getDeclaredMethod("setEnabledAboutMenu", new Class[]{boolean.class});
				enableAboutMethod.invoke(macOSXApplication, new Object[]{Boolean.valueOf(enableAboutMenu)});
			} catch (Exception ex) {
				System.err.println("OSXAdapter could not access the About Menu");
				ex.printStackTrace();
			}
		}

		// Pass this method an Object and a Method equipped to display application options
		// They will be called when the Preferences menu item is selected from the application menu
		public static void setPreferencesHandler(final Object target, final Method prefsHandler) {
			final boolean enablePrefsMenu = (target != null && prefsHandler != null);
			if (enablePrefsMenu) {
				setHandler(new OSXAdapter("handlePreferences", target, prefsHandler));
			}
			// If we're setting a handler, enable the Preferences menu item by calling
			// com.apple.eawt.Application reflectively
			try {
				final Method enablePrefsMethod = macOSXApplication.getClass().getDeclaredMethod("setEnabledPreferencesMenu", new Class[]{boolean.class});
				enablePrefsMethod.invoke(macOSXApplication, new Object[]{Boolean.valueOf(enablePrefsMenu)});
			} catch (Exception ex) {
				System.err.println("OSXAdapter could not access the About Menu");
				ex.printStackTrace();
			}
		}

		// setHandler creates a Proxy object from the passed OSXAdapter and adds it as an ApplicationListener
		public static void setHandler(final OSXAdapter adapter) {
			try {
				final Class applicationClass = Class.forName("com.apple.eawt.Application");
				if (macOSXApplication == null) {
					macOSXApplication = applicationClass.getConstructor((Class[]) null).newInstance((Object[]) null);
				}
				final Class applicationListenerClass = Class.forName("com.apple.eawt.ApplicationListener");
				final Method addListenerMethod = applicationClass.getDeclaredMethod("addApplicationListener", new Class[]{applicationListenerClass});
				// Create a proxy object around this handler that can be reflectively added as an Apple ApplicationListener
				final Object osxAdapterProxy = Proxy.newProxyInstance(OSXAdapter.class.getClassLoader(), new Class[]{applicationListenerClass}, adapter);
				addListenerMethod.invoke(macOSXApplication, new Object[]{osxAdapterProxy});
			} catch (ClassNotFoundException cnfe) {
				System.err.println("This version of Mac OS X does not support the Apple EAWT.  ApplicationEvent handling has been disabled (" + cnfe + ")");
			} catch (Exception ex) {  // Likely a NoSuchMethodException or an IllegalAccessException loading/invoking eawt.Application methods
				System.err.println("Mac OS X Adapter could not talk to EAWT:");
				ex.printStackTrace();
			}
		}

		// Each OSXAdapter has the name of the EAWT method it intends to listen for (handleAbout, for example),
		// the Object that will ultimately perform the task, and the Method to be called on that Object
		protected OSXAdapter(final String proxySignature, final Object target, final Method handler) {
			this.proxySignature = proxySignature;
			this.targetObject = target;
			this.targetMethod = handler;
		}

		// Override this method to perform any operations on the event
		// that comes with the various callbacks
		// See setFileHandler above for an example
		public boolean callTarget(final Object appleEvent) throws InvocationTargetException, IllegalAccessException {
			final Object result = targetMethod.invoke(targetObject, (Object[]) null);
			if (result == null) {
				return true;
			}
			return Boolean.valueOf(result.toString()).booleanValue();
		}

		// InvocationHandler implementation
		// This is the entry point for our proxy object; it is called every time an ApplicationListener method is invoked
		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
			if (isCorrectMethod(method, args)) {
				final boolean handled = callTarget(args[0]);
				setApplicationEventHandled(args[0], handled);
			}
			// All of the ApplicationListener methods are void; return null regardless of what happens
			return null;
		}

		// Compare the method that was called to the intended method when the OSXAdapter instance was created
		// (e.g. handleAbout, handleQuit, handleOpenFile, etc.)
		protected boolean isCorrectMethod(final Method method, final Object[] args) {
			return (targetMethod != null && proxySignature.equals(method.getName()) && args.length == 1);
		}

		// It is important to mark the ApplicationEvent as handled and cancel the default behavior
		// This method checks for a boolean result from the proxy method and sets the event accordingly
		protected void setApplicationEventHandled(final Object event, final boolean handled) {
			if (event != null) {
				try {
					final Method setHandledMethod = event.getClass().getDeclaredMethod("setHandled", new Class[]{boolean.class});
					// If the target method returns a boolean, use that as a hint
					setHandledMethod.invoke(event, new Object[]{Boolean.valueOf(handled)});
				} catch (Exception ex) {
					System.err.println("OSXAdapter was unable to handle an ApplicationEvent: " + event);
					ex.printStackTrace();
				}
			}
		}
	}
}
