package controller;

import java.io.InputStream;

import javafx.animation.PauseTransition;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import model.*;
import view.*;

public class PlayerController {

	private IntroController introController;
	private GameController gameController;
	private GridPane grid;

	private int player1X;
	private int player1Y;
	private int player2X;
	private int player2Y;

	private String direction;

	private Player player;
	private Player player2;
	private Userclass playerClass;
	private Userclass playerClass2;

	private ImageView playerImage1View;
	private ImageView playerImage2View;

	private StackPane root;
	private Group gameLayer;
	

	public PlayerController(StackPane root, Group gameLayer, GridPane grid, GameController gameController) {
		this.root = root;
		this.gameController = gameController;
		this.gameLayer = gameLayer;
		this.grid = grid;
		init(root, gameLayer, grid);
		direction = "Right";
	}

	// on initialization,
	private void init(StackPane root, Group gameLayer, GridPane grid) {

		player = new Player("Bob", this, gameController, 1);
		// player2 = new Player(introController.getPlayer1(), this, gameController, 2);

		// playerClass = player.getUserClass(introController.getUserClass());
		playerClass = player.getUserClass("Runner");

		// playerClass2 = player.getUserClass(introController.getUserClass()); //change
		// this to get user class 2

		// change cooldowns of class based on difficulty
		String diffName = "Easy"; // set from intro contoller
		Difficulty chosenDiff;
		chosenDiff = Difficulty.valueOf(diffName.toUpperCase());
		playerClass.applyDifficulty(chosenDiff);

		// set the player img on the board

		int centerX = (int) Math.floor(gameController.getMaze().getSize() / 2.0);
		int centerY = (int) Math.floor(gameController.getMaze().getSize() / 2.0);
		placePlayer(grid, playerClass, false, centerX, centerY, player);
		// if(introController.isDuoActive() && introController.isHuman())
		// placePlayer(grid, playerClass2);

		if (!playerClass.getUserClass().equals("Visionary") || !playerClass.getUserClass().equals("Psychic")) {
			// playerClass.getDebuff().activateDebuff();
		} else if (!playerClass2.getUserClass().equals("Visionary") || !playerClass2.getUserClass().equals("Psychic")) {
			// playerClass2.getDebuff().activateDebuff();
		}
	}

	private void placePlayer(GridPane grid, Userclass playerClass, boolean flag, int centerX, int centerY,
			Player player) {

		int visibleTiles = gameController.getRadius() * 2 + 1;
		int cellSize = (int) Math.min(1420.0 / visibleTiles, 850.0 / visibleTiles);

		InputStream stream = getClass().getResourceAsStream(playerClass.getClassPathImg());
		Image image = new Image(stream);
		ImageView imageView = new ImageView(image);

		imageView.setPreserveRatio(false);
		imageView.setFitWidth(cellSize);
		imageView.setFitHeight(cellSize);

		if (playerClass.getAbility().isActive() && playerClass.getUserClass().equals("Psychic")) {
			imageView.setOpacity(0.6);
		}

		if (!flag) {
			setPlayer1X(centerX);
			setPlayer1Y(centerY);
			setPlayerImage1View(imageView);
		} else {
			setPlayer2X(centerX);
			setPlayer2Y(centerY);
			setPlayerImage2View(imageView);
		}

		int startX = Math.max(0, centerX - gameController.getRadius());
		int startY = Math.max(0, centerY - gameController.getRadius());

		grid.setFocusTraversable(true);
		grid.setOnKeyPressed(event -> onKeyPressed(event, imageView, playerClass, grid, player));

		grid.add(imageView, centerY - startY, centerX - startX);
	}

	private void onKeyPressed(KeyEvent ev, ImageView playerImg, Userclass playerClass, GridPane grid, Player player) {
		if (player.getPlayerID() == 1) {
			switch (ev.getCode()) {
			case W:
				movePlayer("Up", playerImg, grid, playerClass, player);
				break;
			case S:
				movePlayer("Down", playerImg, grid, playerClass, player);
				break;
			case A:
				movePlayer("Left", playerImg, grid, playerClass, player);
				break;
			case D:
				movePlayer("Right", playerImg, grid, playerClass, player);
				break;
			case E:
				// trigger to use ability
				player.useAbility(playerClass, playerImg);
			case P:
				if (playerClass.getUserClass().equals("Visionary") || playerClass.getUserClass().equals("Psychic")) {
					playerClass.getDebuff().activateDebuff();

				}
				break;
			default:
				break;
			}
		} else {

			switch (ev.getCode()) {
			case UP:
				movePlayer("Up", playerImg, grid, playerClass, player);
				break;
			case DOWN:
				movePlayer("Down", playerImg, grid, playerClass, player);
				break;
			case LEFT:
				movePlayer("Left", playerImg, grid, playerClass, player);
				break;
			case RIGHT:
				movePlayer("Right", playerImg, grid, playerClass, player);
				break;
			case E:
				// trigger to use ability
				player.useAbility(playerClass, playerImg);
			case P:
				if (playerClass.getUserClass().equals("Visionary") || playerClass.getUserClass().equals("Psychic")) {
					playerClass.getDebuff().activateDebuff();

				}
				break;
			default:
				break;

			}
		}
	}

	private void movePlayer(String direction, ImageView playerImg, GridPane grid, Userclass playerClass,
			Player player) {
		// move player coord img based on direction
		int newX;
		int newY;
		boolean psyActive = playerClass.getUserClass().equals("Psychic") && playerClass.getAbility().isActive();

		if (player.getPlayerID() == 1) {
			newX = getPlayer1X();
			newY = getPlayer1Y();
		} else {
			newX = getPlayer2X();
			newY = getPlayer2Y();
		}

		System.out.println("Direction: " + direction);
		System.out.println(newX + ":" + newY);

		// change this interaction for when the runner active E simply moves them twice
		switch (direction) {
		case "Up":
			newX -= player.getSpeed();
			break;
		case "Down":
			newX += player.getSpeed();
			break;
		case "Left":
			newY -= player.getSpeed();
			break;
		case "Right":
			newY += player.getSpeed();
			break;
		}

		// check if cell is walkable or if ability is active to pass through a wall
		if (!gameController.getMaze().isWalkable(newX, newY, gameController.getMaze().getGrid()) && !psyActive) {
			System.out.println(playerClass.getAbility().isActive());
			System.out.println(newX + ":" + newY);
			System.out.println("ABORTED!");
			// if not walkable, take no action and play noise to indicate
			// play noise
			return;
		}
		else if (player.isLockout())
			return;

		setDirection(direction);

		// check if on an exit,
		if (gameController.getMaze().isExit(newX, newY, gameController.getMaze().getGrid())
				&& gameController.getCounter() <= 1) {
			// new exit screen

		} else if (gameController.getMaze().isExit(newX, newY, gameController.getMaze().getGrid())
				&& gameController.getCounter() > 1) {
			int ctr = gameController.getCounter() - 1;
			// restart entire game, delete previous screen
			// gameController = new GameController(root, gameLayer, grid);
			gameController.setCounter(ctr);
		}

		if (player.getPlayerID() == 1) {
			setPlayer1X(newX);
			setPlayer1Y(newY);
		} else {
			setPlayer2X(newX);
			setPlayer2Y(newY);
		}

		gameController.drawMaze(grid, gameController.getMaze().getSize(), gameController.getMaze().getGrid(),
				getGameLayer(), newX, newY);
		placePlayer(grid, playerClass, false, newX, newY, player);

		if ((playerClass.getUserClass().equals("Runner") && playerClass.getAbility().isActive()
				&& !player.isRunnerMove())) {

			player.setRunnerMove(true);

			PauseTransition delay = new PauseTransition(Duration.seconds(0.1));
			delay.setOnFinished(event -> {
				movePlayer(direction, playerImg, grid, playerClass, player);
				player.setRunnerMove(false);
			});
			delay.play();

			return;
		}

	}

	public Userclass getPlayerClass() {
		return playerClass;
	}

	public void setPlayerClass(Userclass playerClass) {
		this.playerClass = playerClass;
	}

	public Userclass getPlayerClass2() {
		return playerClass2;
	}

	public void setPlayerClass2(Userclass playerClass2) {
		this.playerClass2 = playerClass2;
	}

	public ImageView getPlayerImage1View() {
		return playerImage1View;
	}

	public void setPlayerImage1View(ImageView playerImage1View) {
		this.playerImage1View = playerImage1View;
	}

	public ImageView getPlayerImage2View() {
		return playerImage2View;
	}

	public void setPlayerImage2View(ImageView playerImage2View) {
		this.playerImage2View = playerImage2View;
	}

	public int getPlayer1X() {
		return player1X;
	}

	public void setPlayer1X(int player1x) {
		player1X = player1x;
	}

	public int getPlayer1Y() {
		return player1Y;
	}

	public void setPlayer1Y(int player1y) {
		player1Y = player1y;
	}

	public int getPlayer2X() {
		return player2X;
	}

	public void setPlayer2X(int player2x) {
		player2X = player2x;
	}

	public int getPlayer2Y() {
		return player2Y;
	}

	public void setPlayer2Y(int player2y) {
		player2Y = player2y;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	public StackPane getRoot() {
		return root;
	}

	public void setRoot(StackPane root) {
		this.root = root;
	}

	public Group getGameLayer() {
		return gameLayer;
	}

	public void setGameLayer(Group gameLayer) {
		this.gameLayer = gameLayer;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public GridPane getGrid() {
		return grid;
	}

	public void setGrid(GridPane grid) {
		this.grid = grid;
	}

}
