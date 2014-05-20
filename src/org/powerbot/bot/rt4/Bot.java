package org.powerbot.bot.rt4;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.List;

import org.powerbot.Instrument;
import org.powerbot.bot.loader.LoaderUtils;
import org.powerbot.bot.rt4.activation.EventDispatcher;
import org.powerbot.gui.BotLauncher;
import org.powerbot.script.rt4.ClientContext;
import sun.security.pkcs.PKCS7;

public class Bot extends org.powerbot.script.Bot<ClientContext> {
	public Bot(final BotLauncher launcher) {
		super(launcher, new EventDispatcher());
	}

	@Override
	protected ClientContext newContext() {
		return ClientContext.newContext(this);
	}

	@Override
	public void run() {
		String hash = null;
		final Object o0 = launcher.target.get().getClass().getClassLoader();

		for (final Field f1 : Instrument.getFields(o0.getClass())) {
			final boolean a1 = f1.isAccessible();
			f1.setAccessible(true);
			final Object o1;
			try {
				o1 = f1.get(o0);
			} catch (final IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			f1.setAccessible(a1);
			final List<Field> f1x = Instrument.getFields(o1.getClass());
			Hashtable<String, byte[]> v0 = null;
			PKCS7 v1 = null;

			for (final Field f2 : f1x) {
				final boolean a2 = f2.isAccessible();
				f2.setAccessible(true);
				final Object o2;
				try {
					o2 = f2.get(o1);
				} catch (final IllegalAccessException e) {
					throw new RuntimeException(e);
				}
				f2.setAccessible(a2);
				if (o2 == null) {
					continue;
				}
				final Class<?> c2 = o2.getClass();

				if (c2.equals(Hashtable.class)) {
					final Hashtable x = (Hashtable) o2;
					if (!x.isEmpty() && x.elements().nextElement().getClass().equals(byte[].class)) {
						@SuppressWarnings("unchecked")
						final Hashtable<String, byte[]> xs = (Hashtable<String, byte[]>) x;
						v0 = xs;
					}
				} else if (c2.equals(PKCS7.class)) {
					v1 = (PKCS7) o2;
				}
			}

			if (v0 != null && v1 != null) {
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
	}
}
