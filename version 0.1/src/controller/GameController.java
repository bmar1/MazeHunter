/* This class is supposed to control the main logic of the game (fog, maze gen), kinda act as the recipient to all main actions (like moving
 * and will act accordingly, probably will be one big class, or to make it smaller can feed information to the next controller (player controller)
 * which will take action there upon recieving a request
 */

package controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import model.*;
import view.*;

public class GameController {

	private Maze maze;
	private IntroController introController;
	private int radius = 6;
	private Fog fog;
	private StackPane root;
	private int counter;

	// introController.getDifficulty().equals("Easy") ? 9 :
	// introController.getDifficulty().equals("Medium") ? 6 : 5;

	public GameController(StackPane root, Group gameLayer, GridPane grid) {
		this.root = root;
		initialize(root, gameLayer, grid);

	}

	// initalize game, reset screen to completely plane except grid
	// add maze, add columns, cells, initalize fog of war
	private void initialize(StackPane root, Group gameLayer, GridPane grid) {
		// Create maze based on difficulty
		// maze = new Maze(introController.getDifficulty().equals("Easy") ? 21 :
		// introController.getDifficulty().equals("Medium") ? 31 : 41);

		// counter determined on diffculty 3 if hard 2 med etc
		// gameLayer.getChildren().clear();

		maze = new Maze(31);
		setMaze(maze);
		int size = maze.getSize();
		int[][] mazeGrid = maze.getGrid();

		int centerX = (int) Math.floor(size / 2.0);
		int centerY = (int) Math.floor(size / 2.0);

		System.out.println(centerX + ": " + centerY);

		drawMaze(grid, size, mazeGrid, gameLayer, centerX, centerY);
	}

	// Generates maze on screen given maze co-ords
	public void drawMaze(GridPane grid, int size, int[][] mazeGrid, Group gameLayer, int centerX, int centerY) {

		List<Node> toRemove = new ArrayList<>();
		for (Node node : grid.getChildren()) {
			toRemove.add(node);
		}
		grid.getChildren().removeAll(toRemove);
		grid.getColumnConstraints().clear();
		grid.getRowConstraints().clear();

		// Get the amount of tiles we can see + the cell size to fit in the vision
		// circle
		int visibleTiles = radius * 2 + 1; // diameter of visible circle
		int cellSize = (int) Math.min(1420.0 / visibleTiles, 850.0 / visibleTiles);

		// Calculate our start and end positions of the circle in maze
		int startX = Math.max(0, centerX - radius);
		int startY = Math.max(0, centerY - radius);
		int endX = Math.min(size - 1, centerX + radius + 1);
		int endY = Math.min(size - 1, centerY + radius + 1);

		// modify the grid to fit only each cell within the circle
		for (int i = 0; i < visibleTiles; i++) {
			ColumnConstraints column = new ColumnConstraints();
			column.setPrefWidth(cellSize);
			column.setMinWidth(cellSize);
			column.setMaxWidth(cellSize);
			grid.getColumnConstraints().add(column);

			RowConstraints row = new RowConstraints();
			row.setPrefHeight(cellSize);
			row.setMinHeight(cellSize);
			row.setMaxHeight(cellSize);
			grid.getRowConstraints().add(row);
		}

		// for each cell within our vision circle
		for (int row = startX; row < endX; row++) {
			for (int col = startY; col < endY; col++) {

				// only continue if the current cell is within our vision radius
				double distance = Math.sqrt(Math.pow(row - centerX, 2) + Math.pow(col - centerY, 2));
				String imagePath = "";
				if (distance > radius + 0.2)
					continue;

				// convert cell coords to grid coords
				int gridCol = col - (centerY - radius);
				int gridRow = row - (centerX - radius);

				// Select the image based on the current cell's state if valid
				if (gridCol >= 0 && gridRow >= 0 && gridCol < visibleTiles && gridRow < visibleTiles) {
					int cellType = mazeGrid[row][col];
					drawImg(imagePath, cellType, grid, cellSize, gridCol, gridRow);
				}

			}
		}

		// declare fog, otherwise redraw fog
		if (fog == null || fog.getFogCanvas() == null || !gameLayer.getChildren().contains(fog.getFogCanvas())) {
			fog = new Fog(visibleTiles, visibleTiles, cellSize, radius, centerX, centerY);
			gameLayer.getChildren().add(fog.getFogCanvas());
		}else
			fog.updateFog(centerX, centerY, visibleTiles, visibleTiles, radius);
	}

	private void drawImg(String imagePath, int cellType, GridPane grid, int cellSize, int col, int row) {
		switch (cellType) {
		case -1:
			imagePath = "/images/boundary.png";
			break; // Boundary
		case -2:
			imagePath = "/images/doorOpen.png";
			break; // Exit
		case 1:
			imagePath = "/images/path.png";
			break; // path
		case 0:
			imagePath = "/images/wall.png";
			break;
		}

		// Load the image and apply to ImageView
		InputStream stream = getClass().getResourceAsStream(imagePath);
		Image image = new Image(stream);
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(cellSize);
		imageView.setFitHeight(cellSize);

		grid.add(imageView, col, row);

	}

	public int getRadius() {
		return radius;
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
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

}
