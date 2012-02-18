package org.powerbot.event;

import org.powerbot.lang.Activator;

public interface Action {
	public Activator getActivator();

	public ActionComposite[] appendComposites();
}
