package org.powerbot.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.powerbot.Configuration;
import org.powerbot.misc.GameAccounts;
import org.powerbot.misc.GoogleAnalytics;
import org.powerbot.misc.NetworkAccount;
import org.powerbot.misc.ScriptBundle;
import org.powerbot.misc.ScriptList;
import org.powerbot.script.ClientContext;

/**
 */
class BotPreferences extends JDialog implements Runnable {
	private static final int PAD = 5;
	private final BotChrome chrome;
	private final JPanel panel;
	private final JComboBox account;
	private final JPasswordField password, accountPassword;
	private final JLabel description, labelPassword, labelUsername;
	private final JCheckBox members;
	private final JTextField username, pin;
	private final JButton signin, play;
	private final JList script;
	private final JScrollPane scrollScript;
	private final AtomicBoolean blocksave = new AtomicBoolean(false);

	private final Component[] itemsAccount;
	private final List<ScriptBundle.Definition> list;

	public static final AtomicBoolean loading = new AtomicBoolean(false), visible = new AtomicBoolean(false);

	public BotPreferences(final BotChrome chrome) {
		super(chrome.window.get(), true);
		this.chrome = chrome;
		visible.set(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(false);

		list = new ArrayList<ScriptBundle.Definition>();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				setVisible(false);
				GameAccounts.getInstance().save();
				visible.set(false);
				dispose();
			}
		});

		labelUsername = new JLabel();
		username = new JTextField();
		password = new JPasswordField();
		labelPassword = new JLabel();
		signin = new JButton();
		scrollScript = new JScrollPane();
		script = new JList();
		account = new JComboBox();
		final JLabel labelAccountPassword = new JLabel();
		accountPassword = new JPasswordField();
		final JLabel labelPin = new JLabel();
		pin = new JTextField();
		members = new JCheckBox();
		play = new JButton();
		description = new JLabel();

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		labelUsername.setText(BotLocale.USERNAME_OR_EMAIL + ":");
		labelUsername.setVisible(false);
		final DocumentListener docSignin;
		username.getDocument().addDocumentListener(docSignin = new DocumentListener() {
			@Override
			public void insertUpdate(final DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void removeUpdate(final DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
				signin.setText(username.getText().isEmpty() || password.getPassword().length == 0 ? BotLocale.GET_PIN : BotLocale.SIGN_IN);
			}
		});

		labelPassword.setText(BotLocale.PASSWORD_OR_PIN + ":");
		labelPassword.setVisible(false);
		password.enableInputMethods(true);
		password.getDocument().addDocumentListener(docSignin);

		signin.setText(BotLocale.GET_PIN);
		signin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (signin.getText().equals(BotLocale.GET_PIN)) {
					BotChrome.openURL(Configuration.URLs.LOGIN_PIN);
					return;
				}

				signin.setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						final NetworkAccount n = NetworkAccount.getInstance();
						String txt = null;

						if (n.isLoggedIn()) {
							list.clear();
							n.logout();
							GoogleAnalytics.getInstance().pageview("signin/logout", getTitle());
						} else {
							if (!n.login(username.getText(), new String(password.getPassword()), "") || !n.isLoggedIn()) {
								final String msg = n.getResponse();
								txt = msg == null || msg.isEmpty() ? BotLocale.INVALID_CREDENTIALS : msg;
							}
							GoogleAnalytics.getInstance().pageview("signin/login", getTitle());
						}

						BotPreferences.this.run();
						if (txt != null && !txt.isEmpty()) {
							final String msg = txt;
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									description.setText(msg);
									description.setVisible(true);
									panel.setBorder(BorderFactory.createEmptyBorder(0, PAD, PAD * 3, PAD));
									pack();
								}
							});
						}
					}
				}).start();
			}
		});

		script.setModel(new AbstractListModel() {
			public int getSize() {
				return list.size();
			}

			public Object getElementAt(final int i) {
				final ScriptBundle.Definition d = list.get(i);
				return (d.local ? "[L] " : "") + d.getName();
			}
		});
		script.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					play.setEnabled(false);
					return;
				}

				final ScriptBundle.Definition d = script.getSelectedIndex() < 0 || list.isEmpty() ? null : list.get(script.getSelectedIndex());
				play.setEnabled(d != null);

				String s = d == null ? "" : d.getDescription();
				final int w = Math.min(375, username.getWidth() + password.getWidth());
				int n = 0;

				final FontMetrics f = description.getFontMetrics(description.getFont());
				while (f.stringWidth(s) > w) {
					s = s.substring(0, s.length() - 1);
					n++;
				}

				description.setText(s + (n > 1 ? "\u2026" : ""));
			}
		});
		script.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		script.setEnabled(false);
		scrollScript.setViewportView(script);

		account.setModel(new ComboBoxModel() {
			private final String[] pre = {BotLocale.NO_ACCOUNT},
					post = {BotLocale.REMOVE, BotLocale.ADD};
			private Object selected;

			@Override
			public void setSelectedItem(final Object anItem) {
				selected = anItem;
			}

			@Override
			public Object getSelectedItem() {
				return selected;
			}

			@Override
			public int getSize() {
				return GameAccounts.getInstance().size() + pre.length + post.length;
			}

			@Override
			public Object getElementAt(final int index) {
				final List<String> items = new ArrayList<String>(getSize());
				items.addAll(Arrays.asList(pre));
				for (final GameAccounts.Account a : GameAccounts.getInstance()) {
					items.add(a.toString());
				}
				items.addAll(Arrays.asList(post));
				return items.get(index);
			}

			@Override
			public void addListDataListener(final ListDataListener l) {
			}

			@Override
			public void removeListDataListener(final ListDataListener l) {
			}
		});
		account.setRenderer(new BasicComboBoxRenderer() {
			@Override
			public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
				if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				} else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}

				final int l = list.getModel().getSize();
				if (index < 1 || index >= l - 2) {
					setBackground(list.getBackground());
					setForeground(UIManager.getColor(index < 1 ? "Label.disabledForeground" : index == l - 2 ? "Button.select" : "Button.light"));

					if (isSelected) {
						final Color c = getBackground();
						setBackground(getForeground());
						setForeground(c);
					}
				}

				setFont(list.getFont());
				setText((value == null) ? "" : value.toString());
				return this;
			}
		});
		final AtomicInteger accountIndex = new AtomicInteger(0);
		account.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final int i = account.getSelectedIndex(), z = account.getModel().getSize();
				final GameAccounts g = GameAccounts.getInstance();
				final GameAccounts.Account a = i > 0 && i < z - 2 ? g.get(i - 1) : null;

				blocksave.set(true);
				if (a == null) {
					members.setSelected(false);
					accountPassword.setText("");
					pin.setText("");
				} else {
					members.setSelected(a.member);
					accountPassword.setText(a.getPassword());
					final String p = a.getPIN();
					pin.setText(p == null || p.isEmpty() || p.indexOf('-') == 0 ? "          " : p + "  ");
				}
				blocksave.set(false);

				for (final Component c : itemsAccount) {
					c.setVisible(a != null);
				}

				final GoogleAnalytics t = GoogleAnalytics.getInstance();

				if (i == z - 1) {
					final String s = JOptionPane.showInputDialog(BotPreferences.this, "Enter username:", "New Account", JOptionPane.PLAIN_MESSAGE);
					if (s != null) {
						if (!g.contains(s)) {
							g.add(s);
							account.updateUI();
						}
						account.setSelectedIndex(g.find(s) + 1);
					} else {
						account.setSelectedIndex(0);
					}
					accountIndex.set(0);
					actionPerformed(e);
					t.pageview("accounts/add/", BotLocale.ADD);
				} else if (i == z - 2) {
					final int j = accountIndex.get();
					final GameAccounts.Account b = j > 0 && j < z - 2 ? g.get(j - 1) : null;
					if (b != null) {
						g.remove(b);
						account.updateUI();
					}
					account.setSelectedIndex(0);
					accountIndex.set(0);
					actionPerformed(e);
					t.pageview("accounts/remove/", BotLocale.REMOVE);
				} else {
					accountIndex.set(i);
					if (i == 0) {
						t.pageview("accounts/none/", BotLocale.NO_ACCOUNT);
					} else {
						t.pageview("accounts/", "");
					}
				}
			}
		});
		account.setToolTipText("Select game account");

		labelAccountPassword.setText(BotLocale.PASSWORD + ":");

		accountPassword.setText("");
		final DocumentListener docAccount;
		accountPassword.getDocument().addDocumentListener(docAccount = new DocumentListener() {
			@Override
			public void insertUpdate(final DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void removeUpdate(final DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
				save();
			}
		});
		accountPassword.enableInputMethods(true);

		labelPin.setText(BotLocale.PIN + ":");

		pin.setText("          ");
		pin.getDocument().addDocumentListener(docAccount);

		members.setText(BotLocale.MEMBER);
		members.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				save();
			}
		});

		play.setText(BotLocale.SCRIPT_PLAY);
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				loading.set(true);
				setVisible(false);

				new Thread(new Runnable() {
					@Override
					public void run() {
						GameAccounts.getInstance().save();
						final int s = script.getSelectedIndex(), u = account.getSelectedIndex();
						final ScriptBundle.Definition d = s < 0 || s > list.size() ? null : list.get(s);
						final GameAccounts.Account a = u < 1 ? null : GameAccounts.getInstance().get(u - 1);
						final String n = a == null ? "" : a.toString();
						ScriptList.load(chrome, d, n);
						GoogleAnalytics.getInstance().pageview("launch/play/", play.getText());
						loading.set(false);
					}
				}).start();

				visible.set(false);
				dispose();
			}
		});
		play.setEnabled(false);

		description.setForeground(UIManager.getDefaults().getColor("textInactiveText"));
		description.setFont(description.getFont().deriveFont(description.getFont().getSize2D() - 1f));
		description.setText("");

		panel = new JPanel();
		setContentPane(panel);

		final GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
														.addComponent(labelUsername)
														.addComponent(scrollScript)
														.addComponent(username, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(labelPassword)
														.addGroup(layout.createSequentialGroup()
																.addGap(6, 6, 6)
																.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
																		.addGroup(layout.createSequentialGroup()
																				.addComponent(labelAccountPassword)
																				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
																				.addComponent(accountPassword, GroupLayout.PREFERRED_SIZE, 193, GroupLayout.PREFERRED_SIZE))
																		.addGroup(layout.createSequentialGroup()
																				.addComponent(labelPin)
																				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
																				.addComponent(pin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																				.addComponent(members))))
														.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
																.addComponent(account, GroupLayout.Alignment.LEADING, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																.addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
																		.addComponent(password, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(signin))))
												.addGap(0, 0, Short.MAX_VALUE))
										.addGroup(layout.createSequentialGroup()
												.addComponent(description, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(play)))
								.addContainerGap())
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(labelUsername)
										.addComponent(labelPassword))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(username, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(password, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(signin))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(scrollScript, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
										.addGroup(layout.createSequentialGroup()
												.addComponent(account, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(labelAccountPassword)
														.addComponent(accountPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(labelPin)
														.addComponent(pin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
														.addComponent(members))))
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(play)
										.addComponent(description)))
		);

		itemsAccount = new Component[]{labelAccountPassword, accountPassword, labelPin, pin, members};
		for (final Component c : itemsAccount) {
			c.setVisible(false);
		}

		pack();
		setLocationRelativeTo(getParent());
		new Thread(this).start();
		setVisible(true);
	}

	@Override
	public synchronized void run() {
		final NetworkAccount n = NetworkAccount.getInstance();
		list.clear();
		if (n.isLoggedIn()) {
			final List<ScriptBundle.Definition> s = new ArrayList<ScriptBundle.Definition>();
			try {
				s.addAll(ScriptList.getList());
			} catch (final IOException ignored) {
				if (n.isLoggedIn()) {
					ignored.printStackTrace();
					return;
				}
			}
			final Class<? extends ClientContext> c = chrome.bot.get().ctx.getClass();
			for (final ScriptBundle.Definition e : s) {
				if (e.client != null && !c.isAssignableFrom((Class<?>) e.client)) {
					continue;
				}
				list.add(e);
			}
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final boolean l = n.isLoggedIn();
				setTitle(l ? BotLocale.SCRIPTS : BotLocale.SIGN_IN);

				labelUsername.setVisible(!l);
				username.setText(l ? n.getDisplayName() : "");
				username.setEnabled(!l);
				labelPassword.setVisible(!l);
				password.setText(l ? "********" : "");
				password.setEnabled(!l);
				signin.setText(l ? BotLocale.SIGN_OUT : username.getText().isEmpty() || password.getPassword().length == 0 ? BotLocale.GET_PIN : BotLocale.SIGN_IN);
				signin.setEnabled(true);

				script.setEnabled(l);
				scrollScript.setVisible(l);
				account.setVisible(l);
				description.setVisible(l);
				play.setVisible(l);

				for (final Component c : itemsAccount) {
					c.setVisible(false);
				}

				script.setSelectedIndex(0);
				script.updateUI();
				script.getListSelectionListeners()[0].valueChanged(new ListSelectionEvent(script, 0, 0, false));
				account.setSelectedIndex(0);
				account.getActionListeners()[0].actionPerformed(new ActionEvent(account, 0, null));

				getRootPane().setDefaultButton(l ? play : signin);
				final int p = Configuration.OS == Configuration.OperatingSystem.MAC || l ? 0 : PAD;
				panel.setBorder(BorderFactory.createEmptyBorder(0, PAD, PAD * 2 + p, PAD + p));
				pack();

				if (n.isLoggedIn() && list.isEmpty()) {
					final int r = JOptionPane.showConfirmDialog(BotPreferences.this,
							"You have no scripts added to your collection." + System.getProperty("line.separator") +
									"Would you like to browse " + Configuration.URLs.DOMAIN + " for scripts?", "",
							JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null);
					if (r == JOptionPane.YES_OPTION) {
						BotChrome.openURL(Configuration.URLs.SCRIPTS_BROWSE);
					}
				}
			}
		});

		GoogleAnalytics.getInstance().pageview(n.isLoggedIn() ? "launch/" : "signin/", getTitle());
	}

	private void save() {
		if (blocksave.get()) {
			return;
		}

		final int s = account.getSelectedIndex();
		final GameAccounts.Account a = s > 0 && s < account.getModel().getSize() - 2 ? GameAccounts.getInstance().get(s - 1) : null;
		if (a == null) {
			return;
		}

		a.setPassword(new String(accountPassword.getPassword()));
		final String p = pin.getText().trim();
		try {
			a.pin = Integer.parseInt(p);
		} catch (final NumberFormatException ignored) {
			a.pin = -1;
		}
		a.member = members.isSelected();
	}
}
