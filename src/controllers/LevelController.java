package controllers;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import application.AudioManager;
import application.Bomb;
import application.Enemy;
import application.Player;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;

public class LevelController {
	@FXML
	Text score;
	@FXML
	Text lives;
	@FXML
	TilePane tilePane;

	private Player player;
	private int nCols = 17, nRows = 16, scorePoints = 0, nWalls = 50, explosionPower = 1, livesScore = 2;
	private HashMap<List<Integer>, ImageView> map = new HashMap<>();
	private Bomb placedBomb;
	private AudioManager audio = new AudioManager();
	private Image grass = new Image("file:/home/a/eclipse-workspace/Jbomber/src/resources/grass.png", 50, 50, false,
			true),
			border = new Image("file:/home/a/eclipse-workspace/Jbomber/src/resources/barrier.png", 50, 50, false, true),
			wall = new Image("file:/home/a/eclipse-workspace/Jbomber/src/resources/wall.png", 50, 50, false, true);
	private List<Enemy> enemies;

	public LevelController() {
		this.audio.playSoundtrack(true);
	}

	public void populateSpace() {
		BackgroundImage myBI = new BackgroundImage(grass, BackgroundRepeat.REPEAT, null, BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT);

		tilePane.setBackground(new Background(myBI));

		for (int x = 0; x < nCols; x++) {
			for (int y = 0; y < nRows; y++) {
				ImageView tile = createTile();

				tilePane.getChildren().add(tile);
				map.put(List.of(x, y), tile);
			}
		}
	}

	public ImageView createTile() {
		ImageView tile = new ImageView();

		tile.setFitHeight(50);
		tile.setFitWidth(50);

		return tile;
	}

	public void renderBorder() {
		for (int y = 0; y < nRows; y++) {
			for (int x = 0; x < nCols; x++) {
				if (x == 0 || x == nCols - 1 || y == nRows - 1) {
					map.get(List.of(x, y)).setImage(border);
				} else {
					if (x % 2 == 0 && y % 2 != 0) {
						map.get(List.of(x, y)).setImage(border);
					}
				}
			}
		}
	}

	public void renderWalls() {
		for (int i = 0; i < nWalls; i++) {
			int x, y;
			do {
				Random rand = new Random();
				x = rand.nextInt(1, nCols - 1);
				y = rand.nextInt(0, nRows - 1);

			} while (map.get(List.of(x, y)).getImage() != null);

			map.get(List.of(x, y)).setImage(wall);

		}
	}

	public void renderPlayer(Player player) {
		Random rand = new Random();
		int x, y;

		do {
			x = rand.nextInt(1, nCols - 1);
			y = rand.nextInt(3, nRows - 1);

		} while ((map.get(List.of(x, y)).getImage() != null) && !(getNearTiles(x, y, 1).contains(null)));

		player.currentX = x;
		player.currentY = y;
		player.spawnPlayer();

		tilePane.getChildren().add(player.getPlayerNode());

		this.lives.setText("" + livesScore);
	}

	public void renderEnemies(Enemy enemies) {
		Random rand = new Random();
		int x, y;

		do {
			x = rand.nextInt(1, nCols - 1);
			y = rand.nextInt(3, nRows - 1);

		} while ((map.get(List.of(x, y)).getImage() != null) && !(getNearTiles(x, y, 1).contains(null)));

		enemies.currentX = x;
		enemies.currentY = y;
		enemies.spawnEnemy();

		tilePane.getChildren().add(enemies.getEnemyNode());
	}

	public void placeBomb() {
		if (this.placedBomb == null || this.placedBomb.isExploded) {
			this.placedBomb = new Bomb(this, this.player.getX(), this.player.getY(), "player", this.explosionPower);
		}
	}

	public List<ImageView> getNearTiles(int x, int y, int radius) {
		int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
		List<ImageView> nearWalls = new LinkedList<>();
		Set<List<Integer>> visited = new HashSet<>();
		List<Integer> pos = List.of(x, y);

		nearWalls.add(map.get(pos));
		visited.add(pos);

		for (int[] dir : directions) {
			pos = List.of(x, y);
			for (int i = 1; i <= radius; i++) {
				pos = Arrays.asList(pos.get(0) + dir[0], pos.get(1) + dir[1]);
				if (!visited.add(pos)) {
					break;
				}
				ImageView wall = map.get(pos);

				if (wall == null) {
					continue;
				}

				if (wall.getImage() != null) {
					if (wall.getImage().equals(border)) {
						break;
					}
				}
				nearWalls.add(wall);
			}
		}

		return nearWalls;
	}

	public void clearLevel() {
		for (int y = 0; y < nRows; y++) {
			for (int x = 0; x < nCols; x++) {
				if (map.get(List.of(x, y)).getImage() != wall) {
					map.get(List.of(x, y)).setImage(null);
				}
			}
		}
		// 306 perche il player è aggiunto per ultimo, ovvero 17*18-1
		tilePane.getChildren().remove(306);
	}

	public HashMap<List<Integer>, ImageView> getMap() {
		return this.map;
	}

	public void setScore(int points) {
		this.scorePoints += points;
		this.score.setText("" + scorePoints);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void movePlayer(int x, int y) {
		if (this.player.isMoveValid(x, y)) {
			this.player.movePlayer(x, y);
		} else {
			System.out.println("--collision--");
		}
	}

	public void checkPlayerDamage(double x, double y) {
		if (this.player.getX() == x && this.player.getY() == y) {
			if (Integer.parseInt(this.lives.getText()) == 1) {
				this.livesScore--;
				this.player.dieEvent();
				this.audio.playSoundtrack(false);
				this.audio.playGameOver();

			} else {
				this.audio.playDamageTaken();
				this.livesScore--;
				this.lives.setText("" + livesScore);
				this.player.damageAnimation();
			}

		}
	}

	public void checkEnemyDamage(double x, double y) {
		List<Enemy> temp = new ArrayList<>();
		
		for(Enemy e : this.enemies) {
			if(e.getX() == x && e.getY() == y) {
				this.audio.playDamageTaken();
				e.deathAnimation();
				temp.add(e);
			}
		}
		
		if(temp.size() > 0) {
			this.enemies.removeAll(temp);
		}
	}

	public TilePane getTilePane() {
		return this.tilePane;
	}

	public void setEnemies(List<Enemy> enemies) {
		this.enemies = enemies;
	}


}