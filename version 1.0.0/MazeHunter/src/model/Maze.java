/*
 * This is the model class that runs the heart of the game, it generates the maze using 
 * recursive backtracking to carve out paths, dynamically placing exits based on size.
 */

package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class Maze {
	private int[][] grid;
	private int size;
	private int maxExits;
	private Random rand;
	private Fog fog;

	public Maze(int size) {
		this.size = size;
		this.grid = new int[size][size];
		this.rand = new Random();
		this.maxExits = calculateMaxExits(size);
		initializeGrid();
		carvePath(1, 1); // Start carving from (1, 1)
		placeExits();
	}

	// Calculate the maximum number of exits based on size
	private int calculateMaxExits(int size) {
		if (size <= 5)
			return 1;
		return Math.round(size / 13);
	}

	// Initialize the grid
	private void initializeGrid() {
		// Set all cells to walls
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				grid[i][j] = 0;
			}
		}

		for (int i = 0; i < size; i++) {
			grid[0][i] = -1;
			grid[size - 1][i] = -1;
			grid[i][0] = -1;
			grid[i][size - 1] = -1;
		}
	}

	// Recursive backtracking to carve out the paths
	private void carvePath(int x, int y) {
		grid[x][y] = 1; // Mark current position as a path

		// Define possible directions (right, down, left, up)
		int[][] directions = { { 0, 2 }, { 2, 0 }, { 0, -2 }, { -2, 0 } }; // Move in two steps
		shuffle(directions); 

		for (int[] dir : directions) {
			int nx = x + dir[0]; 
			int ny = y + dir[1];

			if (nx > 0 && ny > 0 && nx < size - 1 && ny < size - 1 && grid[nx][ny] == 0) {
				// Remove the wall between the current cell and the new cell
				grid[x + dir[0] / 2][y + dir[1] / 2] = 1; 
				carvePath(nx, ny); // Recursively carve the path
			}
		}
	}

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

			if (side == 0 && grid[1][pos] != 0 && grid[0][pos] == -1) { 
				grid[0][pos] = -2; // Mark exit
				exitCount++;
			} else if (side == 1 && grid[pos][size - 1] == -1 && grid[pos][size - 1] - 1 != 0) { 
				grid[pos][size - 1] = -2; // turn into exit
				exitCount++;
			} else if (side == 2 && grid[size - 1][pos] == -1 && grid[size - 2][pos] != 0) { 
				grid[size - 1][pos] = -2;
				exitCount++;
			} else if (side == 3 && grid[pos][0] == -1 && grid[pos][0] - 1 != 0) { 
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
		}
	}


	public int[][] getGrid() {
		return grid;
	}

	public boolean isPath(int x, int y, int[][] grid) {
		if (x < 0 || y < 0 || x >= grid.length || y >= grid[0].length) {
			return false;
		}
		return grid[x][y] == 1; // Only returns true for actual paths (1)
	}

	public boolean isExit(int x, int y, int[][] grid) {
		return grid[x][y] == -2;

	}

	/*
	 * Returns the string of a direction you must step in to find the nearest exit in the game
	 * @param grid, grid used to calculate
	 * @param startx, y positions to start at
	 */
	public String nearestExit(int[][] grid, int startX, int startY) {
		int n = grid.length;
		boolean[][] visited = new boolean[n][n];

		// Directions: right, down, left, up
		int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
		String[] dirNames = { "RIGHT", "DOWN", "LEFT", "UP" };

		Queue<Node> queue = new LinkedList<>();

		// Store the first move direction along with each node
		for (int i = 0; i < directions.length; i++) {
			int newX = startX + directions[i][0];
			int newY = startY + directions[i][1];

			// if the first move is valid, add it to our visited areas
			if (isValid(newX, newY, grid, visited)) {
				queue.add(new Node(newX, newY, dirNames[i]));
				visited[newX][newY] = true;
			}
		}

		// until the queue is empty (of all movements, continue)
		while (!queue.isEmpty()) {
			Node current = queue.poll();

			if (grid[current.x][current.y] == -2) {
				return current.initialDirection;
			}

			for (int i = 0; i < directions.length; i++) {
				int newX = current.x + directions[i][0];
				int newY = current.y + directions[i][1];

				if (isValid(newX, newY, grid, visited)) {
					queue.add(new Node(newX, newY, current.initialDirection));
					visited[newX][newY] = true;
				}
			}
		}

		return "NO EXIT FOUND";
	}

	private boolean isValid(int x, int y, int[][] grid, boolean[][] visited) {
		int n = grid.length;
		return x >= 0 && y >= 0 && x < n && y < n && (grid[x][y] == 1 || grid[x][y] == -2) && !visited[x][y];
	}

	private class Node {
		int x, y;
		String initialDirection;

		Node(int x, int y, String direction) {
			this.x = x;
			this.y = y;
			this.initialDirection = direction;
		}
	}

	public boolean isDeadEnd(int x, int y, int[][] grid, int skipDirIndex) {
		int n = grid.length;

		int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

		int forwardExitCount = 0;
		int[] singlePathStart = null;

		for (int i = 0; i < 4; i++) {
			// Skip the direction the player came from.
			if (i == skipDirIndex) {
				continue;
			}

			int nx = x + directions[i][0];
			int ny = y + directions[i][1];

			if (isPath(nx, ny, grid)) {
				forwardExitCount++;
				singlePathStart = new int[] { nx, ny };
			}
		}

		if (forwardExitCount == 0) {
			return true;
		}

		if (forwardExitCount > 1) {
			return false;
		}

		Stack<int[]> stack = new Stack<>(); // lifo
		boolean[][] visited = new boolean[grid.length][grid.length];

		visited[x][y] = true;
		visited[singlePathStart[0]][singlePathStart[1]] = true;
		stack.push(singlePathStart); // Push the start of the path onto the stack.

		while (!stack.isEmpty()) {
			int[] current = stack.pop(); 
			int cx = current[0];
			int cy = current[1];

			// Find ALL unvisited neighbors from this point.
			List<int[]> unvisitedNeighbors = new ArrayList<>();
			for (int i = 0; i < 4; i++) {
				int nx = cx + directions[i][0];
				int ny = cy + directions[i][1];
				if (isPath(nx, ny, grid) && !visited[nx][ny]) {
					unvisitedNeighbors.add(new int[] { nx, ny });
				}
			}

			if (unvisitedNeighbors.size() > 1) {
				return false;
			}

			// Add the next part of the path to the stack to continue the walk.
			for (int[] neighbor : unvisitedNeighbors) {
				visited[neighbor[0]][neighbor[1]] = true;
				stack.push(neighbor);
			}
		}

		return true;
	}

	public int getDistance(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
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
