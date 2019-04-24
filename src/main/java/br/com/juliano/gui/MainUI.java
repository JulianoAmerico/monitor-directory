package br.com.juliano.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Main User Interface.
 * @author Juliano R. Américo
 *
 */
public class MainUI extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		Pane root = FXMLLoader.load(getClass().getResource("/MainLayout.fxml"));
		Scene scene = new Scene(root, 400, 300);
		scene.getStylesheets().add("style.css");
		
		// Change window icon
		Image icon = new Image(getClass().getClassLoader().getResourceAsStream("search1_24.png"));
		primaryStage.getIcons().add(icon);
		
		primaryStage.setMinWidth(400);
		primaryStage.setMinHeight(300);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Monitoring directory");
		
		//Terminate all threads are running.
		primaryStage.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});
		
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
