package controller;

import java.awt.Color;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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

	private CooldownIcon abilityIcon;
	private CooldownIcon passiveIcon;

	private ImageView playerImage1View;
	private ImageView playerImage2View;

	private List<ImageView> stepTrail = new ArrayList<>();
	private int maxTrailLength = 7;
	Random random = new Random();
	private int cellSize;
	private boolean isPaused = false;

	private StackPane root;
	private Group gameLayer;

	public PlayerController(StackPane root, GridPane grid, GameController gameController) {

		this.root = root;
		this.gameController = gameController;
		this.grid = grid;
		init(root, grid);
		direction = "Right";
	}

	// on initialization,
	private void init(Pane root, GridPane grid) {

		player = new Player(gameController.getIntroController().getPlayer1(), this, gameController, 1);
		// player2 = new Player(introController.getPlayer1(), this, gameController, 2);

		// playerClass = player.getUserClass(introController.getUserClass());

		playerClass = new Userclass(gameController.getIntroController().getUserClass(), gameController, this);
		setupAbilityIcons();

		// playerClass2 = player.getUserClass(introController.getUserClass()); //change
		// this to get user class 2

		// change cooldowns of class based on difficulty
		String diffName = gameController.getIntroController().getDifficulty();
		Difficulty chosenDiff;
		chosenDiff = Difficulty.valueOf(diffName.toUpperCase());
		playerClass.applyDifficulty(chosenDiff);

		// set the player img on the board

		int centerX = (int) Math.floor(gameController.getMaze().getSize() / 2.0);
		int centerY = (int) Math.floor(gameController.getMaze().getSize() / 2.0);
		setPlayer1X(centerX);
		setPlayer1Y(centerY);

		placePlayer(grid, playerClass, false, centerX, centerY, player);
		// if(introController.isDuoActive() && introController.isHuman())
		// placePlayer(grid, playerClass2);

	}

	private void setupAbilityIcons() {
		// 1. Create the Icons
		String abilityIconPath = "/images/" + playerClass.getUserClass().toLowerCase() + "_ability.png";
		abilityIcon = new CooldownIcon(abilityIconPath, 80);
		setAbilityIcon(abilityIcon);
		playerClass.setAbilityIcon(abilityIcon);

		if (!playerClass.getUserClass().equals("Runner")) {
			String passiveIconPath = "/images/" + playerClass.getUserClass().toLowerCase() + "_passive.png";
			passiveIcon = new CooldownIcon(passiveIconPath, 80);
			setPassiveIcon(passiveIcon);
			playerClass.setPassiveIcon(passiveIcon);
		}

		// 2. Add them to the ROOT pane
		if (abilityIcon != null) {
			gameController.getHudLayer().getChildren().add(abilityIcon.getView());
		}
		if (passiveIcon != null) {
			gameController.getHudLayer().getChildren().add(passiveIcon.getView());
		}

		// 3. Use Platform.runLater to guarantee positioning happens AFTER the initial
		// layout pass.

	}

	public void placePlayer(GridPane grid, Userclass playerClass, boolean flag, int playerMazeX, int playerMazeY,
			Player player) {

		int visibleTiles = gameController.getRadius() * 2 + 1;
		int cellSize = (int) (820.0 / visibleTiles);
		setCellSize(cellSize);

// Calculate grid position (must match maze drawing logic)
		int gridCol = playerMazeY - (getPlayer1Y() - gameController.getRadius());
		int gridRow = playerMazeX - (getPlayer1X() - gameController.getRadius());

// Debug output
		// centerViewOnPlayer( player, cellSize);

// Create and configure player image
		ImageView imageView = createPlayerImageView(playerClass, player, cellSize);

// Store reference and add to grid
		if (!flag) {
			setPlayerImage1View(imageView);
			setPlayer1X(playerMazeX);
			setPlayer1Y(playerMazeY);
		} else {
			setPlayerImage2View(imageView);
			setPlayer2X(playerMazeX);
			setPlayer2Y(playerMazeY);
		}

		grid.add(imageView, gridCol, gridRow);

		grid.setFocusTraversable(true);
		grid.setOnKeyPressed(event -> onKeyPressed(event, imageView, playerClass, grid, player));
	}

	private void onKeyPressed(KeyEvent ev, ImageView playerImg, Userclass playerClass, GridPane grid, Player player) {

		// If paused or lock out, dont move
		if (isPaused || playerClass.isLockout())
			return;

		// move based on player id, and take action depending on kely
		if (player.getPlayerID() == 1) {
			switch (ev.getCode()) {
			case W:
				player.setDirection("Up");
				movePlayer("Up", playerImg, grid, playerClass, player);
				break;
			case S:
				player.setDirection("Down");
				movePlayer("Down", playerImg, grid, playerClass, player);
				break;
			case A:
				player.setDirection("Left");
				movePlayer("Left", playerImg, grid, playerClass, player);
				break;
			case D:
				player.setDirection("Right");
				movePlayer("Right", playerImg, grid, playerClass, player);
				break;
			case E:
				// trigger to use ability
				playerClass.useAbility(playerClass, playerImg);
				break;
			case P:
				if (!playerClass.getUserClass().equals("Runner")) {
					playerClass.usePassive(playerClass, playerImg);
				}
				break;
			default:
				break;
			}
		} /*
			 * else {
			 * 
			 * switch (ev.getCode()) { case UP: player.setDirection("Up"); movePlayer("Up",
			 * playerImg, grid, playerClass, player); break; case DOWN:
			 * player.setDirection("Down"); movePlayer("Down", playerImg, grid, playerClass,
			 * player); break; case LEFT: player.setDirection("Left"); movePlayer("Left",
			 * playerImg, grid, playerClass, player); break; case RIGHT:
			 * player.setDirection("Right"); movePlayer("Right", playerImg, grid,
			 * playerClass, player); break; case E: // trigger to use ability
			 * playerClass.useAbility(playerClass, playerImg); break; case P: if
			 * (!playerClass.getUserClass().equals("Runner")) {
			 * playerClass.usePassive(playerClass, playerImg);
			 * 
			 * } break; default: break;
			 * 
			 * } }
			 */
	}

	private void movePlayer(String direction, ImageView playerImg, GridPane grid, Userclass playerClass,
			Player player) {

		if (playerClass.isLockout()) {
			return;
		}

		// rate limit moving
		playerClass.setLockout(true);
		PauseTransition delay = new PauseTransition(Duration.millis(90)); // 0 for testing
		delay.setOnFinished(e -> playerClass.setLockout(false));
		delay.play();

		int oldX = (player.getPlayerID() == 1) ? getPlayer1X() : getPlayer2X();
		int oldY = (player.getPlayerID() == 1) ? getPlayer1Y() : getPlayer2Y();
		int newX = oldX;
		int newY = oldY;

		boolean psyActive = playerClass.getUserClass().equals("Psychic") && playerClass.getAbility().isActive();

		// Calculate proposed new position
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

		// If the player exits and their repeat counter is less than or 1, theyre
		// allowed to exit, other repeat the layer
		if (gameController.getMaze().isExit(newX, newY, gameController.getMaze().getGrid())) {
			if (gameController.getCounter() <= 1) {
				showWinScreen();
			} else {
				int ctr = gameController.getCounter() - 1;
				restartGame(ctr);
				return;
			}
		}

		// check if cell is walkable or if ability is active to pass through a wall
		if (!gameController.getMaze().isPath(newX, newY, gameController.getMaze().getGrid()) && !psyActive) {
			// play noise to indicate collision
			AudioClip sound = new AudioClip(getClass().getResource("/sounds/wallhit.mp3").toExternalForm());
			sound.setVolume(200);
			sound.play();
			return;
		}

		// Add class specific steps
		if (playerClass.getUserClass().equals("Runner") || playerClass.getUserClass().equals("Visionary")) {
			addStepToTrail(oldX, oldY, player.getDirection());
		}

		if (player.getPlayerID() == 1) {
			setPlayer1X(newX);
			setPlayer1Y(newY);
		} else {
			setPlayer2X(newX);
			setPlayer2Y(newY);
		}

		// set direction and move player
		setDirection(direction);
		redrawScene(newX, newY);

		// Play 1 of 2 walking noises
		int randomNumber = random.nextInt((2 - 1) + 1) + 1;
		String path;
		if (playerClass.getUserClass().equals("Runner") || playerClass.getUserClass().equals("Nomad")) {
			path = "/sounds/bootstep" + randomNumber + ".mp3";
		} else {
			path = "/sounds/heelstep_" + randomNumber + ".mp3";
		}

		URL soundURL = getClass().getResource(path);
		AudioClip sound = new AudioClip(soundURL.toExternalForm());
		sound.play();

		// move twice if runner ability active
		if (playerClass.getUserClass().equals("Runner") && playerClass.getAbility().isActive()
				&& !playerClass.isRunnerMove()) {

			playerClass.setRunnerMove(true);

			PauseTransition delay2 = new PauseTransition(Duration.seconds(0.10));
			delay2.setOnFinished(event -> {
				movePlayer(direction, playerImg, grid, playerClass, player);
				playerClass.setRunnerMove(false);
			});
			delay2.play();

			return;
		}

	}

	private void showWinScreen() {
		Stage stage = (Stage) gameController.getRoot().getScene().getWindow();
		WinScreen winScreen = new WinScreen();

		// Reduce any opacity before
		gameController.getRoot().setOpacity(0);

		FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.2), gameController.getRoot());
		fadeIn.setToValue(1);
		fadeIn.setOnFinished(e -> {

			MediaPlayer player = gameController.getGameScreen().getPlayer();
			if (player != null) {
				player.stop();
			}

			// Then start fade out
			FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.1), gameController.getRoot());
			fadeOut.setToValue(0);
			fadeOut.setOnFinished(e2 -> {

				try {
					winScreen.start(stage);
				} catch (Exception e1) {

					e1.printStackTrace();
				}

			});

			fadeOut.play();
		});

		fadeIn.play(); // Start the fade in
	}

	private void restartGame(int ctr) {

		// Calculate remaining time and clear
		double remainingA = getAbilityIcon().getRemainingCooldownSeconds();
		double remainingP = getPassiveIcon().getRemainingCooldownSeconds();
		root.getChildren().clear();

		// Reset grid and other elements
		GridPane grid2 = new GridPane();
		grid2.setAlignment(Pos.CENTER);
		grid2.setPrefWidth(980);
		grid2.setPrefHeight(980);
		grid2.setHgap(0);
		grid2.setVgap(0);
		grid2.setPadding(Insets.EMPTY);

		Pane hudLayer = new Pane();

		root.getChildren().addAll(grid2, hudLayer);

		this.grid = grid2;

		gameController = new GameController(root, grid2, gameController.getGameScreen(),
				gameController.getIntroController(), hudLayer);
		setGameController(gameController);
		setupAbilityIcons();
		playerClass.setGameController(gameController);

		// Replace both with new abilites and restart cooldowns if used
		if (getAbilityIcon() != null) {
			StackPane view = getAbilityIcon().getView();

			if (remainingA != 0.0 || remainingA != 0) {
				getAbilityIcon().startCooldown(remainingA);
			}

			view.toFront();
			view.setLayoutX(root.getWidth() - view.getWidth() - 220); // from left
			view.setLayoutY(root.getHeight() - view.getHeight() - 90); // from bottom

		}

		if (getPassiveIcon() != null) {
			StackPane view = getPassiveIcon().getView();

			if (remainingP != 0.0 || remainingP != 0) {
				getPassiveIcon().startCooldown(remainingP);
			}
			view.setLayoutX(root.getWidth() - view.getWidth() - 100); // from right
			view.setLayoutY(root.getHeight() - view.getHeight() - 90); // from bottom
			view.toFront();
		}

		int center = (int) Math.floor(gameController.getMaze().getSize() / 2.0);
		setPlayer1X(center);
		setPlayer1Y(center);

		// Create transition elements
		Rectangle blackOverlay = new Rectangle(root.getWidth(), root.getHeight());
		blackOverlay.setFill(javafx.scene.paint.Color.BLACK);
		blackOverlay.setOpacity(0);
		root.getChildren().add(blackOverlay);
		Text layerText = new Text("Layer " + ctr);

		layerText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/MorrisRoman-Black.ttf"), 55));
		layerText.setOpacity(0);
		layerText.setFill(javafx.scene.paint.Color.WHITE);

		// Center the text
		layerText.setX(root.getWidth() / 2 - layerText.getBoundsInLocal().getWidth() / 2);
		layerText.setY(root.getHeight() / 2);
		root.getChildren().add(layerText);

		transition(grid2, blackOverlay, layerText, center, ctr);
	}

	private void transition(GridPane grid2, Rectangle blackOverlay, Text layerText, int center, int ctr) {

		FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), blackOverlay);
		fadeIn.setToValue(1);
		fadeIn.setOnFinished(e -> {
			// 2. Show text after screen is black
			FadeTransition textFadeIn = new FadeTransition(Duration.seconds(0.1), layerText);
			textFadeIn.setToValue(1);
			textFadeIn.setOnFinished(e2 -> {
				// 3. Keep text visible for 3 seconds
				PauseTransition textDelay = new PauseTransition(Duration.seconds(1));
				textDelay.setOnFinished(e3 -> {
					// 4. Fade out text
					FadeTransition textFadeOut = new FadeTransition(Duration.seconds(0.1), layerText);
					textFadeOut.setToValue(0);
					textFadeOut.setOnFinished(e4 -> {
						// 5. Fade out black overlay
						FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.2), blackOverlay);
						fadeOut.setToValue(0);
						fadeOut.setOnFinished(e5 -> {
							// Clean up and place player
							root.getChildren().removeAll(blackOverlay, layerText);
							placePlayer(grid2, playerClass, false, center, center, player);
							gameController.setCounter(ctr);
						});
						fadeOut.play();
					});
					textFadeOut.play();
				});
				textDelay.play();
			});
			textFadeIn.play();
		});
		fadeIn.play();

	}

	private void addStepToTrail(int mazeX, int mazeY, String direction) {
		int visibleTiles = gameController.getRadius() * 2 + 1;
		int cellSize = (int) (820.0 / visibleTiles);

		ImageView step = createStepImage(cellSize, direction);
		// Save data for next use
		step.setUserData(new int[] { mazeX, mazeY });
		stepTrail.add(step);

		// Manage the trail length
		if (stepTrail.size() >= maxTrailLength) {
			ImageView oldestStep = stepTrail.remove(0);
			fadeOnDistance(oldestStep);

		}
	}

	private ImageView createStepImage(int cellSize, String direction) {
		InputStream stream = getClass()
				.getResourceAsStream("/images/" + playerClass.getUserClass().toLowerCase() + "_step.png");
		ImageView step = new ImageView(new Image(stream));

		step.setFitWidth(cellSize);
		step.setFitHeight(cellSize);
		step.setMouseTransparent(true);
		step.setOpacity(0.8);

		switch (direction) {
		case "Up":
			step.setRotate(0);
			break;
		case "Down":
			step.setRotate(180);
			break;
		case "Left":
			step.setRotate(270);
			break;
		case "Right":
			step.setRotate(90);
			break;
		}
		return step;
	}

	private void drawStepTrail(int playerCenterX, int playerCenterY) {
		int radius = gameController.getRadius();
		int visibleTiles = radius * 2 + 1;

		for (ImageView step : stepTrail) {
			int[] stepMazeCoords = (int[]) step.getUserData();
			int stepMazeX = stepMazeCoords[0];
			int stepMazeY = stepMazeCoords[1];

			int gridCol = stepMazeY - (playerCenterY - radius);
			int gridRow = stepMazeX - (playerCenterX - radius);

			// Only add the step if it's within the visible portion of the grid
			if (gridCol >= 0 && gridCol < visibleTiles && gridRow >= 0 && gridRow < visibleTiles) {
				grid.add(step, gridCol, gridRow);

			}
		}
	}

	private ImageView createPlayerImageView(Userclass playerClass, Player player, int cellSize) {
		InputStream stream = getClass().getResourceAsStream(playerClass.getClassPathImg());
		ImageView imageView = new ImageView(new Image(stream));

		// Set rotation based on direction
		switch (player.getDirection()) {
		case "Up":
			imageView.setRotate(270);
			break;
		case "Down":
			imageView.setRotate(90);
			break;
		case "Left":
			imageView.setRotate(0);
			imageView.setScaleX(-1); // flip
			break;
		default:
			imageView.setRotate(0);

			break; // RIGHT
		}

		imageView.setPreserveRatio(false);
		imageView.setFitWidth(cellSize);
		imageView.setFitHeight(cellSize);

		if (playerClass.getAbility().isActive() && playerClass.getUserClass().equals("Psychic")) {
			imageView.setOpacity(0.6);
		}

		return imageView;
	}

	private void fadeOnDistance(ImageView step) {
		FadeTransition fade = new FadeTransition(Duration.seconds(2), step);
		fade.setFromValue(0.9);
		fade.setToValue(0);
		fade.setOnFinished(e -> grid.getChildren().remove(step)); // Cleanup
		fade.play();

	}

	public void redrawScene(int playerNewX, int playerNewY) {

		if (playerClass.isBeamActive() && System.currentTimeMillis() > playerClass.getBeamExpiryTime()) {
			playerClass.setBeamActive(false);
			playerClass.setActiveBeamPath(null);
		}

		// 1. Clear the entire grid from the previous frame
		grid.getChildren().clear();

		// 2. Draw the maze floor tiles (logic from GameController.drawMaze)
		drawMazeTiles(playerNewX, playerNewY);

		// 3. Draw the footsteps from our trail list onto the grid
		drawStepTrail(playerNewX, playerNewY);

		// 4. Draw the player on top of everything else
		placePlayer(grid, playerClass, false, playerNewX, playerNewY, player);
	}

	private void drawMazeTiles(int playerCenterX, int playerCenterY) {

		if (playerClass.isBeamActive()) {
			gameController.getFog().setTemporaryRevealPath(playerClass.getActiveBeamPath());
		} else {
			// Otherwise, tell it there's no path to reveal.
			gameController.getFog().setTemporaryRevealPath(null);
		}

		gameController.drawMaze(grid, gameController.getMaze().getSize(), gameController.getMaze().getGrid(),
				playerCenterX, playerCenterY);
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

	public List<ImageView> getStepTrail() {
		return stepTrail;
	}

	public void setStepTrail(List<ImageView> stepTrail) {
		this.stepTrail = stepTrail;
	}

	public int getCellSize() {
		return cellSize;
	}

	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
	}

	private void setGameController(GameController gameController) {
		this.gameController = gameController;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

	public CooldownIcon getAbilityIcon() {
		return abilityIcon;
	}

	public void setAbilityIcon(CooldownIcon abilityIcon) {
		this.abilityIcon = abilityIcon;
	}

	public CooldownIcon getPassiveIcon() {
		return passiveIcon;
	}

	public void setPassiveIcon(CooldownIcon passiveIcon) {
		this.passiveIcon = passiveIcon;
	}

}
