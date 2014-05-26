package org.powerbot.bot.rt6;

import java.applet.Applet;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.powerbot.Configuration;
import org.powerbot.bot.InputSimulator;
import org.powerbot.bot.Reflector;
import org.powerbot.bot.rt6.activation.EventDispatcher;
import org.powerbot.gui.BotLauncher;
import org.powerbot.script.Random;
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
	protected Map<String, byte[]> getClasses() {
		final Component o0 = launcher.target.get();

		for (final Field f1 : o0.getClass().getDeclaredFields()) {
			final boolean a1 = f1.isAccessible();
			f1.setAccessible(true);
			if (Class.class.equals(f1.getType())) {
				Object o1 = null;
				while (o1 == null) {
					try {
						o1 = f1.get(o0);
					} catch (final IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
			f1.setAccessible(a1);
		}

		for (final Field f1 : o0.getClass().getDeclaredFields()) {
			final boolean a1 = f1.isAccessible();
			f1.setAccessible(true);
			final Object o1;
			try {
				o1 = f1.get(o0);
			} catch (final IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			f1.setAccessible(a1);
			if (o1 == null) {
				continue;
			}
			final List<Field> f1x = Reflector.getFields(o1.getClass());
			Hashtable<String, byte[]> v0 = null;
			Hashtable<String, Class<?>> v1 = null;
			ProtectionDomain v2 = null;

			for (final Field f2 : f1x) {
				final Class<?> c2x = f2.getType();
				if (!c2x.equals(Object.class)) {
					continue;
				}

				final boolean a2 = f2.isAccessible();
				f2.setAccessible(true);
				Object o2 = null;
				while (o2 == null) {
					try {
						o2 = f2.get(o1);
					} catch (final IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
				f2.setAccessible(a2);
				final Class<?> c2 = o2.getClass();

				if (c2.equals(Hashtable.class)) {
					final Hashtable x = (Hashtable) o2;
					if (x.keys().nextElement().getClass().equals(String.class)) {
						final Class<?> c3 = x.elements().nextElement().getClass();
						if (c3.equals(byte[].class)) {
							@SuppressWarnings("unchecked")
							final Hashtable<String, byte[]> xs = x;
							v0 = xs;
						} else if (c3.getClass().equals(Class.class)) {
							@SuppressWarnings("unchecked")
							final Hashtable<String, Class<?>> xs = x;
							v1 = xs;
						}
					}
				} else if (c2.equals(ProtectionDomain.class)) {
					v2 = (ProtectionDomain) o2;
				}
			}

			if (v0 != null && v1 != null && v2 != null) {
				final Map<String, byte[]> z;
				synchronized (v0) {
					z = new HashMap<String, byte[]>(v0);
				}
				return z;
			}
		}

		return null;
	}

	@Override
	public boolean overlay() {
		final boolean jre6 = System.getProperty("java.version").startsWith("1.6");
		final boolean safe = (Configuration.OS == Configuration.OperatingSystem.MAC && !jre6) || (Configuration.OS != Configuration.OperatingSystem.MAC && jre6);
		if (safe) {
			new Thread(new SafeMode()).start();
		}
		return !safe;
	}

	private final class SafeMode implements Runnable {
		@Override
		public void run() {
			Component[] c;
			do {
				try {
					Thread.sleep(180);
				} catch (final InterruptedException ignored) {
					return;
				}
				c = ((Applet) launcher.target.get()).getComponents();
			} while (c == null || c.length == 0);

			log.info("Requesting safe mode");
			final Queue<KeyEvent> q = new LinkedList<KeyEvent>();
			InputSimulator.pushAlpha(q, c[0], KeyEvent.VK_S, 's');
			for (final KeyEvent e : q) {
				c[0].dispatchEvent(InputSimulator.retimeKeyEvent(e));
				try {
					Thread.sleep(Random.getDelay());
				} catch (final InterruptedException ignored) {
				}
			}
		}
	}
}
