package org.powerbot.gui;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.powerbot.Configuration;
import org.powerbot.gui.component.BotLocale;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.StringUtil;
import org.powerbot.util.Tracker;

/**
 * @author Paris
 */
public class BotSignin extends JDialog {

	public BotSignin(final BotChrome parent) {
		super(parent, BotLocale.SIGNIN, true);

		final int width = 320, height = 480;

		final JFXPanel panel = new JFXPanel();
		panel.setPreferredSize(new Dimension(width, height));
		add(panel);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				final Group group = new Group();
				final Scene scene = new Scene(group);
				panel.setScene(scene);
				final WebView web = new WebView();
				web.setContextMenuEnabled(false);
				group.getChildren().add(web);
				web.setMinSize(width, height);
				web.setMaxSize(width, height);
				final WebEngine engine = web.getEngine();
				engine.setJavaScriptEnabled(true);
				engine.load(Configuration.URLs.SIGNIN_PAGE);

				engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
					@Override
					public void changed(final ObservableValue<? extends Worker.State> observableValue, final Worker.State state, final Worker.State state2) {
						((JSObject) engine.executeScript("window")).setMember("java", new Bridge());
					}
				});


				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						setVisible(true);
					}
				});
			}
		});

		pack();
		setMinimumSize(getSize());
		setResizable(false);
		setLocationRelativeTo(getParent());

		Tracker.getInstance().trackPage("signin/", getTitle());
	}

	public final class Bridge {

		public void signin(final String s) {
			try {
				NetworkAccount.getInstance().login(new ByteArrayInputStream(StringUtil.getBytesUtf8(s)));
			} catch (final IOException ignored) {
				ignored.printStackTrace();
			}
			close();
		}

		public void close() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setVisible(false);
					dispose();
				}
			});
		}
	}
}
