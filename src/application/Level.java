package application;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import controllers.LevelController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

public class Level {
	private Scene scene;
	private List<Enemy> enemies = new ArrayList<>();
	private Timeline enemyMovementTimeline, enemyBombsTimeline;

	public Level() throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/view/Level.fxml"));
		Parent root = fxmlLoader.load();
		scene = new Scene(root);

		LevelController controller = fxmlLoader.getController();
		Player player = new Player(controller.getMap(), this);

		controller.populateSpace();
		controller.renderBorder();
		controller.renderWalls();
		controller.setPlayer(player);
		controller.renderPlayer(player);

		generateEnemies(controller);

		startKeyHandler(scene, controller);
	}

	private void startKeyHandler(Scene scene, LevelController controller) {
		// Key Handler
		scene.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
			}
			if (event.getCode() == KeyCode.SPACE) {
				controller.placeBomb("player", 0, 0);
			}
			if (event.getCode() == KeyCode.W) {
				controller.movePlayer(0, -1);
			}
			if (event.getCode() == KeyCode.A) {
				controller.movePlayer(-1, 0);
			}
			if (event.getCode() == KeyCode.S) {
				controller.movePlayer(0, 1);
			}
			if (event.getCode() == KeyCode.D) {
				controller.movePlayer(1, 0);
			}
		});
	}

	private void generateEnemies(LevelController controller) {
		Enemy bomber = new Enemy(controller.getMap(), controller, "bomber");
		controller.renderEnemies(bomber);

		Enemy walker = new Enemy(controller.getMap(), controller, "walker");
		controller.renderEnemies(walker);

		enemies.add(bomber);
		enemies.add(walker);
		
		controller.setEnemies(enemies);

		// Create a Timeline to move the enemies randomly
		this.enemyMovementTimeline = new Timeline(new KeyFrame(Duration.millis(800), event -> {
			for (Enemy e : enemies) {
				e.moveEnemy();
			}
		}));
		this.enemyMovementTimeline.setCycleCount(Timeline.INDEFINITE);
		this.enemyMovementTimeline.play();

		// Create a Timeline to place bombs
		this.enemyBombsTimeline = new Timeline(new KeyFrame(Duration.millis(3500), event -> {
			for (Enemy e : enemies) {
				if (e.enemyType == "bomber") {
					e.placeBomb();
					e.moveEnemy();
				}
			}
		}));

		this.enemyBombsTimeline.setCycleCount(Timeline.INDEFINITE);
		this.enemyBombsTimeline.play();
		

	}

	public void playerDied() {
		try {
			GameOver gameOverScreen = new GameOver();
			Stage currentStage = (Stage) scene.getWindow();
			currentStage.setScene(gameOverScreen.getScene());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.enemyBombsTimeline.stop();
		this.enemyMovementTimeline.stop();
	}

	public Scene getScene() {
		return scene;
	}



}
