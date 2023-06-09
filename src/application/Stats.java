package application;

import java.io.IOException;

import controllers.StatsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

/**
 * Screen Stats.
 */
public class Stats {
	private Scene scene;

	/**
	 * Creates new Screen for statistics.
	 * Loads from fxml, 
	 * loads gamstate from file,
	 * starts key handler for scene.
	 * @throws IOException
	 */
	public Stats() throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/view/Stats.fxml"));
		Parent root = fxmlLoader.load();
		StatsController controller = fxmlLoader.getController();

		this.scene = new Scene(root);
		JBomberMan.stage.setScene(scene);

		controller.loadData();

		// key handling
		AudioManager audio = new AudioManager();
		scene.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				audio.playSelect();
				try {
					new MainMenu();

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

	}

}
