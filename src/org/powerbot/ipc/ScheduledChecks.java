package org.powerbot.ipc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.powerbot.service.NetworkAccount;

/**
 * @author Paris
 */
public final class ScheduledChecks implements ActionListener {
	public static volatile long SESSION_TIME = 0;

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (!Controller.getInstance().isBound() || Controller.getInstance().getRunningInstances() < 1) {
			System.exit(1);
		}

		if (NetworkAccount.getInstance().isLoggedIn()) {
			final long a = Controller.getInstance().getLastSessionUpdateTime();
			if (a < System.currentTimeMillis() - 1000 * 60 * 10) {
				SESSION_TIME = System.currentTimeMillis();
				NetworkAccount.getInstance().session(0);
			}
		}
	}
}
