package org.powerbot.gui.component;

import java.util.Calendar;

import org.powerbot.util.Configuration;

/**
 * @author Paris
 */
public final class BotLocale {
	public static final String NEWTAB = "New";
	public static final String CLOSETAB = "Close";
	public static final String PLAYSCRIPT = "Play";
	public static final String RESUMESCRIPT = "Resume";
	public static final String PAUSESCRIPT = "Pause";
	public static final String STOPSCRIPT = "Stop";
	public static final String FEEDBACK = "Feedback";
	public static final String INPUT = "Input";
	public static final String ACCOUNTS = "Accounts";
	public static final String SIGNIN = "Sign in";
	public static final String SIGNOUT = "Sign out";
	public static final String VIEW = "View";
	public static final String WIDGETEXPLORER = "Widgets";
	public static final String SETTINGEXPLORER = "Settings";
	public static final String ABOUT = "About";
	public static final String EXIT = "Exit";

	public static final String COPY = "Copy";
	public static final String SAVEAS = "Save As...";
	public static final String CLEAR = "Clear";

	public static final String USERNAME = "Username";
	public static final String USERNAME_OR_EMAIL = USERNAME + " or email";
	public static final String PASSWORD = "Password";
	public static final String FORGOTPASS = "I've forgotten my password";
	public static final String PIN = "PIN";
	public static final String MEMBER = "Member";
	public static final String REWARD = "Reward";

	public static final String REGISTER = "Register";
	public static final String ERROR = "Error";
	public static final String INVALIDCREDENTIALS = "Invalid username or password.";
	public static final String SCRIPTS = "Scripts";

	public static final String LOADINGTAB = "Loading a new bot";
	public static final String NEEDVIP = "VIP Access";
	public static final String NEEDVIPMULTITAB = "Only VIPs can open more tabs.";
	public static final String NEEDVIPVPS = "Only VIPs can run the bot on a VPS/Server.";
	public static final String NEEDSIGNIN = "Member Access";
	public static final String NEEDSIGNINMULTITAB = "Please sign in to open more tabs.";
	public static final String CANTOPENTAB = "Unable to open a new tab.";
	public static final String LOGPANE = "Show or hide log pane";
	public static final String WELCOME_SIGNEDIN = "Hi %s, add a tab to start playing";
	public static final String WELCOME_NOTSIGNEDIN = "Please sign in then add a tab to start playing";

	public static final String SEARCH = "Search";
	public static final String REFRESH = "Refresh";
	public static final String BROWSE = "Browse";
	public static final String BROWSETIP = "Browse the full list of scripts";
	public static final String NOACCOUNT = "No account";
	public static final String LOCALONLY = "Show only local scripts";
	public static final String SCRIPTRUNNING = "You are already running this script on another bot.";
	public static final String BY = "By %s";

	public static final String ALLOW = "Allow";
	public static final String KEYBOARD = "Keyboard";
	public static final String MOUSE = "Mouse";
	public static final String BLOCK = "Block";

	public static final String VISITSITE = "Visit Site";
	public static final String OK = "OK";
	public static final String ACCEPT = "Accept";
	public static final String DECLINE = "Decline";

	public static final String WEBSITE = Configuration.URLs.DOMAIN;
	public static final String LICENSE = "License";
	public static final String LICENSETCS = "License Terms and Conditions";
	public static final String LICENSEMSG = "By using this software you agree to be bound by the terms of the license agreement.";
	public static final String COPYRIGHT = "Copyright \u00a9 2011 - " + Calendar.getInstance().get(Calendar.YEAR) + " J.P. Holdings Int'l Ltd and its licensors.";
}
