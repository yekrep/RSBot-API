package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class Render  extends ContextAccessor{
	public Render(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public float getAbsoluteX(){
		return engine.accessFloat(this);
	}

	public float getAbsoluteY(){
		return engine.accessFloat(this);
	}

	public float getXMultiplier(){
		return engine.accessFloat(this);
	}

	public float getYMultiplier(){
		return engine.accessFloat(this);
	}

	public int getGraphicsIndex(){
		return engine.accessInt(this);
	}

	public RenderData getRenderData(){
		return new RenderData(engine, engine.access(this));
	}
}
