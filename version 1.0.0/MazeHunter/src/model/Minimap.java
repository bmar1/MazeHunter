package model; // Or model

import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Minimap {

	private final Canvas canvas;
	private final GraphicsContext gc;
	private final double minimapSize;
	private final String shape;

	private long[][] revealTimestamps;
	private final long revealDurationMillis = 4000; // 4 seconds to reveal each area
	private List<Point2D> temporaryBeamPath = null;

	private final Color pathColor = Color.web("#BFA588");
	private final Color wallColor = Color.web("#3E314A");
	private final Color playerColor = Color.web("#FF4136", 0.9);
	private final Color exitColor = Color.web("#00BFFF");
	private final Color fogColor = Color.BLACK;

	public Minimap(double size, String shape, int mazeWidth, int mazeHeight) {
		this.minimapSize = size;
		this.shape = shape;
		this.canvas = new Canvas(size, size);
		this.gc = canvas.getGraphicsContext2D();

		this.revealTimestamps = new long[mazeHeight][mazeWidth];

		// clip to make circle
		if ("circle".equalsIgnoreCase(shape)) {
			Circle clip = new Circle(size / 2, size / 2, size / 2);
			this.canvas.setClip(clip);

		}
	}

	/**
	 * Restarts the game at the specified maze layer, resetting the grid, HUD, and
	 * player position, while preserving ability and passive cooldowns.
	 *
	 * @param mazeGrid            to work on
	 * @param playerMazeX,Y       player position to draw
	 * @param playerVisionRadius, radius to draw on (reduced)
	 * @param player              true radius
	 */
	public void update(int[][] mazeGrid, int playerMazeX, int playerMazeY, int playerVisionRadius, int radius) {
		long currentTime = System.currentTimeMillis();

		// Reveal the area immediately around the player.
		// We loop through a square area centered on the player.
		for (int dRow = -playerVisionRadius; dRow <= playerVisionRadius; dRow++) {
			for (int dCol = -playerVisionRadius; dCol <= playerVisionRadius; dCol++) {

				int mazeX = playerMazeX + dRow;
				int mazeY = playerMazeY + dCol;

				// Check if the coordinate is valid.
				if (mazeX >= 0 && mazeX < revealTimestamps.length && mazeY >= 0 && mazeY < revealTimestamps[0].length) {
					// Update the timestamp for this tile to "now".
					revealTimestamps[mazeX][mazeY] = currentTime;
				}
			}
		}

		// reveal light beam path, converting to gridCol and row to reveal each node
		// apart of the path
		if (temporaryBeamPath != null) {
			int mainViewStartX = playerMazeX - radius;
			int mainViewStartY = playerMazeY - radius;

			for (Point2D screenPoint : temporaryBeamPath) {
				int gridCol = (int) screenPoint.getX();
				int gridRow = (int) screenPoint.getY();

				// Convert the on-screen grid coordinate back to an absolute maze coordinate.
				int mazeX = mainViewStartX + gridRow;
				int mazeY = mainViewStartY + gridCol;

				// Check if the calculated coordinate is valid and reveal it.

				if (mazeX >= 0 && mazeX < revealTimestamps.length && mazeY >= 0 && mazeY < revealTimestamps[0].length) {
					revealTimestamps[mazeX][mazeY] = currentTime;
				}

			}
		}

		int mazeHeight = mazeGrid.length;
		int mazeWidth = mazeGrid[0].length;
		double cellWidth = minimapSize / mazeWidth;
		double cellHeight = minimapSize / mazeHeight;

		gc.clearRect(0, 0, minimapSize, minimapSize);
		gc.setFill(fogColor);
		gc.fillRect(0, 0, minimapSize, minimapSize);

		for (int row = 0; row < mazeHeight; row++) {
			for (int col = 0; col < mazeWidth; col++) {

				long lastSeenTime = revealTimestamps[row][col];

				if (lastSeenTime == 0) {
					continue;
				}

				// Check if the reveal duration has expired.
				if (currentTime - lastSeenTime > revealDurationMillis) {
					revealTimestamps[row][col] = 0;
					continue; // Skip drawing, leaving it as fog.
				}

				int cellType = mazeGrid[row][col];
				switch (cellType) {
				case 1:
					gc.setFill(pathColor);
					break;
				case -2:
					gc.setFill(exitColor);
					break;
				default:
					gc.setFill(wallColor);
					break;
				}

				double mapX = col * cellWidth;
				double mapY = row * cellHeight;
				gc.fillRect(mapX, mapY, Math.ceil(cellWidth), Math.ceil(cellHeight));
			}
		}

		// Draw player dot position, styling and setting the oval to a circle sized
		double playerMapX = playerMazeY * cellWidth;
		double playerMapY = playerMazeX * cellHeight;
		gc.setFill(playerColor);
		gc.setStroke(Color.WHITE);
		gc.setLineWidth(1);
		double markerSize = Math.max(4, cellWidth * 1.5);
		gc.fillOval(playerMapX - markerSize / 2 + cellWidth / 2, playerMapY - markerSize / 2 + cellHeight / 2,
				markerSize, markerSize);
		gc.strokeOval(playerMapX - markerSize / 2 + cellWidth / 2, playerMapY - markerSize / 2 + cellHeight / 2,
				markerSize, markerSize);

		if ("circle".equalsIgnoreCase(shape)) {
			gc.setStroke(Color.WHITE);
			gc.setLineWidth(4);
			gc.strokeOval(0, 0, minimapSize, minimapSize);
		}

		else if ("rectangle".equalsIgnoreCase(shape)) {
			gc.setStroke(Color.WHITE);
			gc.setLineWidth(4);
			gc.strokeRect(0, 0, minimapSize, minimapSize);
		}

	}

	public Canvas getCanvas() {
		return this.canvas;
	}

	public List<Point2D> getTemporaryBeamPath() {
		return temporaryBeamPath;
	}

	public void setTemporaryBeamPath(List<Point2D> temporaryBeamPath) {
		this.temporaryBeamPath = temporaryBeamPath;
	}

}