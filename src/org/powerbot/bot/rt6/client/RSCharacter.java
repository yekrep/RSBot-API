package org.powerbot.bot.rt6.client;

public interface RSCharacter extends RSAnimable {
	AbstractModel getModel();

	int getInteracting();

	int isMoving();

	int[] getAnimationQueue();

	RSAnimator getAnimation();

	int getHeight();

	LinkedList getCombatStatusList();

	int getOrientation();

	RSMessageData getMessageData();

	RSAnimator getPassiveAnimation();
}
