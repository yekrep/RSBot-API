package org.powerbot.bot;

import java.util.Map;

public abstract class AbstractReflectorSpec {

	public abstract Map<String, String> getInterfaces();

	public abstract Map<String, Reflector.FieldConfig> getConfigs();

	public abstract Map<String, Long> getConstants();
}
