/* This class is supposed to control the main logic of the game (fog, maze gen), kinda act as the recipient to all main actions (like moving
 * and will act accordingly, probably will be one big class, or to make it smaller can feed information to the next controller (player controller)
 * which will take action there upon recieving a request
 */

package controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.*;
import view.*;

public class GameController {


	private IntroController introController;
	private PlayerController playerController;
	private int radius;
	private Fog fog;
	private Maze maze;
	
	private int repeatCounter;

	private GridPane grid;
	private VBox pauseMenu;
	
	private List<Point2D> activeBeamPath = null;
	private Map<Integer, Image> spriteMap = new HashMap<>();
	
	private Minimap minimap = null;
	private StackPane minimapFrame;
	private Pane hudLayer;
	private StackPane root;
	private GameScreen gameScreen;
	private NavController navController = new NavController();
	
	private final double VIEWPORT_SIZE = 820.0;
	private final double SCREEN_SIZE = 980.0;

	public GameController(StackPane root, GridPane grid, GameScreen gameScreen, IntroController introController,
			Pane hudLayer) {
		this.root = root;
		this.grid = grid;
		this.gameScreen = gameScreen;
		this.introController = introController;
		this.hudLayer = hudLayer;
		initialize(root, grid);

		Platform.runLater(() -> {
			Scene scene = root.getScene();
			if (scene != null) {
				scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
				scene.setOnKeyPressed(event -> { // if escape is pressed, pause menu
					if (event.getCode() == KeyCode.ESCAPE) {
						togglePauseMenu();
					}
				});
			}
		});

	}

	/**
	 * Draws the visible portion of the maze centered on the player, updates fog of
	 * war, minimap, and HUD.
	 *
	 * @param grid     GridPane to draw on.
	 * @param mazeSize Size of the maze (width = height = mazeSize).
	 * @param mazeGrid 2D array of maze cell types.
	 * @param centerX  Player's X position in maze coordinates.
	 * @param centerY  Player's Y position in maze coordinates.
	 */
	private void initialize(Pane root, GridPane grid) {
		// Create maze based on difficulty
		maze = new Maze(introController.getDifficulty().equals("Easy") ? 21
				: introController.getDifficulty().equals("Medium") ? 31 : 41);

		// counter determined on diffculty 3 if hard 2 med etc
		int counterValue = introController.getDifficulty().equals("Easy") ? 1
				: introController.getDifficulty().equals("Medium") ? 2 : 3;

		setCounter(counterValue);

		int radius = introController.getDifficulty().equals("Easy") ? 4
				: introController.getDifficulty().equals("Medium") ? 5 : 7;

		setRadius(radius);
		setMaze(maze);
		int mazeSize = maze.getSize();

		int[][] mazeGrid = null;
		if (maze.getGrid() != null)
			mazeGrid = maze.getGrid();

		int playerX = (int) Math.floor(mazeSize / 2.0);
		int playerY = (int) Math.floor(mazeSize / 2.0);
		
		loadSprites();

		drawMaze(grid, mazeSize, mazeGrid, playerX, playerY);

		if (this.minimap == null) {
			this.minimap = new Minimap(200, "Rectangle", maze.getSize(), maze.getSize());
		}

		if (this.minimapFrame == null) {
			Canvas minimapCanvas = minimap.getCanvas();
			minimapFrame = new StackPane(minimapCanvas);
			minimapFrame.setPrefSize(minimapCanvas.getWidth(), minimapCanvas.getHeight());
			hudLayer.getChildren().add(minimapFrame);
		}
	}

	

	// Generates maze on screen given maze co-ords
	public void drawMaze(GridPane grid, int mazeSize, int[][] mazeGrid, int playerX, int playerY) {

		grid.getColumnConstraints().clear();
		grid.getRowConstraints().clear();

		// Get the amount of tiles we can see + the cell size to fit in the visionw
		// circle
		int visibleTiles = radius * 2 + 1; // diameter of visible circle
		int cellSize = (int) (VIEWPORT_SIZE / visibleTiles); // calculate cellSize based on visible content pane

		// Calculate our start and end positions of the circle in maze
		int startX = Math.max(0, playerX - radius);
		int startY = Math.max(0, playerY - radius);

		int endX = Math.min(mazeSize, playerX + radius + 1);
		int endY = Math.min(mazeSize, playerY + radius + 1);

		// modify the grid to fit only each cell within the circle
		for (int i = 0; i < visibleTiles; i++) {
			ColumnConstraints col = new ColumnConstraints(cellSize);
			grid.getColumnConstraints().add(col);
			RowConstraints row = new RowConstraints(cellSize);
			grid.getRowConstraints().add(row);
		}

		for (int row = startX; row < endX; row++) {
			for (int col = startY; col < endY; col++) {

				// only continue if the current cell is within our vision radius
				double dx = row - playerX;
				double dy = col - playerY;
				if ((dx * dx + dy * dy) > (radius + 0.2) * (radius + 0.2))
					continue;

				// Convert col/row (maze) to gridCoordinates
				int gridCol = col - (playerY - radius);
				int gridRow = row - (playerX - radius);

				// Select the image based on the current cell's state if valid
				if (gridCol >= 0 && gridRow >= 0 && gridCol < visibleTiles && gridRow < visibleTiles) {
					int cellType = mazeGrid[row][col];
					try {
						drawImg(cellType, grid, cellSize, gridCol, gridRow);
					} catch (IOException e) {

						e.printStackTrace();
					}

				}

			}
		}

		// Update the HUD of the player with the new move
		updateHUD(cellSize, playerX, playerY, mazeGrid);

	}

	private void updateHUD(int cellSize, int playerX, int playerY, int[][] mazeGrid) {

		if (fog == null || fog.getFogCanvas() == null || !root.getChildren().contains(fog.getFogCanvas())) {
			fog = new Fog(SCREEN_SIZE, SCREEN_SIZE, cellSize, radius);
			root.getChildren().add(fog.getFogCanvas());
		}

		fog.setTemporaryRevealPath(getActiveBeamPath());
		fog.updateAndDraw(playerX, playerY, radius, maze.getGrid());

		if (this.minimap != null) {

			int minimapRevealRadius = radius / 2; // reveal radius will be half of players vision
			minimap.setTemporaryBeamPath(getActiveBeamPath());
			this.minimap.update(mazeGrid, playerX, playerY, minimapRevealRadius, radius);

		}

		// Layer ui elements together, the fog stays ontop of the playable grid to be
		// blurry/not visible
		// Minimap is layered ontop of this since it sits in the corner, as well as
		// icons/HUD
		Platform.runLater(() -> {
			if (fog != null) {
				fog.getFogCanvas().toFront();
			}

			if (minimapFrame != null) {
				StackPane.setAlignment(minimapFrame, Pos.TOP_LEFT);
				minimapFrame.toFront();
			}

			// Place ability icons on the bottom right
			if (playerController != null) {
				if (playerController.getAbilityIcon() != null) {
					StackPane view = playerController.getAbilityIcon().getView();
					view.toFront();
					view.setLayoutX(root.getWidth() - view.getWidth() - 150);
					view.setLayoutY(root.getHeight() - view.getHeight() - 20);
				}

				if (playerController.getPassiveIcon() != null) {
					StackPane view = playerController.getPassiveIcon().getView();

					view.setLayoutX(root.getWidth() - view.getWidth() - 20);
					view.setLayoutY(root.getHeight() - view.getHeight() - 20);
					view.toFront();
				}

			}
			hudLayer.toFront();

		});

		if (fog != null) {
			fog.getFogCanvas().setMouseTransparent(true);
		}
	}

	/**
	 * Draws a maze cell image at the specified grid coordinates.
	 * 
	 * @param cellType Integer code for the cell type (-1 boundary, -2 exit, 0 wall,
	 *                 1 path).
	 * @param grid     GridPane to draw on.
	 * @param cellSize Size in pixels for the cell.
	 * @param col      Column in the grid to place the cell.
	 * @param row      Row in the grid to place the cell.
	 */

	public void drawImg(int cellType, GridPane grid, int cellSize, int col, int row) throws IOException {

		Image sprite = spriteMap.get(cellType);
		ImageView imageView = new ImageView(sprite);

		imageView.setFitWidth(cellSize);
		imageView.setFitHeight(cellSize);
		imageView.setSmooth(false); // prevent anti-alias gaps
		imageView.setPreserveRatio(false); // fill the entire cell

		grid.add(imageView, col, row);
	}

	/** Toggles the pause menu on/off and updates the paused state of the player. */
	private void togglePauseMenu() {
		if (pauseMenu == null) { // Menu closed, create it
			createPauseMenu();
			playerController.setPaused(true);
		} else {
			closePauseMenu();
			playerController.setPaused(false);
		}
	}
	
	private void loadSprites() {
		spriteMap.put(-1, new Image(getClass().getResource("/images/boundary.png").toExternalForm()));
	    spriteMap.put(-2, new Image(getClass().getResource("/images/doorClosed.png").toExternalForm()));
	    spriteMap.put(1, new Image(getClass().getResource("/images/path.png").toExternalForm()));
	    spriteMap.put(0, new Image(getClass().getResource("/images/wall.png").toExternalForm()));
	   
		
	}

	/** Closes the pause menu, restores game effects, and resets player volume. */
	private void closePauseMenu() {
		if (pauseMenu != null) {
			// Set the volume back to its default state
			gameScreen.getPlayer().setVolume(0.30);

			grid.setEffect(null);
			pauseMenu = null;
			minimap.getCanvas().setEffect(null);
			root.getChildren().remove(pauseMenu);

		}
	}

	/**
	 * Creates and displays the pause menu overlay, applies visual effects to the
	 * game grid, and wires up button actions.
	 */
	private void createPauseMenu() {
		pauseMenu = new VBox(20);
		pauseMenu.setAlignment(Pos.CENTER);
		pauseMenu.getStyleClass().add("pause-menu");
		pauseMenu.setMaxSize(SCREEN_SIZE, SCREEN_SIZE);

		// 2. Create the title and apply its style class
		Label title = new Label("Paused");
		title.getStyleClass().add("pause-menu-title");

		// 3. Create the buttons and apply their style class
		Button resumeButton = new Button("Resume Game");
		Button homeButton = new Button("Return to Home Screen");
		Button quitButton = new Button("Quit Game");

		// Add the base style class to all buttons
		resumeButton.getStyleClass().add("menu-button");
		homeButton.getStyleClass().add("menu-button");
		quitButton.getStyleClass().add("menu-button");

		// 4. Set button actions
		resumeButton.setOnAction(e -> togglePauseMenu());

		homeButton.setOnAction(event -> {
			if (navController != null) {

				// We create a "synthetic" event source for the method.
				navController.setPlayer(gameScreen.getPlayer());
				Node eventSource = (Node) event.getSource();

				navController.returnHome(new MouseEvent(eventSource, eventSource, MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0,
						MouseButton.PRIMARY, 1, false, false, false, false, true, false, false, false, false, false,
						null));
			}
		});

		quitButton.setOnAction(e -> Platform.exit());

		// 5. Add all elements to the VBox
		pauseMenu.getChildren().addAll(title, resumeButton, homeButton, quitButton);

		// Add effects
		gameScreen.getPlayer().setVolume(0.1);
		minimap.getCanvas().setEffect(new GaussianBlur(4)); // 4 for soft blur
		grid.setEffect(new GaussianBlur(4));

		if (!root.getChildren().contains(pauseMenu))
			root.getChildren().add(pauseMenu);

	}

	public int getRadius() {
		return radius;
	}

	public void setPlayerController(PlayerController playerController) {
		this.playerController = playerController;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public Fog getFog() {
		return fog;
	}

	public void setFog(Fog fog) {
		this.fog = fog;
	}

	public StackPane getRoot() {
		return root;
	}

	public void setRoot(StackPane root) {
		this.root = root;
	}

	public Maze getMaze() {
		return maze;
	}

	public void setMaze(Maze maze) {
		this.maze = maze;
	}

	public int getCounter() {
		return repeatCounter;
	}

	private List<Point2D> getActiveBeamPath() {
		return this.activeBeamPath;
	}

	public Pane getHudLayer() {
		return hudLayer;
	}

	public void setHudLayer(Pane hudLayer) {
		this.hudLayer = hudLayer;
	}

	public void setCounter(int counter) {
		this.repeatCounter = counter;
	}

	public GameScreen getGameScreen() {
		return gameScreen;
	}

	public void setGameScreen(GameScreen gameScreen) {
		this.gameScreen = gameScreen;
	}

	public void setBeamPath(List<Point2D> activeBeamPath1) {
		this.activeBeamPath = activeBeamPath1;

	}

	public Minimap getMinimap() {
		return minimap;
	}

	public void setNavController(NavController navController) {
		this.navController = navController;
	}

	public void setMinimap(Minimap minimap) {
		this.minimap = minimap;
	}

	public IntroController getIntroController() {
		return introController;
	}

	public void setIntroController(IntroController introController) {
		this.introController = introController;
	}

}