
package game1024;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.Stack;

/**
 * 
 * @author Mark Baker
 * @date 2/28/17
 * @version final
 *
 */

public class NumberGame implements NumberSlider {

	/** instance variable for the game board **/
	public int[][] gameBoard;

	/** instance variable for the winning value **/
	public int winningValue = 1024;

	/** instance variable for the Undo Stack **/
	public Stack<int[][]> undo = new Stack<int[][]>();

	/**
	 * Remove all numbered tiles from the board and place TWO non-zero values at
	 * random location
	 */
	@Override
	public void reset() {
		ArrayList<Cell> cellList = getNonEmptyTiles();

		// iterate through all cells making their values 0.
		for (Cell c : cellList) {
			int r = c.row;
			int col = c.column;
			gameBoard[r][col] = 0;
		}

		// Place two random values (2 or 4)
		placeRandomValue();
		placeRandomValue();
	}

	/**
	 * Reset the game logic to handle a board of a given dimension
	 *
	 * @param height
	 *            the number of rows in the board
	 * @param width
	 *            the number of columns in the board
	 * @param winningValue
	 *            the value that must appear on the board to win the game
	 * @throws IllegalArgumentException
	 *             when the winning value is not power of two or negative
	 */
	public void resizeBoard(int height, int width, int winV) {
		if (!isPowerOf2(winV) || winV < 0) {
			throw new IllegalArgumentException();
		} else {
			gameBoard = new int[height][width];
			this.winningValue = winV;
		}

	}

	/**
	 * Set the game board to the desired values given in the 2D array. This
	 * method should use nested loops to copy each element from the provided
	 * array to your own internal array. Do not just assign the entire array
	 * object to your internal array object. Otherwise, your internal array may
	 * get corrupted by the array used in the JUnit test file. This method is
	 * mainly used by the JUnit tester.
	 * 
	 * @param ref
	 */
	public void setValues(final int[][] ref) {
		int rows = ref.length;
		int cols = ref[0].length;

		// iterate through gameboard setting values equal to those in the ref
		// board
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				this.gameBoard[i][j] = ref[i][j];
			}
		}
	}

	/**
	 * Randomly generate a value that is either a 2 or a 4
	 * 
	 * @return value
	 */
	private int generateValue() {
		Random randGen = new Random();
		int value = (int) Math.pow(2, randGen.nextInt(2) + 1);
		return value;
	}

	/**
	 * Insert one random tile into an empty spot on the board.
	 *
	 * @return a Cell object with its row, column, and value attributes
	 *         initialized properly
	 *
	 * @throws IllegalStateException
	 *             when the board has no empty cell
	 */
	public Cell placeRandomValue() {
		Random randGen = new Random();
		int r, c, value;
		int rows = this.gameBoard.length;
		int cols = this.gameBoard[0].length;

		// Find random location that has a value of 0
		do {
			r = randGen.nextInt(rows);
			c = randGen.nextInt(cols);
		} while (this.gameBoard[r][c] != 0);

		// generate value for that spot
		value = generateValue();

		// set spot equal to value
		this.gameBoard[r][c] = value;

		// create new cell for this location
		Cell x = new Cell(r, c, value, false);

		// return cell
		return x;
	}

	/**
	 * Check if a cell exists given an row and column value to Return that cell
	 * if it exists, otherwise return null
	 * 
	 * @param x
	 * @param y
	 * @return Cell
	 */
	private Cell returnCellIfExists(int x, int y) {
		ArrayList<Cell> cellList = getNonEmptyTiles();
		for (Cell cell : cellList) {
			if (cell.row == y && cell.column == x) {
				return cell;
			}
		}

		return null;
	}

	/**
	 * Slide all the tiles in the board in the requested direction
	 * 
	 * @param dir
	 *            move direction of the tiles
	 *
	 * @return true when the board changes
	 */
	public boolean slide(SlideDirection dir) {
		ArrayList<Cell> cellList = getNonEmptyTiles();
		boolean slideOccured = false;
		boolean recentlyMerged = false;

		int[][] clone = copyBoard(gameBoard);
		// push current gameboard onto Stack
		undo.push(clone);

		if (dir == SlideDirection.UP) {

			// Sort cellList so that it slides starting at 0,0
			cellList.sort(new Comparator<Cell>() {

				@Override
				public int compare(Cell arg0, Cell arg1) {
					if (arg0.row < arg1.row)
						return -1;
					if (arg0.row > arg1.row)
						return +1;

					/* break the tie using column */
					if (arg0.column < arg1.column)
						return -1;
					if (arg0.column > arg1.column)
						return +1;

					return arg0.value - arg1.value;
				}
			});

			// for every cell in cell list, move as far up as possible
			for (Cell cell : cellList) {
				int initialCellRow = cell.row;
				int initialCellCol = cell.column;
				int initialCellVal = cell.value;
				int finalCellRow = cell.row;
				int finalCellCol = cell.column;
				int finalCellVal = cell.value;
				for (int i = initialCellRow - 1; i >= 0; i--) {

					Cell adjacentCell = returnCellIfExists(initialCellCol, i);

					// if adjacent cell is null, take over adjacent cell
					// location
					if (adjacentCell == null) {
						finalCellRow = i;
						slideOccured = true;
						recentlyMerged = false;

						// else...
					} else {
						// if adjacent cell value and initial cell value are
						// equal, merge them as one into adjacent cell location
						if (adjacentCell.value == initialCellVal && !adjacentCell.recentlyMerged) {
							finalCellVal = initialCellVal * 2;
							finalCellRow = i;
							slideOccured = true;
							recentlyMerged = true;

							Cell mergedCell = returnCellIfExists(finalCellCol, finalCellRow);
							if (recentlyMerged) {
								mergedCell.recentlyMerged = true;
							}

							break;
						} else { // if neither above is true, don't slide this
									// cell.
							recentlyMerged = false;
							break;
						}
					}

				}

				// update gameboard
				gameBoard[initialCellRow][initialCellCol] = 0;
				gameBoard[finalCellRow][finalCellCol] = finalCellVal;
			}
		} else if (dir == SlideDirection.DOWN) {
			cellList.sort(new Comparator<Cell>() {

				// sort cellList so sliding starts at the bottom right corner of
				// the board
				@Override
				public int compare(Cell arg0, Cell arg1) {
					if (arg0.row > arg1.row)
						return -1;
					if (arg0.row < arg1.row)
						return +1;

					/* break the tie using column */
					if (arg0.column > arg1.column)
						return -1;
					if (arg0.column < arg1.column)
						return +1;

					return arg0.value - arg1.value;
				}

			});

			// for every cell in cellList, slide as far down as possible.
			for (Cell cell : cellList) {
				int initialCellRow = cell.row;
				int initialCellCol = cell.column;
				int initialCellVal = cell.value;
				int finalCellRow = cell.row;
				int finalCellCol = cell.column;
				int finalCellVal = cell.value;
				for (int i = initialCellRow + 1; i < gameBoard.length; i++) {

					// check if adjacent cell exists
					Cell adjacentCell = returnCellIfExists(initialCellCol, i);

					// if adjacent cell is null, take over that location
					if (adjacentCell == null) {
						finalCellRow = i;
						slideOccured = true;
						recentlyMerged = false;
					} else {
						// if current value and adjacent value are equal, merge
						// and take over adjacent location
						if (adjacentCell.value == initialCellVal && !adjacentCell.recentlyMerged) {
							finalCellVal = initialCellVal + adjacentCell.value;
							finalCellRow = i;
							slideOccured = true;
							recentlyMerged = true;

							Cell mergedCell = returnCellIfExists(finalCellCol, finalCellRow);
							if (recentlyMerged) {
								mergedCell.recentlyMerged = true;
							}

							break;
						} else { // else don't slide current cell
							recentlyMerged = false;
							break;
						}
					}
				}

				// update gameboard
				gameBoard[initialCellRow][initialCellCol] = 0;
				gameBoard[finalCellRow][finalCellCol] = finalCellVal;
			}
		} else if (dir == SlideDirection.RIGHT) {

			// sort cellList so slide right starts at the top right corner
			cellList.sort(new Comparator<Cell>() {

				@Override
				public int compare(Cell arg0, Cell arg1) {
					if (arg0.row < arg1.row)
						return -1;
					if (arg0.row > arg1.row)
						return +1;

					/* break the tie using column */
					if (arg0.column > arg1.column)
						return -1;
					if (arg0.column < arg1.column)
						return +1;

					return arg0.value - arg1.value;
				}

			});

			// for every cell in cellList move as far right as possible
			for (Cell cell : cellList) {
				int initialCellRow = cell.row;
				int initialCellCol = cell.column;
				int initialCellVal = cell.value;
				int finalCellRow = cell.row;
				int finalCellCol = cell.column;
				int finalCellVal = cell.value;
				for (int i = initialCellCol + 1; i < gameBoard[0].length; i++) {

					// check if adjacent cell exists
					Cell adjacentCell = returnCellIfExists(i, initialCellRow);

					// if adjacent cell is null, take over that location
					if (adjacentCell == null) {
						finalCellCol = i;
						slideOccured = true;
						recentlyMerged = false;
					} else { // if adjacent cell value equals current cell
								// values, merge and take over adjacent location
						if (adjacentCell.value == initialCellVal && !adjacentCell.recentlyMerged) {
							finalCellVal = initialCellVal * 2;
							finalCellCol = i;
							slideOccured = true;
							recentlyMerged = true;

							Cell mergedCell = returnCellIfExists(finalCellCol, finalCellRow);
							if (recentlyMerged) {
								mergedCell.recentlyMerged = true;
							}

							break;
						} else { // else don't slide current cell
							recentlyMerged = false;
							break;
						}
					}
				}

				// update gameboard
				gameBoard[initialCellRow][initialCellCol] = 0;
				gameBoard[finalCellRow][finalCellCol] = finalCellVal;
			}
		} else if (dir == SlideDirection.LEFT) {
			// sort cellList starting top left.
			cellList.sort(new Comparator<Cell>() {

				@Override
				public int compare(Cell arg0, Cell arg1) {
					if (arg0.row > arg1.row)
						return -1;
					if (arg0.row < arg1.row)
						return +1;

					/* break the tie using column */
					if (arg0.column < arg1.column)
						return -1;
					if (arg0.column > arg1.column)
						return +1;

					return arg0.value - arg1.value;
				}
			});

			// for every cell in cellList, slide as far left as possible
			for (Cell cell : cellList) {
				int initialCellRow = cell.row;
				int initialCellCol = cell.column;
				int initialCellVal = cell.value;
				int finalCellRow = cell.row;
				int finalCellCol = cell.column;
				int finalCellVal = cell.value;
				for (int i = initialCellCol - 1; i >= 0; i--) {

					// check if adjacentCell exists
					Cell adjacentCell = returnCellIfExists(i, initialCellRow);

					// if adjacent cell doesnt exist, take over that location
					if (adjacentCell == null) {
						finalCellCol = i;
						slideOccured = true;
						recentlyMerged = false;
					} else { // if adjacent cell value equals current value,
								// merge into one cell in adjacent cell location
						if (adjacentCell.value == initialCellVal && !adjacentCell.recentlyMerged) {
							finalCellVal = initialCellVal * 2;
							finalCellCol = i;
							slideOccured = true;
							recentlyMerged = true;
							Cell mergedCell = returnCellIfExists(finalCellCol, finalCellRow);
							if (recentlyMerged) {
								mergedCell.recentlyMerged = true;
							}
							break;
						} else { // else don't slide current cell
							recentlyMerged = false;
							break;
						}
					}
				}
				// update gameboard
				gameBoard[initialCellRow][initialCellCol] = 0;
				gameBoard[finalCellRow][finalCellCol] = finalCellVal;

			}
		}

		// if slide occurred, place a new random value
		if (slideOccured) {
			placeRandomValue();
		}

		// return if a slide occurred or not
		return slideOccured;
	}

	/**
	 * Takes in a 2d array board and copies it
	 * 
	 * @param board
	 * @return copied board
	 */
	public int[][] copyBoard(int[][] board) {
		int[][] copy = new int[board.length][board[0].length];

		int rows = board.length;
		int cols = board[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				copy[i][j] = board[i][j];
			}
		}
		return copy;
	}

	/**
	 *
	 * @return an arraylist of Cells. Each cell holds the (row,column) and value
	 *         of a tile
	 */
	public ArrayList<Cell> getNonEmptyTiles() {
		ArrayList<Cell> cellList = new ArrayList<Cell>();

		int rows = gameBoard.length;
		int cols = gameBoard[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (gameBoard[i][j] != 0) {
					Cell c = new Cell(i, j, gameBoard[i][j], false);
					cellList.add(c);
				}
			}
		}

		return cellList;
	}

	/**
	 * Check if the user has any moves left
	 * 
	 * @param board
	 * @return boolean hasMoveLeft
	 */
	private boolean hasMoveLeft(int[][] board) {
		boolean hasMove = false;

		int rows = board.length;
		int cols = board[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (gameBoard[i][j] == 0) {
					return true;
				} else {
					if (slide(SlideDirection.UP)) {
						undo();
						return true;
					}
					if (slide(SlideDirection.DOWN)) {
						undo();
						return true;
					}
					if (slide(SlideDirection.LEFT)) {
						undo();
						return true;
					}
					if (slide(SlideDirection.RIGHT)) {
						undo();
						return true;
					}
				}

			}
		}

		return hasMove;
	}

	/**
	 * Checks to see if the user has won by searching for the winning value on
	 * the gameBoard
	 * 
	 * @param board
	 * @return if user has won
	 */
	private boolean hasWon(int[][] board) {
		boolean hasWon = false;

		int rows = board.length;
		int cols = board[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (gameBoard[i][j] == winningValue) {
					hasWon = true;
				}
			}
		}

		return hasWon;
	}

	/**
	 * Return the current state of the game
	 * 
	 * @return one of the possible values of GameStatus enum
	 */
	public GameStatus getStatus() {

		GameStatus x = GameStatus.IN_PROGRESS;

		if (hasMoveLeft(gameBoard)) {
			x = GameStatus.IN_PROGRESS;
		}

		if (!hasMoveLeft(gameBoard)) {
			x = GameStatus.USER_LOST;
		}

		if (hasWon(gameBoard)) {
			x = GameStatus.USER_WON;
		}

		return x;
	}

	/**
	 * Undo the most recent action, i.e. restore the board to its previous
	 * state. Calling this method multiple times will ultimately restore the gam
	 * to the very first initial state of the board holding two random values.
	 * Further attempt to undo beyond this state will throw an
	 * IllegalStateException.
	 *
	 * @throws IllegalStateException
	 *             when undo is not possible
	 */
	public void undo() {
		try {
			gameBoard = undo.pop();
		} catch (Exception IllegalStateException) {
			throw new IllegalStateException();
		}
	}

	/**
	 * Check if an integer is a power of 2
	 * 
	 * @param integer
	 *            n code taken from
	 *            http://www.java-fries.com/2014/05/fastest-way-to-check-if-a-number-is-power-of-2/
	 **/
	private static boolean isPowerOf2(int n) {
		if (n <= 0) {
			return false;
		}
		return (n & (n - 1)) == 0;
	}

	/**
	 * Flip the board
	 * 
	 * @param board
	 * @return new 2d board array
	 */
	private int[][] FlipBoard(int[][] board) {
		int[][] finalBoard = new int[board.length][board[0].length];

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				finalBoard[i][j] = board[j][i];
			}
		}

		return finalBoard;

	}
}
