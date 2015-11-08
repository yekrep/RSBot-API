package org.powerbot.script.rt4;

/**
 * {@inheritDoc}
 */
public class GeItem extends org.powerbot.script.GeItem {

	/**
	 * {@inheritDoc}
	 */
	public GeItem(final int id) {
		super("oldschool", id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.powerbot.script.GeItem nil() {
		return new GeItem(0);
	}
}
