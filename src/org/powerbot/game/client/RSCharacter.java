package org.powerbot.game.client;

public interface RSCharacter extends RSAnimable {
	public Model getModel();

	public int getInteracting();

	public int isMoving();

	public int[] getAnimationQueue();

	public RSAnimator getAnimation();

	public int getHeight();

	public LinkedList getCombatStatusList();

	public int getOrientation();

	public RSMessageData getMessageData();

	public RSAnimator getPassiveAnimation();
}
