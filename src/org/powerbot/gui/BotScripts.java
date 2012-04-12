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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.bot.Bot;
import org.powerbot.gui.component.BotLocale;
import org.powerbot.gui.component.BotToolBar;
import org.powerbot.service.GameAccounts;
import org.powerbot.service.GameAccounts.Account;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.IniParser;
import org.powerbot.util.io.Resources;
import org.powerbot.util.io.SecureStore;

/**
 * @author Paris
 */
public final class BotScripts extends JDialog implements ActionListener, WindowListener {
	private static final long serialVersionUID = 1L;
	private final BotToolBar parent;
	private final static String FAVOURITES_FILENAME = "script-favourites.txt";
	private final List<String> favourites;
	private final JScrollPane scroll;
	private final JPanel table;
	private final JToggleButton locals, star, paid;
	private final JButton username, refresh;
	private final JTextField search;

	public BotScripts(final BotToolBar parent) {
		super(parent.parent, BotLocale.SCRIPTS, true);
		setIconImage(Resources.getImage(Resources.Paths.SCRIPT));
		this.parent = parent;

		favourites = new ArrayList<String>();
		InputStream favouritesSource = null;
		try {
			favouritesSource = SecureStore.getInstance().read(FAVOURITES_FILENAME);
		} catch (final IOException ignored) {
		} catch (final GeneralSecurityException ignored) {
		}
		if (favouritesSource != null) {
			for (String entry : IOHelper.readString(favouritesSource).split("\n")) {
				entry = entry.trim();
				if (!entry.isEmpty()) {
					favourites.add(entry.toLowerCase());
				}
			}
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

		refresh = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.ARROW_REFRESH)));
		refresh.setVisible(Configuration.DEVMODE);
		refresh.setToolTipText(BotLocale.REFRESH);
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				refresh();
			}
		});
		refresh.setFocusable(false);
		toolbar.add(refresh);
		locals = new JToggleButton(new ImageIcon(Resources.getImage(Resources.Paths.SCRIPT_EDIT)));
		locals.setToolTipText(BotLocale.LOCALONLY);
		locals.addActionListener(this);
		locals.setFocusable(false);
		toolbar.add(locals);
		star = new JToggleButton(new ImageIcon(Resources.getImage(Resources.Paths.STAR)));
		star.setToolTipText(BotLocale.FAVSONLY);
		star.addActionListener(this);
		star.setFocusable(false);
		toolbar.add(star);
		toolbar.add(Box.createHorizontalStrut(d));
		paid = new JToggleButton(new ImageIcon(Resources.getImage(Resources.Paths.MONEY_DOLLAR)));
		paid.setToolTipText(BotLocale.PAIDONLY);
		paid.addActionListener(this);
		paid.setFocusable(false);
		toolbar.add(paid);
		toolbar.add(Box.createHorizontalStrut(d));

		username = new JButton(BotLocale.NOACCOUNT);
		username.addActionListener(this);
		username.setFocusable(false);
		username.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.KEY)));
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
		search.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.LIGHT_GRAY, d, true), BorderFactory.createEmptyBorder(0, d + d, 0, d + d)));
		panelRight.add(search);
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

		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(scroll);
		add(panel);

		addComponentListener(new ComponentAdapter() {
			public void componentResized(final ComponentEvent e) {
				adjustViewport();
			}
		});
		addWindowListener(this);
		pack();
		setMinimumSize(getSize());
		//setResizable(false);
		setLocationRelativeTo(getParent());

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				refresh();
			}
		});

		setVisible(true);
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
	}

	public void refresh() {
		refresh.setEnabled(false);
		table.removeAll();
		final JLabel status = new JLabel("Loading...");
		status.setFont(status.getFont().deriveFont(status.getFont().getSize2D() * 1.75f));
		status.setForeground(Color.GRAY);
		status.setBorder(new EmptyBorder(15, 15, 15, 15));
		table.add(status);
		table.validate();
		table.repaint();
		new Thread(new Runnable() {
			public void run() {
				final List<ScriptDefinition> scripts;
				try {
					scripts = loadScripts();
				} catch (final IOException ignored) {
					status.setText("Could not load scripts, please try again later");
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							table.validate();
							table.repaint();
						}
					});
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
						adjustViewport();
						refresh.setEnabled(true);
					}
				});
			}
		}).start();
	}

	public List<ScriptDefinition> loadScripts() throws IOException {
		final URL src = new URL(Resources.getServerLinks().get("scripts"));
		final Map<String, Map<String, String>> manifests = IniParser.deserialise(HttpClient.openStream(src));
		final List<ScriptDefinition> list = new ArrayList<ScriptDefinition>(manifests.size());
		for (final Entry<String, Map<String, String>> entry : manifests.entrySet()) {
			final ScriptDefinition def = ScriptDefinition.fromMap(entry.getValue());
			if (def != null) {
				def.source = new URL(src, entry.getKey());
				if (entry.getValue().containsKey("className")) {
					def.className = entry.getValue().get("className");
				}
				list.add(def);
			}
		}
		if (Configuration.DEVMODE) {
			final List<File> paths = new ArrayList<File>(2);
			paths.add(new File("bin"));
			paths.add(new File("out"));
			final String key = "scripts";
			if (Resources.getSettings() != null && Resources.getSettings().containsKey(key) && !Resources.getSettings().get(key).isEmpty()) {
				for (final String path : Resources.getSettings().get(key).split(String.format("\\Q%s\\E", File.pathSeparator))) {
					paths.add(new File(path));
				}
			}
			for (final File path : paths) {
				if (path.isDirectory()) {
					loadLocalScripts(list, path, null);
				}
			}
		}
		return list;
	}

	public void loadLocalScripts(final List<ScriptDefinition> list, final File parent, final File dir) {
		for (final File file : (dir == null ? parent : dir).listFiles()) {
			if (file.isDirectory()) {
				loadLocalScripts(list, parent, file);
			} else if (file.isFile()) {
				final String name = file.getName();
				try {
					if (name.endsWith(".class") && name.indexOf('$') == -1) {
						final URL src = parent.getCanonicalFile().toURI().toURL();
						final ClassLoader cl = new URLClassLoader(new URL[]{src});
						String className = file.getCanonicalPath().substring(parent.getCanonicalPath().length() + 1);
						className = className.substring(0, className.lastIndexOf('.'));
						className = className.replace(File.separatorChar, '.');
						final Class<?> clazz = cl.loadClass(className);
						if (ActiveScript.class.isAssignableFrom(clazz)) {
							final Class<? extends ActiveScript> script = clazz.asSubclass(ActiveScript.class);
							if (script.isAnnotationPresent(Manifest.class)) {
								final Manifest m = script.getAnnotation(Manifest.class);
								final ScriptDefinition def = new ScriptDefinition(m);
								def.source = src;
								def.className = className;
								def.local = true;
								list.add(def);
							}
						}
					} else if (file.getName().endsWith(".jar")) {
						// TODO: load local scripts from a jar
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
			try {
				GameAccounts.getInstance().load();
			} catch (IOException ignored) {
			} catch (GeneralSecurityException ignored) {
			}
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
		for (final Component c : table.getComponents()) {
			final ScriptDefinition d = ((ScriptCell) c).getScriptDefinition();
			boolean v = true;
			if (star.isSelected() && !favourites.contains(d.toString())) {
				v = false;
			}
			if (paid.isSelected() && !d.isPremium()) {
				v = false;
			}
			if (!search.getText().isEmpty() && !search.getText().equals(BotLocale.SEARCH) && !d.matches(search.getText())) {
				v = false;
			}
			if (locals.isSelected() && !d.local) {
				v = false;
			}
			c.setVisible(v);
		}
		adjustViewport();
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

			final JLabel skill = new JLabel(new ImageIcon(getSkillImage(26)));
			skill.setBounds(1, (getPreferredCellSize().height - skill.getPreferredSize().height) / 2, skill.getPreferredSize().width, skill.getPreferredSize().height);
			add(skill);

			final JPanel panelInfo = new JPanel(new GridLayout(0, 1));
			panelInfo.setBackground(null);
			final int dx = skill.getLocation().x + skill.getPreferredSize().width + 8;
			panelInfo.setBounds(dx, skill.getLocation().y, getPreferredCellSize().width - dx - 1, skill.getPreferredSize().height);
			add(panelInfo);

			final JLabel name = new JLabel(def.getName());
			name.setToolTipText(String.format("v%s by %s", def.getVersion(), def.getAuthors()));
			name.setFont(name.getFont().deriveFont(Font.BOLD));
			panelInfo.add(name, BorderLayout.NORTH);

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

			if (def.getWebsite() != null && !def.getWebsite().isEmpty()) {
				final JLabel link = new JLabel(new ImageIcon(Resources.getImage(Resources.Paths.WORLD_LINK)));
				link.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(final MouseEvent arg0) {
						BotChrome.openURL(def.getWebsite());
					}
				});
				link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				panelIconsLeft.add(link, BorderLayout.WEST);
			}

			final boolean isFav = favourites.contains(def.toString());
			final JLabel fav = new JLabel(new ImageIcon(Resources.getImage(isFav ? Resources.Paths.STAR : Resources.Paths.STAR_GRAY)));
			fav.setToolTipText(isFav ? BotLocale.REMOVEFROMFAVS : BotLocale.ADDTOFAVS);
			fav.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					final JLabel src = (JLabel) e.getSource();
					final String entry = def.toString();
					final String img, tip;
					if (favourites.contains(def.toString())) {
						favourites.remove(entry);
						img = Resources.Paths.STAR_GRAY;
						tip = BotLocale.ADDTOFAVS;
					} else {
						favourites.add(entry);
						img = Resources.Paths.STAR;
						tip = BotLocale.REMOVEFROMFAVS;
					}
					src.setIcon(new ImageIcon(Resources.getImage(img)));
					src.setToolTipText(tip);
				}
			});
			fav.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			panelIconsLeft.add(fav);
			final JLabel authors = new JLabel(String.format(BotLocale.BY, def.getAuthors()));
			authors.setBorder(new EmptyBorder(3, 0, 0, 0));
			authors.setForeground(Color.GRAY);
			authors.setFont(desc.getFont());
			panelIconsLeft.add(authors);

			final JButton act = new JButton("Play");
			act.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					setVisible(false);
					dispose();
					String name;
					if (def.className != null && !def.className.isEmpty()) {
						name = def.className;
					} else {
						name = def.source.getFile();
						name = name.substring(name.lastIndexOf('/') + 1);
						name = name.substring(0, name.lastIndexOf('.'));
					}
					final ClassLoader cl = new URLClassLoader(new URL[]{def.source});
					final ActiveScript script;
					try {
						script = cl.loadClass(name).asSubclass(ActiveScript.class).newInstance();
					} catch (final Exception ignored) {
						return;
					}
					final Bot bot = Bot.bots.get(BotScripts.this.parent.getActiveTab());
					bot.setAccount(null);
					for (final Account a : GameAccounts.getInstance()) {
						if (username.getText().equalsIgnoreCase(a.toString())) {
							bot.setAccount(a);
							break;
						}
					}
					script.log.info("Starting");//debug to show script has loaded
					bot.startScript(script);
					BotScripts.this.parent.updateScriptControls();
				}
			});
			act.setFont(act.getFont().deriveFont(Font.BOLD, act.getFont().getSize2D() - 1f));
			act.setBackground(null);
			act.setFocusable(false);
			panelIconsRight.add(act);
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

		private Image getSkillImage(final int index) {
			final Image src = Resources.getImage(Resources.Paths.SKILLS);
			final int d = 4;
			final BufferedImage img = new BufferedImage(103, 94, BufferedImage.TYPE_INT_ARGB);
			final Graphics2D g = img.createGraphics();
			final int y = img.getHeight() * index + d;
			g.drawImage(src, 0, 0, img.getWidth(), img.getHeight(), 0, y, img.getWidth(), y + img.getHeight(), null);
			g.dispose();
			final int h = getPreferredCellSize().height - d * 2;
			return img.getScaledInstance((int) ((double) img.getWidth() / img.getHeight() * h), h, Image.SCALE_SMOOTH);
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

	public void windowActivated(final WindowEvent e) {
	}

	public void windowClosed(final WindowEvent e) {
	}

	public void windowClosing(final WindowEvent e) {
		final StringBuilder sb = new StringBuilder();
		for (final String entry : favourites) {
			sb.append(entry);
			sb.append('\n');
		}
		try {
			SecureStore.getInstance().write(FAVOURITES_FILENAME, new ByteArrayInputStream(StringUtil.getBytesUtf8(sb.toString())));
		} catch (final IOException ignored) {
		} catch (final GeneralSecurityException ignored) {
		}
	}

	public void windowDeactivated(final WindowEvent e) {
	}

	public void windowDeiconified(final WindowEvent e) {
	}

	public void windowIconified(final WindowEvent e) {
	}

	public void windowOpened(final WindowEvent e) {
	}
}
