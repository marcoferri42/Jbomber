package application;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import controllers.MenuController;

public class MainMenu {
	private Scene scene;

	public MainMenu() throws IOException {
		// loading MENU fxml and css
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/view/MainMenu.fxml"));
		Parent root = fxmlLoader.load();
		MenuController controller = fxmlLoader.getController();

		// set scene
		this.scene = new Scene(root);

		// key handling
		scene.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.UP) {
				controller.up();
			}
			if (event.getCode() == KeyCode.DOWN) {
				controller.down();
			}
			if (event.getCode() == KeyCode.ENTER) {
				try {
					// prende stage da scene DOPO essere stato show() in MAIN
					Stage currentStage = (Stage) this.scene.getWindow();

					currentStage.setScene(controller.select());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

	public Scene getScene() {
		return this.scene;
	}
}