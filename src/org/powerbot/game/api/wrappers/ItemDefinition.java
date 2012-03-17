package org.powerbot.game.api.wrappers;

import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.RSItemDefActions;
import org.powerbot.game.client.RSItemDefBooleans;
import org.powerbot.game.client.RSItemDefGroundActions;
import org.powerbot.game.client.RSItemDefID;
import org.powerbot.game.client.RSItemDefInts;
import org.powerbot.game.client.RSItemDefIsMembersObject;
import org.powerbot.game.client.RSItemDefName;

/**
 * @author Timer
 */
public class ItemDefinition {
	private final Object definition;

	public ItemDefinition(final Object definition) {
		this.definition = definition;
	}

	public String getName() {
		return (String) ((RSItemDefName) definition).getRSItemDefName();
	}

	public int getId() {
		return ((RSItemDefID) ((RSItemDefInts) definition).getRSItemDefInts()).getRSItemDefID() * Bot.resolve().multipliers.ITEMDEF_ID;
	}

	public boolean isMembers() {
		return ((RSItemDefIsMembersObject) ((RSItemDefBooleans) definition).getRSItemDefBooleans()).getRSItemDefIsMembersObject();
	}

	public String[] getActions() {
		return (String[]) ((RSItemDefActions) definition).getRSItemDefActions();
	}

	public String[] getGroundActions() {
		return (String[]) ((RSItemDefGroundActions) definition).getRSItemDefGroundActions();
	}
}
