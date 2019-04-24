package br.com.juliano.gui;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Callable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;

/**
 * Monitors a specific directory.
 * 
 * @author Juliano R. Américo
 *
 */
public class MonitorService implements Callable<Void> {

	/**
	 * Append all text events occurred in a specific directory.
	 */
	private TextArea logEvent = new TextArea();

	/**
	 * Receive an action to start monitoring directory.
	 */
	private BooleanProperty startMonitor = new SimpleBooleanProperty();

	/**
	 * Receive a specific directory to monitor.
	 */
	private StringProperty pathString = new SimpleStringProperty();

	public String getLogEvent() {
		return logEvent.getText();
	}

	public void setLogEvent(String logEvent) {
		this.logEvent.appendText(logEvent);
	}

	public Boolean getStartMonitor() {
		return startMonitor.get();
	}

	public void setStartMonitor(Boolean startMonitor) {
		this.startMonitor.set(startMonitor);
	}

	public String getPathString() {
		return pathString.get();
	}

	public void setPathString(String pathString) {
		this.pathString.set(pathString);
	}

	public BooleanProperty startMonitorProperty() {
		return startMonitor;
	}

	public StringProperty logEventProperty() {
		return logEvent.textProperty();
	}

	public StringProperty pathStringProperty() {
		return pathString;
	}

	/**
	 * This method uses WatchService API to monitor a specific directory.
	 */
	@Override
	public Void call() throws Exception {

		try {
			// Directory inserted by user. There is a bind to TextField.
			Path dir = Paths.get(pathString.get());

			// Show directory choose by user.
			logEvent.appendText("Directory='" + dir + "'");
			logEvent.appendText("\n");

			// Create WatchService and kind of events to monitor.
			WatchService watcher = FileSystems.getDefault().newWatchService();
			dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE);

			// Stop loop if user stopped monitoring.
			while (startMonitor.get()) {

				// Return null if not occurred an event.
				WatchKey key = watcher.poll();

				// None event occurred then test loop 'while' and test if user didn't stop
				// monitoring.
				if (key == null) {
					continue;
				}

				for (WatchEvent<?> watchEvents : key.pollEvents()) {
					Kind<?> kind = watchEvents.kind();

					// If several events occurred and the system wasn't able to catch up all of them
					// then OVERFLOW is true. Test loop 'while' event again.
					if (kind == StandardWatchEventKinds.OVERFLOW) {
						continue;
					}

					@SuppressWarnings("unchecked")
					WatchEvent<Path> we = (WatchEvent<Path>) watchEvents;
					Path fileName = we.context();

					// Determinate kind of events and build text event.
					switch (kind.toString()) {
					case "ENTRY_CREATE":
						logEvent.appendText("Event: Created; File: " + fileName);
						logEvent.appendText("\n");
						break;
					case "ENTRY_DELETE":
						logEvent.appendText("Event: Deleted; File: " + fileName);
						logEvent.appendText("\n");
						break;
					case "ENTRY MODIFY":
						logEvent.appendText("Event: Modified; File: " + fileName);
						logEvent.appendText("\n");
						break;
					}

					// Reset key to catch more events, if it is not possible to reset then leave
					// loop.
					if (!key.reset()) {
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
