package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Render  extends ReflectProxy {
	public Render(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public float getAbsoluteX(){
		return reflector.accessFloat(this);
	}

	public float getAbsoluteY(){
		return reflector.accessFloat(this);
	}

	public float getXMultiplier(){
		return reflector.accessFloat(this);
	}

	public float getYMultiplier(){
		return reflector.accessFloat(this);
	}

	public int getGraphicsIndex(){
		return reflector.accessInt(this);
	}

	public RenderData getRenderData(){
		return new RenderData(reflector, reflector.access(this));
	}
}
