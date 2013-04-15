package org.powerbot.script;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Manifest {
	String name();

	String description();

	double version() default 1.0;

	String[] authors();

	String website() default "";

	int topic() default 0;

	boolean vip() default false;

	boolean hidden() default false;

	int instances() default Integer.MAX_VALUE;
}
