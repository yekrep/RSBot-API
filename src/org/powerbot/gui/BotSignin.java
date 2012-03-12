package org.powerbot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.powerbot.gui.component.BotLocale;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public final class BotSignin extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final CredentialTextField username, password;
	private final JButton signin;
	private final JLabel register;

	public BotSignin(final Frame parent) {
		super(parent, BotLocale.SIGNIN, true);

		final GridLayout gridCredentials = new GridLayout(2, 1);
		gridCredentials.setVgap(5);
		final JPanel panelCredentials = new JPanel(gridCredentials);

		username = new CredentialTextField(BotLocale.USERNAME_OR_EMAIL, false);
		panelCredentials.add(username);
		password = new CredentialTextField(BotLocale.PASSWORD, true);
		panelCredentials.add(password);

		final GridLayout gridAction = new GridLayout(1, 2);
		gridAction.setHgap(gridCredentials.getVgap());
		final JPanel panelAction = new JPanel(gridAction);
		final int pad = gridAction.getHgap();
		panelAction.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));

		register = new JLabel("<html><a href='#'>" + BotLocale.REGISTER + "</a></html>");
		register.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent arg0) {
				BotChrome.openURL(Resources.getServerLinks().get("register"));
			}
		});
		panelAction.add(register);

		signin = new JButton(BotLocale.SIGNIN);
		signin.setFocusable(false);
		signin.setPreferredSize(new Dimension((int) (signin.getPreferredSize().width * 1.2), (int) (signin.getPreferredSize().height * 1.2)));
		signin.addActionListener(this);
		panelAction.add(signin);

		final JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.setBorder(panelAction.getBorder());
		panel.add(panelCredentials);

		add(panel);
		add(panelAction, BorderLayout.SOUTH);

		updateState();
		getRootPane().setDefaultButton(signin);
		register.requestFocusInWindow();

		pack();
		setMinimumSize(getSize());
		setResizable(false);
		setLocationRelativeTo(getParent());
		setVisible(true);
	}

	public void actionPerformed(final ActionEvent arg0) {
		final Object s = arg0.getSource();
		if (s == signin) {
			signin.setEnabled(false);
			if (signin.getText().equals(BotLocale.SIGNIN)) {
				if (username.getText().length() != 0 && password.getText().length() != 0) {
					boolean success = false;
					try {
						success = NetworkAccount.getInstance().login(username.getText(), password.getText());
					} catch (final IOException ignored) {
					}
					updateState();
					if (success) {
						dispose();
					} else {
						JOptionPane.showMessageDialog(this, BotLocale.INVALIDCREDENTIALS, BotLocale.ERROR, JOptionPane.ERROR_MESSAGE);
					}
					signin.setEnabled(true);
				}
			} else if (signin.getText().equals(BotLocale.SIGNOUT)) {
				NetworkAccount.getInstance().logout();
				updateState();
			}
		}
	}

	private void updateState() {
		if (NetworkAccount.getInstance().isLoggedIn()) {
			username.setText(NetworkAccount.getInstance().getAccount().getDisplayName());
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
	}

	private class CredentialTextField extends JPasswordField implements FocusListener {
		private static final long serialVersionUID = 1L;
		final boolean password;
		final char defaultEchoChar;
		final String initText;
		final Color initColor, altColor = Color.GRAY;

		public CredentialTextField(final String text, final boolean password) {
			super(text);
			setFont(getFont().deriveFont((float) (getFont().getSize() * 1.3)));
			initColor = getForeground();
			setForeground(altColor);
			this.password = password;
			defaultEchoChar = getEchoChar();
			setEchoChar((char) 0);
			initText = text;
			addFocusListener(this);
		}

		public void focusGained(final FocusEvent arg0) {
			if (getForeground() == altColor) {
				super.setText("");
				if (password) {
					setEchoChar(defaultEchoChar);
				}
				setForeground(initColor);
			}
		}

		public void focusLost(final FocusEvent arg0) {
			final String text = new String(getPassword());
			if (text.length() == 0) {
				setForeground(altColor);
				if (password) {
					setEchoChar((char) 0);
				}
				super.setText(initText);
			}
		}

		@Override
		public String getText() {
			return getForeground() == altColor ? "" : new String(getPassword());
		}

		@Override
		public void setText(final String t) {
			if (t == null || t.length() == 0) {
				super.setText("");
				focusLost(null);
			} else {
				setForeground(initColor);
				if (password) {
					setEchoChar(defaultEchoChar);
				}
				super.setText(t);
			}
		}
	}
}
