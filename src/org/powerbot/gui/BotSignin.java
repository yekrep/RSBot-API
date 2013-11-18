package org.powerbot.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.powerbot.Configuration;
import org.powerbot.gui.component.BotLocale;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.Tracker;

/**
 * @author Paris
 */
public final class BotSignin extends JDialog implements ActionListener {

	private static final long serialVersionUID = -5079757502617361896L;
	private final JButton signin;
	private final JLabel register;
	private final JTextField username;
	private final JPasswordField password;

	public BotSignin(final BotChrome parent) {
		super(parent, BotLocale.SIGNIN + " to " + BotLocale.WEBSITE, true);
		setFont(getFont().deriveFont(getFont().getSize2D() * 1.5f));

		final JLabel labelUsername = new JLabel();
		username = new JTextField();
		final JLabel labelPassword = new JLabel();
		password = new JPasswordField();
		signin = new JButton();
		final JLabel lostPass = new JLabel();
		register = new JLabel();
		final JPanel panelSide = new JPanel();
		final JLabel info = new JLabel();

		for (final Component c : new Component[]{labelUsername, username, labelPassword, password, signin, lostPass, register, panelSide, info}) {
			c.setFont(getFont());
		}

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		labelUsername.setHorizontalAlignment(SwingConstants.RIGHT);
		labelUsername.setText(BotLocale.USERNAME_OR_EMAIL);

		labelPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		labelPassword.setText(BotLocale.PASSWORD);

		signin.setText(BotLocale.SIGNIN);
		signin.addActionListener(this);
		signin.setFocusable(false);

		lostPass.setFont(lostPass.getFont().deriveFont(lostPass.getFont().getSize2D() - 3f));
		lostPass.setText(BotLocale.FORGOTPASS);
		lostPass.setForeground(new Color(0, 0, 0xcc));
		lostPass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lostPass.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				BotChrome.openURL(Configuration.URLs.LOSTPASS);
			}
		});
		lostPass.setVisible(false);

		register.setFont(register.getFont().deriveFont(register.getFont().getSize2D() - 2f));
		register.setText("<html><a href='#'>" + BotLocale.REGISTER + "</a></html>");
		register.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		register.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				BotChrome.openURL(Configuration.URLs.REGISTER);
			}
		});

		info.setHorizontalAlignment(SwingConstants.CENTER);
		info.setText("<html><center>Sign in to your " + BotLocale.WEBSITE + " account to access your script collection</center></html>");
		info.setFont(info.getFont().deriveFont(info.getFont().getSize2D() * 0.7f));
		info.setHorizontalAlignment(SwingConstants.CENTER);
		info.setOpaque(true);
		info.setBackground(null);
		panelSide.setBackground(new Color(0xdd, 0xdd, 0xdd));
		panelSide.setBorder(BorderFactory.createEtchedBorder());
		panelSide.setVisible(false);

		final GroupLayout panelInfoLayout = new GroupLayout(panelSide);
		panelSide.setLayout(panelInfoLayout);
		panelInfoLayout.setHorizontalGroup(
				panelInfoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(panelInfoLayout.createSequentialGroup()
								.addContainerGap()
								.addComponent(info, GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
								.addContainerGap())
		);
		panelInfoLayout.setVerticalGroup(
				panelInfoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(panelInfoLayout.createSequentialGroup()
								.addContainerGap()
								.addComponent(info)
								.addContainerGap())
		);

		final GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
										.addGroup(layout.createSequentialGroup()
												.addComponent(register)
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(signin))
										.addComponent(lostPass)
										.addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
														.addComponent(labelUsername, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(labelPassword, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
												.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
														.addComponent(username)
														.addComponent(password, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))))
								.addGap(18, 18, 18)
								.addComponent(panelSide, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addContainerGap())
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(panelSide, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(labelUsername)
														.addComponent(username, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(labelPassword)
														.addComponent(password, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(lostPass)
												.addGap(18, 18, 18)
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(signin)
														.addComponent(register))
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
			register.setVisible(false);
		} else {
			signin.setText(BotLocale.SIGNIN);
			username.setText(null);
			username.setEnabled(true);
			password.setText(null);
			password.setEnabled(true);
			register.setVisible(true);
		}
		signin.setEnabled(true);
	}
}
