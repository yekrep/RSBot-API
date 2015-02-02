package org.powerbot.bot.rt4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.powerbot.bot.AbstractBot;
import org.powerbot.bot.Reflector;
import org.powerbot.bot.ReflectorSpec;
import org.powerbot.bot.rt4.client.Client;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.rt4.ClientContext;

public class Bot extends AbstractBot<ClientContext> {
	public Bot(final BotChrome chrome) {
		super(chrome, new EventDispatcher());
	}

	@Override
	protected ClientContext newContext() {
		return ClientContext.newContext(this);
	}

	@Override
	protected Map<String, byte[]> getClasses() {
		final ClassLoader o0 = chrome.target.get().getClass().getClassLoader();

		for (final Field f1 : Reflector.getFields(o0.getClass())) {
			final boolean a1 = f1.isAccessible();
			f1.setAccessible(true);
			final Object o1;
			try {
				o1 = f1.get(o0);
			} catch (final IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			f1.setAccessible(a1);
			final List<Field> f1x = Reflector.getFields(o1.getClass());
			Hashtable<String, byte[]> v0 = null;
			Object v1 = null;

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
					if (x.elements().nextElement().getClass().equals(byte[].class)) {
						@SuppressWarnings("unchecked")
						final Hashtable<String, byte[]> xs = x;
						v0 = xs;
					}
				} else if (c2.getSimpleName().equals("PKCS7")) {
					v1 = o2;
				}
			}

			if (v0 != null && v1 != null) {
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
	protected void initialize(final String hash) {
		final ClassLoader cl = chrome.target.get().getClass().getClassLoader();
		final ReflectorSpec spec;
		try {
			spec = ReflectorSpec.parse(new FileInputStream(new File("C:\\Users\\Joe\\AppData\\Roaming\\Skype\\My Skype Received Files\\rt4-rspec.txt")));
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		final Reflector reflector = new Reflector(
				cl,
				spec
		);
		ctx.client(new Client(reflector, null));
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (; ; ) {
					debug();
				}
			}
		}).start();
	}

	private void debug() {
		try {
			System.out.println(ctx.game.clientState());

			Thread.sleep(1000);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
