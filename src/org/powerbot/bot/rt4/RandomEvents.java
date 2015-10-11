package org.powerbot.bot.rt4;

import org.powerbot.script.Filter;
import org.powerbot.script.PollingScript;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;

public class RandomEvents extends PollingScript<ClientContext> {
	public RandomEvents() {
		priority.set(3);
	}

	private boolean isValid() {
		return !ctx.npcs.select().within(5d).action("Dismiss").select(new Filter<Npc>() {
			@Override
			public boolean accept(final Npc npc) {
				return npc.interacting().equals(ctx.players.local());
			}
		}).isEmpty();
	}

	@Override
	public void poll() {
		if (!isValid()) {
			if (threshold.contains(this)) {
				threshold.remove(this);
			}
			return;
		}
		if (!threshold.contains(this)) {
			threshold.add(this);
		}

		ctx.npcs.poll().interact(false, "Dismiss");
	}
}