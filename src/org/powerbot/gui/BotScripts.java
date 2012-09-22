package org.powerbot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

import org.powerbot.core.bot.Bot;
import org.powerbot.core.script.Script;
import org.powerbot.game.api.Manifest;
import org.powerbot.gui.component.BotLocale;
import org.powerbot.gui.component.BotToolBar;
import org.powerbot.ipc.Controller;
import org.powerbot.service.GameAccounts;
import org.powerbot.service.GameAccounts.Account;
import org.powerbot.service.NetworkAccount;
import org.powerbot.service.scripts.ScriptClassLoader;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.util.Configuration;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.IniParser;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public final class BotScripts extends JDialog implements ActionListener {
	private static final Logger log = Logger.getLogger(BotScripts.class.getName());
	private static final long serialVersionUID = 1L;
	private final BotToolBar parent;
	private final JScrollPane scroll;
	private final JPanel table;
	private final JToggleButton locals;
	private final JButton username, refresh;
	private final JTextField search;
	private final boolean localOnly;
	private volatile boolean init;

	public BotScripts(final BotToolBar parent) {
		super(parent.parent, BotLocale.SCRIPTS, true);

		if (!NetworkAccount.getInstance().isLoggedIn() && !Configuration.SUPERDEV) {
			new BotSignin(parent.parent);
		}

		setIconImage(Resources.getImage(Resources.Paths.FILE));
		this.parent = parent;

		localOnly = Configuration.DEVMODE;
		if (localOnly) {
			setTitle(getTitle() + " (showing only local scripts)");
		}

		final JToolBar toolbar = new JToolBar();
		final int d = 2;
		toolbar.setBorder(new EmptyBorder(d, d, d, d));
		toolbar.setFloatable(false);
		final FlowLayout flow = new FlowLayout(FlowLayout.RIGHT);
		flow.setHgap(0);
		flow.setVgap(0);
		final JPanel panelRight = new JPanel(flow);
		add(toolbar, BorderLayout.NORTH);

		refresh = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.REFRESH)));
		refresh.setToolTipText(BotLocale.REFRESH);
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				refresh();
			}
		});
		refresh.setFocusable(false);
		toolbar.add(refresh);
		locals = new JToggleButton(new ImageIcon(Resources.getImage(Resources.Paths.PENCIL)));
		locals.setToolTipText(BotLocale.LOCALONLY);
		locals.addActionListener(this);
		locals.setFocusable(false);
		locals.setVisible(Configuration.DEVMODE);
		toolbar.add(locals);
		toolbar.add(Box.createHorizontalStrut(d));

		username = new JButton(BotLocale.NOACCOUNT);
		username.setFont(username.getFont().deriveFont(username.getFont().getSize2D() - 1f));
		username.addActionListener(this);
		username.setFocusable(false);
		username.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.KEYS)));
		toolbar.add(username);

		search = new JTextField(BotLocale.SEARCH);
		final Color searchColor[] = {search.getForeground(), Color.GRAY};
		search.setForeground(searchColor[1]);
		search.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent e) {
				actionPerformed(new ActionEvent(search, search.hashCode(), search.getText()));
			}
		});
		search.addFocusListener(new FocusListener() {
			public void focusGained(final FocusEvent e) {
				final JTextField f = (JTextField) e.getSource();
				if (f.getForeground().equals(searchColor[1])) {
					f.setText("");
					f.setForeground(searchColor[0]);
				}
			}

			public void focusLost(final FocusEvent e) {
				final JTextField f = (JTextField) e.getSource();
				if (f.getText().length() == 0) {
					f.setForeground(searchColor[1]);
					f.setText(BotLocale.SEARCH);
				}
			}
		});
		search.setPreferredSize(new Dimension(150, search.getPreferredSize().height));
		panelRight.add(search);
		panelRight.add(Box.createHorizontalStrut(d));
		final JButton more = new JButton(BotLocale.BROWSE, new ImageIcon(Resources.getImage(Resources.Paths.SEARCH)));
		more.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		more.setToolTipText(BotLocale.BROWSETIP);
		more.setFont(username.getFont());
		more.setFocusable(false);
		more.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				BotChrome.openURL(Configuration.URLs.SCRIPTSLIST);
			}
		});
		panelRight.add(more);
		toolbar.add(panelRight);

		final FlowLayout tableFlow = new FlowLayout(FlowLayout.LEFT);
		tableFlow.setHgap(0);
		tableFlow.setVgap(0);
		table = new JPanel(tableFlow);
		table.setBorder(new EmptyBorder(0, 0, 0, 0));
		table.setPreferredSize(new Dimension(getPreferredCellSize().width, getPreferredCellSize().height));
		table.setPreferredSize(new Dimension(getPreferredCellSize().width * 2, getPreferredCellSize().height * table.getComponentCount() / 2));

		scroll = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(scroll.getPreferredSize().width, getPreferredCellSize().height * 3));

		final JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(final Graphics g) {
				adjustViewport();
				super.paintComponent(g);
			}
		};
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(scroll);
		add(panel);

		addComponentListener(new ComponentAdapter() {
			public void componentResized(final ComponentEvent e) {
				adjustViewport();
			}
		});
		pack();
		setMinimumSize(getSize());
		//setResizable(false);
		setLocationRelativeTo(getParent());

		final Timer t = new Timer(1000 * 60 * 2, new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				((Timer) e.getSource()).stop();
				if (isVisible()) {
					setVisible(false);
					dispose();
				}
			}
		});
		t.setCoalesce(false);
		t.start();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				refresh();
			}
		});

		if (NetworkAccount.getInstance().isLoggedIn() || Configuration.SUPERDEV) {
			setVisible(true);
		} else {
			dispose();
		}
	}

	public void adjustViewport() {
		int n = 0;
		for (final Component c : table.getComponents()) {
			if (c.isVisible()) {
				n++;
			}
		}
		final double w = Math.ceil(table.getWidth() / getPreferredCellSize().width);
		final int f = (int) Math.ceil(n / w) * getPreferredCellSize().height;
		scroll.setPreferredSize(new Dimension(scroll.getPreferredSize().width, f));
		table.setPreferredSize(new Dimension(table.getPreferredSize().width, f));
		if (scroll.getVerticalScrollBar().getValue() > f) {
			scroll.getVerticalScrollBar().setValue(f);
		}
		scroll.validate();
		scroll.repaint();
	}

	public void refresh() {
		refresh.setEnabled(false);
		table.removeAll();
		final JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(15, 15, 15, 15));
		final JLabel status = new JLabel("Loading...");
		status.setFont(status.getFont().deriveFont(status.getFont().getSize2D() * 1.75f));
		status.setForeground(Color.GRAY);
		status.setBorder(new EmptyBorder(0, 0, 0, 10));
		panel.add(status);
		final JProgressBar progress = new JProgressBar();
		progress.setIndeterminate(true);
		progress.setPreferredSize(new Dimension(progress.getPreferredSize().width * 3 / 2, status.getPreferredSize().height / 2));
		panel.add(progress);
		table.add(panel);
		table.validate();
		table.repaint();
		new Thread(new Runnable() {
			public void run() {
				final List<ScriptDefinition> scripts;
				try {
					scripts = loadScripts();
					Collections.sort(scripts, new Comparator<ScriptDefinition>() {
						@Override
						public int compare(final ScriptDefinition a, final ScriptDefinition b) {
							return a.getName().compareToIgnoreCase(b.getName());
						}
					});
					if (scripts.size() == 0) {
						status.setText("Click browse to add some scripts to your collection");
						progress.setVisible(false);
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								table.validate();
								table.repaint();
								refresh.setEnabled(true);
							}
						});
						return;
					}
				} catch (final IOException ignored) {
					status.setText("Could not load scripts, please try again later");
					progress.setVisible(false);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							table.validate();
							table.repaint();
						}
					});
					ignored.printStackTrace();
					return;
				}
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						table.removeAll();
						for (final ScriptDefinition def : scripts) {
							table.add(new ScriptCell(table, def));
						}
						table.validate();
						table.repaint();
						filter();
						refresh.setEnabled(true);
						init = true;
					}
				});
			}
		}).start();
	}

	public List<ScriptDefinition> loadScripts() throws IOException {
		final List<ScriptDefinition> list = new ArrayList<ScriptDefinition>();

		if (localOnly) {
			final List<File> paths = new ArrayList<File>(2);
			paths.add(new File("bin"));
			paths.add(new File("out"));
			for (final File path : paths) {
				if (path.isDirectory()) {
					loadLocalScripts(list, path, null);
				}
			}

			if (!Configuration.SUPERDEV) {
				return list;
			}
		}

		final Map<String, Map<String, String>> manifests = IniParser.deserialise(HttpClient.openStream(Configuration.URLs.SCRIPTSCOLLECTION, NetworkAccount.getInstance().getAccount().getAuth()));
		final boolean vip = NetworkAccount.getInstance().isVIP();
		for (final Entry<String, Map<String, String>> entry : manifests.entrySet()) {
			final Map<String, String> params = entry.getValue();
			if (params.containsKey("vip") && IniParser.parseBool(params.get("vip")) && !vip) {
				continue;
			}
			final ScriptDefinition def = ScriptDefinition.fromMap(params);
			if (def != null) {
				def.source = entry.getKey();
				if (entry.getValue().containsKey("className")) {
					def.className = entry.getValue().get("className");
					list.add(def);
				}
			}
		}
		return list;
	}

	public void loadLocalScripts(final List<ScriptDefinition> list, final File parent, final File dir) {
		if (!NetworkAccount.getInstance().isLoggedIn() || !((NetworkAccount.getInstance().getAccount().getPermissions() & NetworkAccount.Permissions.LOCALSCRIPTS) == NetworkAccount.Permissions.LOCALSCRIPTS)) {
			return;
		}
		for (final File file : (dir == null ? parent : dir).listFiles()) {
			if (file.isDirectory()) {
				loadLocalScripts(list, parent, file);
			} else if (file.isFile()) {
				final String name = file.getName();
				try {
					if (name.endsWith(".class") && name.indexOf('$') == -1) {
						if (name.length() < 9) {
							continue;
						}
						final FileInputStream fis = new FileInputStream(file);
						final byte[] data = new byte[512];
						fis.read(data);
						fis.close();
						if (data == null || data.length < 12) {
							continue;
						}
						if (data[7] != 0x33) {
							continue;
						}
						boolean skip = false;
						for (int i = 11; i < data.length - 2; ) {
							if (data[i++] == 0x5a && data[i++] == 0x4b && data[i++] == 0x4d) {
								skip = true;
								break;
							}
						}
						if (skip) {
							continue;
						}
						final URL src = parent.getCanonicalFile().toURI().toURL();
						@SuppressWarnings("resource")
						final ClassLoader cl = new URLClassLoader(new URL[]{src});
						String className = file.getCanonicalPath().substring(parent.getCanonicalPath().length() + 1);
						className = className.substring(0, className.lastIndexOf('.'));
						className = className.replace(File.separatorChar, '.');
						final Class<?> clazz = cl.loadClass(className);
						if (Script.class.isAssignableFrom(clazz)) {
							final Class<? extends Script> script = clazz.asSubclass(Script.class);
							if (script.isAnnotationPresent(Manifest.class)) {
								final Manifest m = script.getAnnotation(Manifest.class);
								final ScriptDefinition def = new ScriptDefinition(m);
								def.source = parent.getCanonicalFile().toString();
								def.className = className;
								def.local = true;
								list.add(def);
							}
						}
					}
				} catch (final Exception ignored) {
				}
			}
		}
	}

	public void actionPerformed(final ActionEvent e) {
		if (e == null || table.getComponentCount() == 0 || !(table.getComponent(0) instanceof ScriptCell)) {
			return;
		}
		if (e.getSource().equals(username)) {
			final JPopupMenu accounts = new JPopupMenu();
			final ActionListener l = new ActionListener() {
				public void actionPerformed(final ActionEvent e1) {
					username.setText(((JCheckBoxMenuItem) e1.getSource()).getText());
				}
			};
			boolean hit = false;
			if (GameAccounts.getInstance().size() == 0) {
				return;
			}
			for (final Account a : GameAccounts.getInstance()) {
				hit = username.getText().equalsIgnoreCase(a.toString());
				final JCheckBoxMenuItem item = new JCheckBoxMenuItem(a.toString(), hit);
				item.addActionListener(l);
				accounts.add(item);
			}
			accounts.addSeparator();
			final JCheckBoxMenuItem item = new JCheckBoxMenuItem(BotLocale.NOACCOUNT, !hit);
			item.addActionListener(l);
			accounts.add(item);
			accounts.show(username, 0, username.getHeight());
			return;
		}
		filter();
	}

	private void filter() {
		for (final Component c : table.getComponents()) {
			final ScriptDefinition d = ((ScriptCell) c).getScriptDefinition();
			boolean v = true;
			if (!search.getText().isEmpty() && !search.getText().equals(BotLocale.SEARCH) && !d.matches(search.getText())) {
				v = false;
			}
			if (locals.isSelected() && !d.local) {
				v = false;
			}
			c.setVisible(v);
		}
		adjustViewport();
		scroll.getVerticalScrollBar().setValue(0);
	}

	public Dimension getPreferredCellSize() {
		return new Dimension(340, 90);
	}

	private final class ScriptCell extends JPanel {
		private static final long serialVersionUID = 1L;
		private final Component parent;
		private final ScriptDefinition def;
		private final Color[] c = new Color[]{null, null};

		public ScriptCell(final Component parent, final ScriptDefinition def) {
			super();
			this.parent = parent;
			this.def = def;

			final int w = parent.getPreferredSize().width / getPreferredCellSize().width;
			final int row = getIndex() / w;

			setLayout(null);
			setBorder(new InsetBorder());
			setPreferredSize(getPreferredCellSize());
			final boolean alt = row % 2 == 1;
			c[0] = getBackground();
			final int s = 24;
			c[1] = new Color(c[0].getRed() - s, c[0].getGreen() - s, c[0].getBlue() - s);
			setBackground(alt ? c[1] : c[0]);

			final JLabel skill = new JLabel(new ImageIcon(getSkillImage(def.getCategory().index)));
			skill.setBounds(4, (getPreferredCellSize().height - skill.getPreferredSize().height) / 2, skill.getPreferredSize().width, skill.getPreferredSize().height);
			add(skill);

			final JPanel panelInfo = new JPanel(new GridLayout(0, 1));
			panelInfo.setBackground(null);
			final int dy = 4, dx = skill.getLocation().x + skill.getPreferredSize().width + 8;
			panelInfo.setBounds(dx, dy, getPreferredCellSize().width - dx - 1, getPreferredCellSize().height - dy * 2);
			add(panelInfo);

			final JLabel name = new JLabel(def.getName());
			name.setToolTipText(String.format("v%s by %s", def.getVersion(), def.getAuthors()));
			name.setFont(name.getFont().deriveFont(Font.BOLD));
			panelInfo.add(name, BorderLayout.NORTH);

			if (def.getWebsite() != null && !def.getWebsite().isEmpty()) {
				name.setForeground(new Color(0x23, 0x23, 0x8e));
				name.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(final MouseEvent arg0) {
						String url = def.getWebsite();
						url = String.format(Configuration.URLs.LINKFILTER, url.replace("&", "%26"));
						BotChrome.openURL(url);
					}
				});
				name.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			final JTextArea desc = new JTextArea(def.getDescription());
			desc.setBackground(null);
			desc.setEditable(false);
			desc.setBorder(null);
			desc.setLineWrap(true);
			desc.setWrapStyleWord(true);
			desc.setFocusable(false);
			desc.setFont(name.getFont().deriveFont(0, name.getFont().getSize2D() - 2f));
			panelInfo.add(desc);

			final JPanel panelIcons = new JPanel(new GridLayout(0, 2));
			panelInfo.add(panelIcons, BorderLayout.SOUTH);
			panelIcons.setBackground(null);
			final JPanel panelIconsLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panelIconsLeft.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			panelIconsLeft.setBackground(null);
			panelIcons.add(panelIconsLeft);
			final JPanel panelIconsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			panelIconsRight.setBackground(null);
			panelIcons.add(panelIconsRight);

			final JButton act = new JButton("Play");
			act.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					setVisible(false);
					dispose();
					if (!NetworkAccount.getInstance().isLoggedIn()) {
						return;
					}
					final ClassLoader cl;
					if (def.local) {
						try {
							cl = new ScriptClassLoader(new File(def.source).toURI().toURL());
						} catch (final IOException ignored) {
							return;
						}
					} else {
						try {
							final byte[] buf = IOHelper.read(HttpClient.openStream(Configuration.URLs.SCRIPTSDOWNLOAD, NetworkAccount.getInstance().getAccount().getAuth(), def.source.toString(), "key"));
							final Cipher c = Cipher.getInstance("RC4");
							c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(buf, 0, buf.length, "ARCFOUR"));
							final InputStream in = HttpClient.openStream(Configuration.URLs.SCRIPTSDOWNLOAD, NetworkAccount.getInstance().getAccount().getAuth(), def.source.toString(), "jar");
							cl = new ScriptClassLoader(new ZipInputStream(new CipherInputStream(in, c)));
						} catch (final Exception ignored) {
							log.severe("Could not download script");
							ignored.printStackTrace();
							return;
						}
						int n = 0;
						for (final String running : Controller.getInstance().getRunningScripts()) {
							if (def.getID().equals(running)) {
								n++;
							}
						}
						final Map<String, Map<String, String>> data;
						try {
							data = IniParser.deserialise(HttpClient.openStream(Configuration.URLs.SCRIPTSAUTH, NetworkAccount.getInstance().getAccount().getAuth(), def.getID(), Integer.toString(n)));
						} catch (final IOException ignored) {
							log.severe("Unable to obtain auth response");
							return;
						} catch (final NullPointerException ignored) {
							log.severe("Could not identify auth server");
							return;
						}
						if (data == null || !data.containsKey("auth")) {
							log.severe("Error reading auth response");
							return;
						}
						if (!data.get("auth").containsKey("access") || !IniParser.parseBool(data.get("auth").get("access"))) {
							if (data.get("auth").containsKey("message")) {
								JOptionPane.showMessageDialog(BotScripts.this, data.get("auth").get("message"));
							}
							log.severe("You are not authorised to run this script");
							return;
						}
					}
					final Script script;
					try {
						script = cl.loadClass(def.className).asSubclass(Script.class).newInstance();
					} catch (final Exception ignored) {
						log.severe("Error loading script");
						return;
					}
					final Bot bot = Bot.getInstance();
					bot.setAccount(null);
					for (final Account a : GameAccounts.getInstance()) {
						if (username.getText().equalsIgnoreCase(a.toString())) {
							bot.setAccount(a);
							break;
						}
					}
					log.info("Starting script");
					if (bot.getScriptHandler().start(script, def)) {
						BotScripts.this.parent.updateScriptControls();
					} else {
						log.severe("There is a script running");
					}
				}
			});
			act.setFont(act.getFont().deriveFont(Font.BOLD, act.getFont().getSize2D() - 1f));
			act.setBackground(null);
			act.setFocusable(false);
			panelIconsRight.add(act);
		}

		private Image getSkillImage(final int index) {
			final Image src;
			try {
				src = ImageIO.read(Resources.getResourceURL(Resources.Paths.SKILLS));
			} catch (final IOException ignored) {
				return null;
			}
			final int w = 32, h = 32;
			final BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			final Graphics2D g = img.createGraphics();
			final int y = img.getHeight() * index;
			g.drawImage(src, 0, 0, img.getWidth(), img.getHeight(), 0, y, img.getWidth(), y + img.getHeight(), null);
			g.dispose();
			return img.getScaledInstance((int) ((double) img.getWidth() / img.getHeight() * h), h, Image.SCALE_SMOOTH);
		}

		@Override
		public void paintComponent(final Graphics g) {
			if (c[0] != null && c[1] != null) {
				final int w = parent.getWidth() / getPreferredCellSize().width;
				final int row = getIndex() / w;
				final boolean alt = row % 2 == 1;
				setBackground(alt ? c[1] : c[0]);
			}
			super.paintComponent(g);
			adjustViewport();
			if (init) {
				scroll.getVerticalScrollBar().setValue(0);
				init = false;
			}
		}

		private int getIndex() {
			int index = 0;
			for (final Component c : ((JPanel) parent).getComponents()) {
				if (c == this) {
					break;
				}
				if (c.isVisible()) {
					index++;
				}
			}
			return index;
		}

		public ScriptDefinition getScriptDefinition() {
			return def;
		}

		private final class InsetBorder extends AbstractBorder {
			private static final long serialVersionUID = 1L;

			public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
				g.setColor(Color.WHITE);
				g.drawLine(x, y, x, height);
				g.drawLine(x, y, width, y);
				g.setColor(Color.LIGHT_GRAY);
				g.drawLine(x + width - 1, y, x + width - 1, y + height);
				g.drawLine(x, y + height - 1, x + width, y + height - 1);
			}
		}
	}
}
