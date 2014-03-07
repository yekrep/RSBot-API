package org.powerbot.script;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A descriptor of a {@link Script}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Manifest {
	/**
	 * The name of this {@link Script}.
	 *
	 * @return the name of this {@link Script}
	 */
	String name();

	/**
	 * The description, which should be 140 characters or less.
	 *
	 * @return the description
	 */
	String description();

	/**
	 * A series of key=value pairs separated by semicolons (;) or newlines.
	 *
	 * @return the properties
	 */
	String properties() default "";
}
