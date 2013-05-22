package org.powerbot.gui;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public class BotControlPanel extends JDialog {
	public final BotChrome parent;
	private final int VIEWPORT_WIDTH = 700, VIEWPORT_HEIGHT = 400;

	public BotControlPanel(final BotChrome parent) {
		super(parent, "Control Panel");
		this.parent = parent;

		final JFXPanel jfxPanel = new JFXPanel();
		add(jfxPanel);
		jfxPanel.setLocation(0, 0);
		jfxPanel.setSize(new Dimension(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));

		getContentPane().setLayout(null);
		getContentPane().setPreferredSize(new Dimension(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
		pack();
		setLocationRelativeTo(getParent());
		setResizable(false);


		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				final Group group = new Group();
				final Scene scene = new Scene(group);
				jfxPanel.setScene(scene);
				final WebView webView = new WebView();
				group.getChildren().add(webView);
				webView.setMinSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
				webView.setContextMenuEnabled(false);

				String html = "";

				try {
					html = IOHelper.readString(Resources.getResourceURL(Resources.Paths.HTML_INDEX));
					String css = IOHelper.readString(Resources.getResourceURL(Resources.Paths.HTML_CSS));
					String js = IOHelper.readString(Resources.getResourceURL(Resources.Paths.HTML_JS));
					html = html.replace("<link href=\"main.css\" rel=\"stylesheet\" type=\"text/css\" />\n", "<style type=\"text/css\">" + css + "</style>");
					html = html.replace("<script src=\"main.js\"></script>\n", "<script type=\"application/javascript\">" + js + "</script>\n");
				} catch (final IOException ignored) {
					ignored.printStackTrace();
				}

				webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
					@Override
					public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State state, Worker.State state2) {
						if (state2 == Worker.State.SUCCEEDED) {
							((JSObject) webView.getEngine().executeScript("window")).setMember("java", new Bridge(BotControlPanel.this));
							webView.getEngine().executeScript("java_onload();");
						}
					}
				});

				webView.getEngine().loadContent(html);

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						setModalityType(ModalityType.DOCUMENT_MODAL);
					}
				});
			}
		});
	}

	public class Bridge {
		private final BotControlPanel control;

		public Bridge(final BotControlPanel control) {
			this.control = control;
		}

		public void loaded() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					control.setVisible(true);
				}
			});
		}

		public void println(final String s) {
			System.out.println(s);
		}
	}
}
