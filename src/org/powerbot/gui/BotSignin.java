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
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.powerbot.gui.component.BotLocale;
import org.powerbot.util.Configuration;

/**
 * @author Paris
 */
public final class BotSignin extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(BotSignin.class.getName());
	private final CredentialTextField username, password;
	private final JButton signin;
	private final JLabel register;

	public BotSignin(final Frame parent) {
		super(parent, BotLocale.SIGNIN, true);

		final GridLayout gridCredentials = new GridLayout(2, 1);
		gridCredentials.setVgap(5);
		final JPanel panelCredentials = new JPanel(gridCredentials);

		username = new CredentialTextField(BotLocale.USERNAME, false);
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
				BotChrome.openURL(Configuration.URLs.REGISTER);
			}
		});
		panelAction.add(register);

		signin = new JButton(BotLocale.SIGNIN);
		signin.setPreferredSize(new Dimension((int) (signin.getPreferredSize().width * 1.2), (int) (signin.getPreferredSize().height * 1.2)));
		signin.addActionListener(this);
		panelAction.add(signin);

		final JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.setBorder(panelAction.getBorder());
		panel.add(panelCredentials);

		add(panel);
		add(panelAction, BorderLayout.SOUTH);

		pack();
		setMinimumSize(getSize());
		setResizable(false);
		setLocationRelativeTo(getParent());
		setVisible(true);
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		final Object s = arg0.getSource();
		if (s == signin) {
			if (username.getText().length() != 0 && password.getText().length() != 0) {
				log.info("Logging in with " + username.getText() + ":" + password.getText());
			}
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

		@Override
		public void focusGained(final FocusEvent arg0) {
			if (getForeground() == altColor) {
				setText("");
				if (password) {
					setEchoChar(defaultEchoChar);
				}
				setForeground(initColor);
			}
		}

		@Override
		public void focusLost(final FocusEvent arg0) {
			final String text = new String(getPassword());
			if (text.length() == 0) {
				setForeground(altColor);
				if (password) {
					setEchoChar((char) 0);
				}
				setText(initText);
			}
		}

		@Override
		public String getText() {
			return getForeground() == altColor ? "" : new String(getPassword());
		}
	}
}
