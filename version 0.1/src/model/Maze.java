package model;

import java.util.Arrays;
import java.util.Random;

public class Maze {
	private int[][] grid; // -1 for boundary walls, 0 for normal walls, 1 for paths
	private int size; // Size of the maze (must be odd for proper generation)
	private int maxExits; // Maximum number of exits allowed
	private Random rand; // Random number generator
	private Fog fog;

	public Maze(int size) {
		this.size = size;
		this.grid = new int[size][size];
		this.rand = new Random();
		this.maxExits = calculateMaxExits(size);
		initializeGrid(); // Initialize the grid with walls
		carvePath(1, 1); // Start carving from (1, 1)
		placeExits(); // Place exits in the maze
	}

	// Calculate the maximum number of exits based on size
	private int calculateMaxExits(int size) {
		if (size <= 5)
			return 1;
		return Math.round(size / 13); // One exit for every 10 units of size
	}

	// Initialize the grid
	private void initializeGrid() {
		// Set all cells to walls
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				grid[i][j] = 0; // All cells as normal walls (0)
			}
		}
		// Set the outer walls
		for (int i = 0; i < size; i++) {
			grid[0][i] = -1; // Top wall
			grid[size - 1][i] = -1; // Bottom wall
			grid[i][0] = -1; // Left wall
			grid[i][size - 1] = -1; // Right wall
		}
	}

	// Recursive backtracking to carve out the paths
	private void carvePath(int x, int y) {
		grid[x][y] = 1; // Mark current position as a path

		// Define possible directions (right, down, left, up)
		int[][] directions = { { 0, 2 }, { 2, 0 }, { 0, -2 }, { -2, 0 } }; // Move in two steps
		shuffle(directions); // Shuffle to randomize path generation

		for (int[] dir : directions) {
			int nx = x + dir[0]; // New x position
			int ny = y + dir[1]; // New y position

			// Check if the new position is within bounds and is a wall
			if (nx > 0 && ny > 0 && nx < size - 1 && ny < size - 1 && grid[nx][ny] == 0) {
				// Remove the wall between the current cell and the new cell
				grid[x + dir[0] / 2][y + dir[1] / 2] = 1; // Carve path
				carvePath(nx, ny); // Recursively carve the path
			}
		}
	}

	// Shuffle directions to randomize movement
	private void shuffle(int[][] array) {
		Random rand = new Random();
		for (int i = array.length - 1; i > 0; i--) {
			int index = rand.nextInt(i + 1);
			int[] temp = array[index];
			array[index] = array[i];
			array[i] = temp;
		}
	}

	// Place exits in the maze
	private void placeExits() {
		int exitCount = 0;

		// Randomly choose positions for exits along the boundary
		while (exitCount < maxExits) {
			int side = rand.nextInt(4);
			int pos = rand.nextInt(size - 2) + 1;

			if (side == 0 && grid[1][pos] != 0 && grid[0][pos] == -1) { // Top side
				grid[0][pos] = -2; // Mark exit
				exitCount++;
			} else if (side == 1 && grid[pos][size - 1] == -1 && grid[pos][size - 1] - 1 != 0) { // Right side
				grid[pos][size - 1] = -2; // turn into exit
				exitCount++;
			} else if (side == 2 && grid[size - 1][pos] == -1 && grid[size - 2][pos] != 0) { // Bottom side
				grid[size - 1][pos] = -2;
				exitCount++;
			} else if (side == 3 && grid[pos][0] == -1 && grid[pos][0] - 1 != 0) { // Left side
				grid[pos][0] = -2;
			}
		}
	}

	// Display the maze as text
	public void displayMaze() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (grid[i][j] == -1) {
					System.out.print("x "); // boundary characters
				} else if (grid[i][j] == -2) { // exits
					System.out.print("o "); // exit character
				} else if (grid[i][j] == 0) {
					System.out.print("W "); // Normal walls as 'W'
				} else if (grid[i][j] == 1) {
					System.out.print(". "); // Paths as spaces
				}
			}
			System.out.println(); // Print new line after each row
		}
	}


	

	/*public static void main(String[] args) {
		int mazeSize = 31; // Set the maze size (must be odd)
		Maze maze = new Maze(mazeSize); // Create a new Maze object
		maze.displayMaze(); // Display the generated maze
	}
	 */
	public int[][] getGrid() {
		return grid;
	}

	public boolean isWalkable(int x, int y, int[][] grid) {

		if (x < 0 || y < 0 || x >= getSize() || y >= getSize()) {
			return false;
		}
		return grid[x][y] == 1;

	}
	
	public boolean isExit(int x, int y, int[][] grid) {

		
		return grid[x][y] == -2;

	}
	
	public String nearestExit(int[][] grid, int x, int y) {
		String direction = ""; 
		
		//loop through the maze, find the nearest exit, using distance formula based on current pos
		return direction;
		
	}

	public void setGrid(int[][] grid) {
		this.grid = grid;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getMaxExits() {
		return maxExits;
	}

	public void setMaxExits(int maxExits) {
		this.maxExits = maxExits;
	}

	public Random getRand() {
		return rand;
	}

	public void setRand(Random rand) {
		this.rand = rand;
	}

	@Override
	public String toString() {
		return "Maze [grid=" + Arrays.toString(grid) + ", size=" + size + ", rand=" + rand + "]";
	}

	public Fog getFog() {
		return fog;
	}

	public void setFog(Fog fog) {
		this.fog = fog;
	}

}
