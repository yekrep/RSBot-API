package org.powerbot.bot.rt6;

import java.awt.Component;
import java.awt.EventQueue;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;

import org.powerbot.Configuration;
import org.powerbot.bot.SelectiveEventQueue;
import org.powerbot.bot.loader.LoaderUtils;
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

	public List<Field> getFields(final Class<?> cls) {
		final List<Field> f = new ArrayList<Field>();
		Collections.addAll(f, cls.getDeclaredFields());

		final Class<?> p = cls.getSuperclass();
		if (p != null && !p.equals(Object.class)) {
			f.addAll(getFields(p));
		}

		return f;
	}

	@Override
	public void run() {
		String hash = null;
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
					Thread.yield();
				}
			}
			f1.setAccessible(a1);
		}

		for (final Field f1 : o0.getClass().getDeclaredFields()) {
			final boolean a1 = f1.isAccessible();
			f1.setAccessible(true);
			Object o1 = null;
			while (o1 == null) {
				try {
					o1 = f1.get(o0);
				} catch (final IllegalAccessException e) {
					throw new RuntimeException(e);
				}
				Thread.yield();
			}
			f1.setAccessible(a1);
			final List<Field> f1x = getFields(o1.getClass());
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
					Thread.yield();
				}
				f2.setAccessible(a2);
				final Class<?> c2 = o2.getClass();

				if (c2.equals(Hashtable.class)) {
					final Hashtable x = (Hashtable) o2;
					if (!x.isEmpty()) {
						final Object xk = x.keys().nextElement(), xv = x.get(xk);
						if (xk.getClass().equals(String.class) && xv.getClass().equals(byte[].class)) {
							@SuppressWarnings("unchecked")
							final Hashtable<String, byte[]> xs = (Hashtable<String, byte[]>) x;
							v0 = xs;
						} else if (xk.getClass().equals(String.class) && xv.getClass().equals(Class.class)) {
							@SuppressWarnings("unchecked")
							final Hashtable<String, Class<?>> xs = (Hashtable<String, Class<?>>) x;
							v1 = xs;
						}
					}
				} else if (c2.equals(ProtectionDomain.class)) {
					v2 = (ProtectionDomain) o2;
				}
			}

			if (v0 != null && v1 != null && v2 != null) {
				synchronized (v0) {
					hash = LoaderUtils.hash(v0);
					log.info("Hash: " + hash + " size: " + v0.size());
				}
				break;
			}
		}

		if (hash == null || hash.isEmpty()) {
			log.severe("Could not load client");
		}

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
