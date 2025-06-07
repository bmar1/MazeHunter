package model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Fog {
	private Canvas fogCanvas;
	private GraphicsContext gc;

	private double cellSize;
	private double visionRadius;
	double fogOpacity[][];

	public Fog(int visibleCols, int visibleRows, double cellSize, int radius, int centerX, int centerY) {

		this.cellSize = cellSize;
		this.visionRadius = radius;

		// Full maze coverage
		fogCanvas = new Canvas(visibleCols * cellSize, visibleRows * cellSize);
		gc = fogCanvas.getGraphicsContext2D();

		initFog(visibleCols, visibleRows);

	}

	public Canvas getFogCanvas() {
		return fogCanvas;
	}

	public void initFog(int visibleCols, int visibleRows) {
		int fogCenterX = visibleCols / 2;
		int fogCenterY = visibleRows / 2;

		fogOpacity = new double[visibleRows][visibleCols];

		for (int row = 0; row < visibleRows; row++) {
			for (int col = 0; col < visibleCols; col++) {

				// only continue if the current cell is within our vision radius
				double distance = Math.sqrt(Math.pow(col - fogCenterX, 2) + Math.pow(row - fogCenterY, 2));

				fogOpacity[row][col] = calculateOpacity(distance);

			}
		}

		drawFog();
	}

	public void updateFog(int playerMazeX, int playerMazeY, int visibleCols, int visibleRows, int radius) {
		gc.clearRect(0, 0, fogCanvas.getWidth(), fogCanvas.getHeight());

		fogOpacity = new double[visibleRows][visibleCols];

		// Center of viewport in grid coordinates
		int viewportCenterX = visibleCols / 2;
		int viewportCenterY = visibleRows / 2;

		// Calculate top-left corner of visible area in maze coordinates
		int mazeStartX = playerMazeX - viewportCenterX;
		int mazeStartY = playerMazeY - viewportCenterY;

		for (int gridRow = 0; gridRow < visibleRows; gridRow++) {
			for (int gridCol = 0; gridCol < visibleCols; gridCol++) {
				// Convert grid to maze coordinates
				int mazeX = mazeStartX + gridCol; // Note: gridCol affects X
				int mazeY = mazeStartY + gridRow; // Note: gridRow affects Y

				// Calculate actual distance from player
				double distance = Math.sqrt(Math.pow(mazeX - playerMazeX, 2) + Math.pow(mazeY - playerMazeY, 2));

				fogOpacity[gridRow][gridCol] = calculateOpacity(distance);

				// Debug output
				System.out.printf("Player@[%d,%d] | Grid[%d,%d]->Maze[%d,%d] | Dist:%.1f\n", playerMazeX, playerMazeY,
						gridCol, gridRow, mazeX, mazeY, distance);
			}
		}
		drawFog();
	}

	private double calculateOpacity(double distance) {
		if (distance > visionRadius)
			return 1.0;
		if (Math.abs(distance - visionRadius) < 1)
			return 0.4;
		return 0.1;
	}

	// !
	private void drawFog() {
		gc.clearRect(0, 0, fogCanvas.getWidth(), fogCanvas.getHeight()); // Clear previous frame

		for (int row = 0; row < fogOpacity.length; row++) {
			for (int col = 0; col < fogOpacity[0].length; col++) {
				double opacity = fogOpacity[row][col];
				gc.setFill(new Color(0, 0, 0, opacity));
				gc.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
			}
		}
	}

	public double[][] getFogOpacity() {
		return fogOpacity;
	}

	public void setFogOpacity(double[][] fogOpacity) {
		this.fogOpacity = fogOpacity;
	}

}
