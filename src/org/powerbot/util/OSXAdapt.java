package org.powerbot.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import apple.dts.samplecode.OSXAdapter;
import org.powerbot.Configuration;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.component.BotMenuBar;

/**
 * @author Paris
 */
public class OSXAdapt implements Runnable {

	@OSXAdapt.OSXAdapterInfo(mode = 1)
	public static void about() {
		BotMenuBar.showDialog(BotMenuBar.Action.ABOUT);
	}

	@OSXAdapt.OSXAdapterInfo(mode = 2)
	public static void quit() {
		BotChrome.getInstance().close();
	}

	@OSXAdapt.OSXAdapterInfo(mode = 3)
	public static void signin() {
		BotMenuBar.showDialog(BotMenuBar.Action.SIGNIN);
	}

	@Override
	public void run() {
		if (Configuration.OS == Configuration.OperatingSystem.MAC) {
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
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD})
	public @interface OSXAdapterInfo {
		public int mode() default 0;
	}
}
