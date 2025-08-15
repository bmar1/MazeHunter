package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.util.Duration;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Fog {
	private Canvas fogCanvas;
	private GraphicsContext gc;

	private double cellSize;
	private double visionRadius;
	double fogOpacity[][];

	private List<Point2D> temporaryRevealPath = null;
	private final Map<Integer, Image> tileImageCache = new HashMap<>();

	public Fog(double sCREEN_SIZE, double sCREEN_SIZE2, double cellSize, int radius) {

		this.cellSize = cellSize;
		this.visionRadius = radius;

		// Cover the entire screen
		fogCanvas = new Canvas(sCREEN_SIZE, sCREEN_SIZE2);
		fogCanvas.setLayoutX(0);
		fogCanvas.setLayoutY(0);
		System.out.println(fogCanvas.getWidth());
		gc = fogCanvas.getGraphicsContext2D();

		preloadImages();

	}

	private void preloadImages() {
		tileImageCache.put(-1, new Image(getClass().getResourceAsStream("/images/boundary.png")));
		tileImageCache.put(0, new Image(getClass().getResourceAsStream("/images/wall.png")));
		tileImageCache.put(1, new Image(getClass().getResourceAsStream("/images/path.png")));

	}

	/*
	 * Update and draw new fog area depending new player position as center, using
	 * radius and distance only reveal a certain area Interact with ability (light
	 * beam) and check if it pierces fog.
	 */
	public void updateAndDraw(int playerMazeX, int playerMazeY, int visionRadius, int[][] mazeGrid) {
		int visibleTiles = visionRadius * 2 + 1;
		int gridPixelSize = visibleTiles * (int) this.cellSize;
		int intCellSize = (int) this.cellSize;

		this.cellSize = Math.floor(this.cellSize);

		// Calculate the top-left MAZE coordinate of the visible area.
		int mazeStartX = playerMazeX - visionRadius;
		int mazeStartY = playerMazeY - visionRadius;

		// Maze offset, since it covers the whole screen, versus the visible content
		// pane (not whole screen)
		double offsetX = (this.fogCanvas.getWidth() - gridPixelSize) / 2.0;
		double offsetY = (this.fogCanvas.getHeight() - gridPixelSize) / 2.0;

		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, fogCanvas.getWidth(), fogCanvas.getHeight());

		for (int row = 0; row < visibleTiles + 2; row++) {
			for (int col = 0; col < visibleTiles + 2; col++) {

				int currentMazeX = mazeStartX + row;
				int currentMazeY = mazeStartY + col;

				// calculate distance and reveal based on it
				double distance = Math
						.sqrt(Math.pow(currentMazeX - playerMazeX, 2) + Math.pow(currentMazeY - playerMazeY, 2));

				double screenX = (col * intCellSize) + offsetX;
				double screenY = (row * intCellSize) + offsetY;

				double opacity = calculateOpacity(distance, visionRadius, row, col);

				if (opacity >= 1.0)
					continue;

				boolean isRevealedByBeam = (temporaryRevealPath != null
						&& temporaryRevealPath.contains(new Point2D(col, row)));

				if (distance <= visionRadius) {
					gc.clearRect(screenX, screenY, intCellSize, intCellSize);

					gc.setFill(new Color(0, 0, 0, opacity));
					gc.fillRect(screenX, screenY, intCellSize, intCellSize);

				} else if (isRevealedByBeam && distance > visionRadius) {
					// We are OUTSIDE the main circle, but revealed by the BEAM.
					// The Fog class must draw this tile itself.
					int cellType = getCellTypeFromMaze(currentMazeX, currentMazeY, mazeGrid);
					Image tileImage = tileImageCache.get(cellType);
					if (tileImage != null) {

						gc.drawImage(tileImage, screenX, screenY, intCellSize + 1, intCellSize);
					}
				} else if (Math.abs(distance - visionRadius) < 1) {

					gc.setFill(new Color(0, 0, 0, opacity));
					gc.fillRect(screenX, screenY, cellSize, cellSize);
				}

			}

		}

	}

	private int getCellTypeFromMaze(int mazeX, int mazeY, int[][] grid) {
		if (mazeX >= 0 && mazeX < grid.length && mazeY >= 0 && mazeY < grid[0].length) {
			return grid[mazeX][mazeY];
		}
		return -1; // Treat out-of-bounds as a boundary wall.
	}

	private double calculateOpacity(double distance, int radius, int mazeRow, int mazeCol) {
		// First, check if this cell should be temporarily revealed
		if (temporaryRevealPath != null && temporaryRevealPath.contains(new Point2D(mazeCol, mazeRow))) {

			System.out.println(mazeRow + " " + mazeCol);
			return 0.1; // Fully transparent (revealed)

		}

		// If not temporarily revealed, fall back to radius logic
		if (distance > radius)
			return 1.0;
		if (Math.abs(distance - radius) < 1)
			return 0.4;

		return 0.1;
	}

	public void setTemporaryRevealPath(List<Point2D> path) {
		this.temporaryRevealPath = path;
	}

	public double[][] getFogOpacity() {
		return fogOpacity;
	}

	public void setFogOpacity(double[][] fogOpacity) {
		this.fogOpacity = fogOpacity;
	}

	public Canvas getFogCanvas() {
		return fogCanvas;
	}

}
