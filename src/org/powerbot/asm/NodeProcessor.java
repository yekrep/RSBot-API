package org.powerbot.asm;

import org.powerbot.lang.AdaptException;

/**
 * Represents a container of ClassNodes and basic manipulative methods.
 *
 * @author Timer
 */
public interface NodeProcessor {
	public void adapt() throws AdaptException;

	public byte[] process(String name, byte[] data);
}
