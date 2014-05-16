package org.powerbot;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicReference;

public class InterceptAgent implements ClassFileTransformer {
	private static final AtomicReference<ClassFileTransformer> obj = new AtomicReference<ClassFileTransformer>(null);
	private static final AtomicReference<Method> proxy = new AtomicReference<Method>(null);

	public static void premain(final String agentArgs, final Instrumentation instrumentation) {
		try {
			final Class<? extends ClassFileTransformer> c = Class.forName(InterceptAgent.class.getName() + "Proxy").asSubclass(ClassFileTransformer.class);
			obj.set(c.newInstance());

			final Field f = c.getDeclaredField("registered");
			final boolean a = f.isAccessible();
			f.setAccessible(true);
			f.setBoolean(obj.get(), true);
			f.setAccessible(a);

			proxy.set(c.getMethod("transform", new Class[]{ClassLoader.class, String.class, Class.class, ProtectionDomain.class, byte[].class}));
			instrumentation.addTransformer(new InterceptAgent(), true);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unused")
	public static void agentmain(final String agentArgs, final Instrumentation instrumentation) {
		premain(agentArgs, instrumentation);
	}

	@Override
	public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
	                        final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {
		if (proxy.get() == null) {
			return new byte[0];
		}

		try {
			return (byte[]) proxy.get().invoke(obj.get(), loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (final InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
