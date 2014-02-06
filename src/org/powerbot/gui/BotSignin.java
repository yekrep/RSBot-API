package org.powerbot.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.powerbot.misc.NetworkAccount;
import org.powerbot.misc.Tracker;

public final class BotSignin extends JDialog implements ActionListener {
	private static final long serialVersionUID = -5079757502617361896L;
	private final JButton signin;
	private final JTextField username;
	private final JPasswordField password;

	public BotSignin(final BotChrome parent) {
		super(parent, BotLocale.SIGNIN, true);

		final JLabel labelUsername = new JLabel();
		username = new JTextField();
		final JLabel labelPassword = new JLabel();
		password = new JPasswordField();
		signin = new JButton();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		labelUsername.setHorizontalAlignment(SwingConstants.RIGHT);
		labelUsername.setText(BotLocale.USERNAME);

		labelPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		labelPassword.setText(BotLocale.PASSWORD);

		signin.setText(BotLocale.SIGNIN);
		signin.addActionListener(this);
		signin.setFocusable(false);

		final GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
										.addGroup(layout.createSequentialGroup()
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(signin))
										.addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
														.addComponent(labelUsername, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(labelPassword, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
												.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
														.addComponent(username)
														.addComponent(password, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))))
								.addContainerGap())
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(labelUsername)
														.addComponent(username, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(labelPassword)
														.addComponent(password, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(signin))
												.addGap(0, 0, Short.MAX_VALUE)))
								.addContainerGap())
		);

		updateState(NetworkAccount.getInstance().isLoggedIn());
		getRootPane().setDefaultButton(signin);

		pack();
		setMinimumSize(getSize());
		setResizable(false);
		setLocationRelativeTo(getParent());
		setVisible(true);

		Tracker.getInstance().trackPage("signin/", getTitle());
	}

	public void actionPerformed(final ActionEvent e) {
		signin.setEnabled(false);
		username.setEnabled(false);
		password.setEnabled(false);
		if (signin.getText().equals(BotLocale.SIGNIN)) {
			final String user = username.getText().trim(), pass = new String(password.getPassword()).trim();
			if (!user.isEmpty() && !pass.isEmpty()) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						final boolean success = NetworkAccount.getInstance().login(user, pass, "");
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								updateState(success);
								if (success) {
									setVisible(false);
									dispose();
								} else {
									final String m = NetworkAccount.getInstance().getResponse();
									JOptionPane.showMessageDialog(BotSignin.this, m == null || m.isEmpty() ? BotLocale.INVALIDCREDENTIALS : m, BotLocale.ERROR, JOptionPane.ERROR_MESSAGE);
								}
							}
						});
					}
				}).start();
			}
			Tracker.getInstance().trackPage("signin/login", getTitle());
		} else if (signin.getText().equals(BotLocale.SIGNOUT)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					NetworkAccount.getInstance().logout();
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							updateState(false);
						}
					});
				}
			}).start();
			Tracker.getInstance().trackPage("signin/logout", getTitle());
		}
	}

	private void updateState(final boolean signedin) {
		if (signedin) {
			username.setText(NetworkAccount.getInstance().getDisplayName());
			username.setEnabled(false);
			password.setText("********");
			password.setEnabled(false);
			signin.setText(BotLocale.SIGNOUT);
		} else {
			signin.setText(BotLocale.SIGNIN);
			username.setText(null);
			username.setEnabled(true);
			password.setText(null);
			password.setEnabled(true);
		}
		signin.setEnabled(true);
	}
}
