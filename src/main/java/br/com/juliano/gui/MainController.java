package br.com.juliano.gui;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controller of Main JavaFX Application.
 * 
 * @author Juliano R. Américo
 *
 */
public class MainController {

	@FXML
	private TextField txtFieldDir;

	@FXML
	private Button btnStart;

	@FXML
	private Button btnStop;

	@FXML
	private Button btnClear;

	@FXML
	private TextArea txtAreaMonitor;

	@FXML
	private Label lblStatus;

	@FXML
	private Menu menuFile;

	@FXML
	private Menu menuLanguage;

	@FXML
	private MenuItem menuItemPortuguese;

	@FXML
	private MenuItem menuItemEnglish;

	@FXML
	private MenuItem menuItemClose;

	@FXML
	private Label lblDirectory;

	/**
	 * Signal to start or stop directory monitoring.
	 */
	private BooleanProperty monitoring = new SimpleBooleanProperty();

	/**
	 * Thread to monitor directory.
	 */
	private MonitorService monitorService = new MonitorService();

	/**
	 * Service to execute task.
	 */
	private ExecutorService service;

	/**
	 * Task to load thread of directory monitoring.
	 */
	private FutureTask<Void> task;

	/**
	 * Map of status messages to user.
	 */
	private Map<String, String> messageMap = new HashMap<>();

	@FXML
	private void initialize() {
		// Map status messages
		messageMap.put("START", "Monitoring...");
		messageMap.put("STOP", "Stopped monitoring!");
		messageMap.put("CLEAR", "Cleared");
		messageMap.put("ERROR", "Path is not valid directory!");

		// Disable some user interface functions while monitoring or not.
		btnStart.disableProperty().bind(monitoring);
		btnStop.disableProperty().bind(monitoring.not());
		btnClear.disableProperty().bind(monitoring);
		txtFieldDir.disableProperty().bind(monitoring);

		// TextArea is read-only. The events is printed in TextArea.
		txtAreaMonitor.setEditable(false);
		txtAreaMonitor.textProperty().bindBidirectional(monitorService.logEventProperty());

		monitorService.pathStringProperty().bind(txtFieldDir.textProperty());
		monitorService.startMonitorProperty().bind(monitoring);
	}

	/**
	 * Intercepts action from buttons and set status messages to user.
	 * 
	 * @param event source.
	 * @throws InterruptedException
	 */
	@FXML
	private void onController(ActionEvent event) throws InterruptedException {
		Button btnPressed = (Button) event.getSource();

		// Gets button label and test it to execute an action.
		String btnText = btnPressed.getText().toUpperCase();
		System.out.println(btnText);
		if (btnText.equals("START") || btnText.equals("INICIAR")) {
			lblStatus.setText(messageMap.get("START"));
			onStartMonitor();
		} else if (btnText.equals("STOP") || btnText.equals("PARAR")) {
			onStopMonitor();
			lblStatus.setText(messageMap.get("STOP"));
		} else if (btnText.equals("CLEAR") || btnText.equals("LIMPAR")) {
			onClear();
			lblStatus.setText(messageMap.get("CLEAR"));
		}
	}

	/**
	 * Validate directory inserted by user and if validate is true then start
	 * monitoring thread in a directory specified.
	 */
	@FXML
	private void onStartMonitor() {
		if (validatePath()) {
			monitoring.set(true);

			onClear();

			task = new FutureTask<>(monitorService);
			service = Executors.newSingleThreadExecutor();
			service.execute(task);
		} else {
			lblStatus.setText(messageMap.get("ERROR"));
		}
	}

	/**
	 * Stops monitoring thread.
	 * 
	 * @throws InterruptedException
	 */
	@FXML
	private void onStopMonitor() throws InterruptedException {
		monitoring.set(false);

		while (!task.isDone()) {
			System.out.println("Log Console: Waiting...");
			Thread.sleep(1000);
		}
		service.shutdown();
	}

	/**
	 * Close all application and threads is running.
	 */
	@FXML
	private void onClose() {
		monitoring.set(false);

		if (service != null && !service.isTerminated()) {
			service.shutdownNow();
		}

		Platform.exit();
		System.exit(0);
	}

	/**
	 * Clear events TextArea.
	 */
	@FXML
	private void onClear() {
		txtAreaMonitor.clear();
	}

	/**
	 * Validate if path is directory.
	 * 
	 * @return true if is directory, otherwise false.
	 */
	@FXML
	private boolean validatePath() {
		return Files.isDirectory(Paths.get(txtFieldDir.getText()));
	}

	/**
	 * This application has language support for two languages: Portuguese (BR) and
	 * English (US). It is used i18n with ResourceBundle.
	 * 
	 * @param event action from menuItem. There is a menu item to choose a language.
	 */
	@FXML
	private void onChangeLanguage(ActionEvent event) {
		MenuItem menuItem = (MenuItem) event.getSource();

		String menuItemText = menuItem.getText().toUpperCase();

		if (menuItemText.equals("PORTUGUESE")) {
			menuFile.setText(ConfigFile.getMessage("file"));
			menuLanguage.setText(ConfigFile.getMessage("language"));
			menuItemPortuguese.setText(ConfigFile.getMessage("portuguese"));
			menuItemEnglish.setText(ConfigFile.getMessage("english"));
			menuItemClose.setText(ConfigFile.getMessage("close"));
			lblDirectory.setText(ConfigFile.getMessage("directory"));
			btnStart.setText(ConfigFile.getMessage("start"));
			btnStop.setText(ConfigFile.getMessage("stop"));
			btnClear.setText(ConfigFile.getMessage("clear"));
			messageMap.put("START", ConfigFile.getMessage("msgStart"));
			messageMap.put("STOP", ConfigFile.getMessage("msgStop"));
			messageMap.put("CLEAR", ConfigFile.getMessage("msgClear"));
			messageMap.put("ERROR", ConfigFile.getMessage("msgError"));

		} else if (menuItemText.equals("INGLÊS")) {
			ConfigFile.setLocale(new Locale("en", "US"));
			menuFile.setText(ConfigFile.getMessage("file"));
			menuLanguage.setText(ConfigFile.getMessage("language"));
			menuItemPortuguese.setText(ConfigFile.getMessage("portuguese"));
			menuItemEnglish.setText(ConfigFile.getMessage("english"));
			menuItemClose.setText(ConfigFile.getMessage("close"));
			lblDirectory.setText(ConfigFile.getMessage("directory"));
			btnStart.setText(ConfigFile.getMessage("start"));
			btnStop.setText(ConfigFile.getMessage("stop"));
			btnClear.setText(ConfigFile.getMessage("clear"));
			messageMap.put("START", ConfigFile.getMessage("msgStart"));
			messageMap.put("STOP", ConfigFile.getMessage("msgStop"));
			messageMap.put("CLEAR", ConfigFile.getMessage("msgClear"));
			messageMap.put("ERROR", ConfigFile.getMessage("msgError"));
		}

	}
}
