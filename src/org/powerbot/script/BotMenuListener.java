package org.powerbot.script;

import javax.swing.event.MenuListener;

/**
 * A listener that represents a {@link Script} which has {@link javax.swing.JMenuItem}s available.
 *
 * @deprecated the UI now uses {@link org.powerbot.script.BotMenuActionListener} instead
 */
@Deprecated
public interface BotMenuListener extends MenuListener {
}
