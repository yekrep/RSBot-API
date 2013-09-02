package org.powerbot.script.internal;

import org.powerbot.script.lang.Prioritizable;
import org.powerbot.script.lang.Yieldable;

public interface YieldableTask extends Prioritizable, Yieldable {
	public boolean isValid();
}
