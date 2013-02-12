package org.powerbot.gui.component;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.powerbot.gui.BotChrome;

/**
 * @author Paris
 */
public final class BotKeyEventDispatcher implements KeyEventDispatcher {
	private final static Map<Long, Action> keyMap;
	public static enum Action { MENU, TAB_ADD, TAB_CLOSE, ACCOUNTS, SIGNIN, ABOUT, SCRIPT_PLAYPAUSE, SCRIPT_STOP };

	static {
		keyMap = new HashMap<Long, Action>();

		keyMap.put((long) KeyEvent.VK_F1, Action.MENU);
		keyMap.put((long) KeyEvent.VK_CONTEXT_MENU, Action.MENU);

		keyMap.put((long) KeyEvent.CTRL_DOWN_MASK << 32 | KeyEvent.VK_N, Action.TAB_ADD);
		keyMap.put((long) KeyEvent.CTRL_DOWN_MASK << 32 | KeyEvent.VK_T, Action.TAB_ADD);
		keyMap.put((long) (KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK) << 32L | KeyEvent.VK_N, Action.TAB_ADD);

		keyMap.put((long) KeyEvent.ALT_DOWN_MASK << 32 | KeyEvent.VK_F4, Action.TAB_CLOSE);
		keyMap.put((long) (KeyEvent.ALT_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK) << 32 | KeyEvent.VK_W, Action.TAB_CLOSE);
		keyMap.put((long) KeyEvent.CTRL_DOWN_MASK << 32 | KeyEvent.VK_W, Action.TAB_CLOSE);
		keyMap.put((long) KeyEvent.CTRL_DOWN_MASK << 32 | KeyEvent.VK_F4, Action.TAB_CLOSE);

		keyMap.put((long) KeyEvent.VK_F7, Action.ACCOUNTS);
		keyMap.put((long) KeyEvent.VK_F6, Action.SIGNIN);
		keyMap.put((long) KeyEvent.VK_F12, Action.ABOUT);
		keyMap.put((long) KeyEvent.VK_F9, Action.SCRIPT_PLAYPAUSE);
		keyMap.put((long) KeyEvent.VK_F10, Action.SCRIPT_STOP);
	}

	public BotKeyEventDispatcher() {
	}

	public static void setAccelerator(final JMenuItem menuItem, final Action action) {
		for (final Entry<Long, Action> entry : keyMap.entrySet()) {
			if (entry.getValue().equals(action)) {
				final long l = entry.getKey();
				menuItem.setAccelerator(KeyStroke.getKeyStroke((int) l, (int) (l >> 32), true));
				return;
			}
		}
	}

	@Override
	public boolean dispatchKeyEvent(final KeyEvent e) {
		if (e.getID() != KeyEvent.KEY_RELEASED) {
			return false;
		}
		final BotChrome chrome = BotChrome.getInstance();
		final Component panel = chrome.panel;
		Point p = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(p, panel);
		if (p.getX() < 0 || p.getY() < 0 || p.getX() > panel.getWidth() || p.getY() > panel.getHeight()) {
			p = new Point(8, 8);
		}
		final long m = e.getModifiersEx() << 32 | e.getKeyCode();
		if (keyMap.containsKey(m)) {
			final Action a = keyMap.get(m);
			switch (a) {
			case MENU: new BotMenu().show(panel, (int) p.getX(), (int) p.getY()); break;
			case TAB_ADD: BotMenu.tabAdd(); break;
			case TAB_CLOSE: BotMenu.tabClose(false); break;
			case ACCOUNTS:
			case SIGNIN:
			case ABOUT:
				BotMenu.showDialog(a);
				break;
			case SCRIPT_PLAYPAUSE: BotMenu.scriptPlayPause(); break;
			case SCRIPT_STOP: BotMenu.scriptStop(); break;
			default: break;
			}
			return true;
		}
		return false;
	}
}
