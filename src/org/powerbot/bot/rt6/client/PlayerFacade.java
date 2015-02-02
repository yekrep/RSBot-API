package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class PlayerFacade extends ReflectProxy {
	public PlayerFacade(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Skill[] getSkills() {
		final Object[] arr = reflector.access(this, Object[].class);
		final Skill[] arr2 = arr != null ? new Skill[arr.length] : new Skill[0];
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new Skill(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public Varpbits getVarpbits() {
		return new Varpbits(reflector, reflector.access(this));
	}
}
