package org.powerbot.script;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A descriptor of a {@link Script} for the RSBot SDN™.
 *
 * @author Paris
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
	 * The version.
	 *
	 * @return the version
	 */
	double version() default 1.0;

	/**
	 * The authors.
	 *
	 * @return the authors
	 * @deprecated the RSBot SDN™ author is now used instead
	 */
	@Deprecated
	String[] authors();

	/**
	 * The website URL.
	 *
	 * @return the website URL
	 * @deprecated see {@link #topic()}
	 */
	@Deprecated
	String website() default "";

	/**
	 * The powerbot.org forum topic ID.
	 *
	 * @return the powerbot.org forum topic ID
	 */
	int topic() default 0;

	/**
	 * The VIP status.
	 *
	 * @return any value
	 * @deprecated no longer used by the RSBot SDN™
	 */
	@Deprecated
	boolean vip() default false;

	/**
	 * The hidden status.
	 *
	 * @return the hidden status
	 */
	boolean hidden() default false;

	/**
	 * The maximum number of running instances a user may run this {@link Script}
	 *
	 * @return the maximum number of running instances
	 */
	int instances() default 4;
}
