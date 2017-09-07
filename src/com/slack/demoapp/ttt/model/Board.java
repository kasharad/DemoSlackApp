package com.slack.demoapp.ttt.model;

import com.slack.demoapp.ttt.exception.InternalError;

/**
 * 
 * Model for a ttt board
 * 
 */
public class Board {

	// Can be changed to an N sized board
	private static final int SIZE = 3;

	char board[][];

	public Board(String s) throws InternalError {
		board = new char[SIZE][SIZE];
		if (s.length() != SIZE * SIZE) {
			throw new InternalError("Incorrect board persisted" + s);
		}
		int r = 0;
		int c = 0;
		for (int i = 0; i < s.length(); i++) {
			r = i / SIZE;
			c = i % SIZE;
			board[r][c] = s.charAt(i);
		}
	}

	/**
	 * Checks if row r has all same char c
	 * 
	 * @param r
	 * @param c
	 * @return
	 */
	private boolean isSameRow(int r, char c) {
		boolean hasWon = true;
		// Check row
		for (int i = 0; i < SIZE; i++) {
			if (board[r][i] != c) {
				hasWon = false;
				break;
			}
		}
		return hasWon;
	}

	/**
	 * Checks if the col c has same char ch
	 * 
	 * @param c
	 * @param ch
	 * @return
	 */
	private boolean isSameCol(int c, char ch) {
		boolean hasWon = true;
		// Check row
		for (int i = 0; i < SIZE; i++) {
			if (board[i][c] != ch) {
				hasWon = false;
				break;
			}
		}
		return hasWon;
	}

	/**
	 * Checks if the Diagonal has same value
	 * 
	 * @return
	 */
	private boolean isSameDiagonal() {
		boolean hasWon = true;
		// Check row
		char ch = board[0][0];
		if (ch == '-') {
			return false;
		}
		for (int i = 0; i < SIZE; i++) {
			if (board[i][i] != ch) {
				hasWon = false;
				break;
			}
		}
		return hasWon;
	}

	/**
	 * s
	 * @return
	 */
	private boolean isSameReverseDiagonal() {
		boolean hasWon = true;
		// Check row
		char ch = board[0][SIZE - 1];
		if (ch == '-') {
			return false;
		}
		int j = 0;
		for (int i = SIZE - 1; i >= 0; i--) {
			if (board[j++][i] != ch) {
				hasWon = false;
				break;
			}
		}
		return hasWon;
	}

	/**
	 * Return true if the player with "theSeed" has won after placing at
	 * (currentRow, currentCol)
	 */
	public boolean hasWon(char c, int currentRow, int currentCol) {
		return isSameRow(currentRow, c) || isSameCol(currentCol, c)
				|| isSameDiagonal() || isSameReverseDiagonal();
	}

	/**
	 * Checks if board is a draw.
	 * @return
	 */
	public boolean isDraw() {
		for (int row = 0; row < SIZE; ++row) {
			for (int col = 0; col < SIZE; ++col) {
				if (board[row][col] == '-') {
					return false; // an empty cell found, not draw, exit
				}
			}
		}
		return true; // no empty cell, it's a draw
	}

	/**
	 * Used to persist in data store.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int row = 0; row < SIZE; ++row) {
			for (int col = 0; col < SIZE; ++col) {
				sb.append(board[row][col]);
			}
		}
		return sb.toString();
	}

	/**
	 * Gives back pretty print of the board.
	 * @return
	 */
	public String printBoard() {

		StringBuffer sb = new StringBuffer();
		for (int row = 0; row < SIZE; ++row) {
			for (int col = 0; col < SIZE; ++col) {
				printCell(board[row][col], sb); // print each of the cells
				if (col != SIZE - 1) {
					sb.append("|"); // print vertical partition
				}
			}
			sb.append("\n");
			if (row != SIZE - 1) {
				for (int i = 0; i < SIZE; i++)
					sb.append("----"); // print horizontal partition
				sb.append("\n");
			}
		}
		sb.append("\n");
		return sb.toString();
	}

	public char get(int r, int c) {
		return board[r][c];
	}

	public void set(char ch, int r, int c) {
		board[r][c] = ch;
	}

	public void printCell(char content, StringBuffer sb) {
		switch (content) {
		case 'O':
			sb.append(" O ");
			break;
		case 'X':
			sb.append(" X ");
			break;
		case '-':
		default:
			sb.append("    ");
			break;
		}
	}
}
