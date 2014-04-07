package org.powerbot.script;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.EventListener;
import java.util.Queue;

/**
 * The base interface of a script.
 */
public interface Script extends EventListener {

	/**
	 * The possible states.
	 */
	public enum State {
		START, SUSPEND, RESUME, STOP
	}

	/**
	 * Returns the execution queue.
	 *
	 * @param state the state being invoked
	 * @return a sequence of {@link java.lang.Runnable} items to process
	 */
	public Queue<Runnable> getExecQueue(State state);

	/**
	 * A controller for a {@link Script} which invokes and determines state changes.
	 */
	public interface Controller extends Suspendable, Stoppable {

		/**
		 * Adds a {@link java.lang.Runnable} to the executor.
		 *
		 * @param e a runnable to be executed
		 * @param <E> a type that extends {@link java.lang.Runnable}
		 * @return {@code true} if the runnable was added, otherwise {@code false}
		 */
		public <E extends Runnable> boolean offer(E e);
	}

	/**
	 * A {@link Script} descriptor.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public @interface Manifest {
		/**
		 * The human-friendly name.
		 *
		 * @return the name
		 */
		String name();

		/**
		 * The description, which should be 140 characters or less.
		 *
		 * @return the description
		 */
		String description();

		/**
		 * A series of key=value pairs separated by semicolons (;) or newlines,
		 * e.g. {@code "hidden=true;topic=1234"}.
		 *
		 * @return the properties
		 */
		String properties() default "";
	}
}
