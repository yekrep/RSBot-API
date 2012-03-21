package org.powerbot.game.api.methods;

import java.awt.event.KeyEvent;

import org.powerbot.game.api.methods.widget.WidgetComposite;
import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

/**
 * @author Timer
 */
public enum Tabs {
	NONE(-1, null, -1),
	ATTACK(0, "Combat Styles", KeyEvent.VK_F5),
	TASK_LIST(1, "Task List", -1),
	STATS(2, "Stats", -1),
	QUESTS(3, "Quest Journals", -1),
	INVENTORY(4, "Inventory", KeyEvent.VK_F1),
	EQUIPMENT(5, "Worn Equipment", KeyEvent.VK_F2),
	PRAYER(6, "Prayer List", KeyEvent.VK_F3),
	MAGIC(7, "Magic Spellbook", KeyEvent.VK_F4),
	FRIENDS(9, "Friends List", -1),
	FRIENDS_CHAT(10, "Friends Chat", -1),
	CLAN_CHAT(11, "Clan Chat", -1),
	OPTIONS(12, "Options", -1),
	EMOTES(13, "Emotes", -1),
	MUSIC(14, "Music Player", -1),
	NOTES(15, "Notes", -1),
	LOGOUT(16, "Exit", -1);

	private final String description;
	private final int functionKey;
	private final int index;

	Tabs(final int index, final String description, final int functionKey) {
		this.description = description;
		this.functionKey = functionKey;
		this.index = index;
	}

	public String getDescription() {
		return description;
	}

	public int getFunctionKey() {
		return functionKey;
	}

	public boolean hasFunctionKey() {
		return functionKey != -1;
	}

	public int getIndex() {
		return index;
	}

	public boolean open() {
		return false;//TODO
	}

	public static Tabs getCurrent() {
		for (final Tabs t : Tabs.values()) {
			final WidgetChild tab = WidgetComposite.getTab(t);
			if (tab != null && tab.getTextureId() != -1) {
				return t;
			}
		}
		final Widget logout = Widgets.get(182);//TODO static var | not number
		return logout != null && logout.isValid() ? Tabs.LOGOUT : Tabs.NONE;
	}
}
