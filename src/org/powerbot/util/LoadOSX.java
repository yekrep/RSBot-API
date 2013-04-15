package org.powerbot.util;

import org.powerbot.OSXAdapter;
import org.powerbot.gui.controller.BotInteract;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author Paris
 */
public class LoadOSX implements Callable<Boolean> {

	@Override
	public Boolean call() throws Exception {
		if (Configuration.OS == Configuration.OperatingSystem.MAC) {
			for (final Method m : getClass().getDeclaredMethods()) {
				if (m.isAnnotationPresent(OSXAdapterInfo.class)) {
					switch (m.getAnnotation(OSXAdapterInfo.class).mode())
					{
						case 1: OSXAdapter.setAboutHandler(this, m); break;
						case 2: OSXAdapter.setQuitHandler(this, m); break;
					}
				}
			}
		}
		return true;
	}

	@LoadOSX.OSXAdapterInfo(mode = 1)
	public static void about() {
		BotInteract.showDialog(BotInteract.Action.ABOUT);
	}

	@LoadOSX.OSXAdapterInfo(mode = 2)
	public static void quit() {
		BotInteract.tabClose(false);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface OSXAdapterInfo {
		public int mode() default 0;
	}
}
