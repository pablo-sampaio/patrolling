package search_library.sample_problems;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import search_library.AbstractSearchNode;

/**
 * A search node class for the puzzle called <b>Cilada</b>. In this puzzle, you
 * have a set of pieces of different types that you must use to cover completely
 * a board without overlapping pieces. <br>
 * <br>
 * The board and the pieces are divided in cells of different shapes (or
 * patterns), here represented as <b>O</b> (originally, a circle), <b>#</b> (a
 * square in the original) and <b>+</b> (a cross). Each piece only fits the
 * board if the cells that will be covered on the board match the corresponding
 * cells of the piece. <br>
 * <br>
 * The kinds of pieces are fixed. However, the puzzle admits many variations, by
 * using different <i>sets</i> of pieces (possibly with some repeated pieces).
 * 
 * @author Pablo A. Sampaio
 */
public class CiladaSearchNode extends AbstractSearchNode {
	private static final char[][] DEFAULT_BOARD = new char[][] { { 'O', 'O', '+', '+', '+', 'O', '#' },
			{ 'O', '#', '#', '#', '+', '#', '+' }, { '#', 'O', '+', 'O', 'O', '#', 'O' },
			{ '#', 'O', '+', '#', '+', '+', 'O' } };

	// these attributes are shared with descendants (because they don't change)
	private char[][] baseBoard;
	private List<Piece> pieceList;

	// these are not shared
	private char[][] usedBoard;
	private int remaining; // remaining free space in the (used) board
	private int nextPiece;

	/**
	 * Receives a string of upper case letters, where each letter uniquely
	 * identifies one type of piece. Repeated letters indicate repetitions of
	 * the same piece.
	 */
	public CiladaSearchNode(String pieceSet) {
		this(pieceSet, DEFAULT_BOARD);
	}

	/**
	 * Receives a string of upper case letters and the board, where each
	 * occurrence of a letter represents one piece of the type identified by
	 * that letter. The board is a arbitrary matrix of patterns <b>O</b>,
	 * <b>#</b> and <b>+</b>, to be covered by the given piece set.
	 */
	public CiladaSearchNode(String pieceSet, char[][] board) {
		baseBoard = board;
		usedBoard = new char[board.length][board[0].length]; // initialize all
																// with zeros
																// (ok)
		remaining = board.length * board[0].length;

		pieceList = new LinkedList<>();
		for (int i = 0; i < pieceSet.length(); i++) {
			Piece p = Piece.valueOf(pieceSet.substring(i, i + 1)); // char at
																	// [i]
			pieceList.add(p);
		}

		pieceList = Collections.unmodifiableList(pieceList);
		nextPiece = 0;
	}

	/**
	 * Create a copy.
	 */
	CiladaSearchNode(CiladaSearchNode father) {
		// setFatherNode(father); //not necessary, because it is never used

		baseBoard = father.baseBoard;
		usedBoard = new char[baseBoard.length][baseBoard[0].length];
		for (int i = 0; i < usedBoard.length; i++) {
			for (int j = 0; j < usedBoard[0].length; j++) {
				usedBoard[i][j] = father.usedBoard[i][j];
			}
		}
		remaining = father.remaining;

		pieceList = father.pieceList;
		nextPiece = father.nextPiece;
	}

	@Override
	public boolean isGoal() {
		return remaining == 0;
	}

	@Override
	public LinkedList<AbstractSearchNode> expand() {
		LinkedList<AbstractSearchNode> successors = new LinkedList<>();
		if (remaining == 0 || nextPiece >= pieceList.size()) {
			return successors;
		}

		Piece piece = pieceList.get(nextPiece);

		int maxLine = baseBoard.length;
		int maxCol = baseBoard[0].length;
		CiladaSearchNode state;

		for (int line = 0; line < maxLine; line++) {
			for (int col = 0; col < maxCol; col++) {
				// para cada orientacao, testar se casa e se tem espa�o livre no
				// tabuleiro
				for (PieceOrientation orientation : PieceOrientation.values()) {
					if (piece.matches(baseBoard, line, col, orientation)
							&& piece.fitsInFreeSpace(usedBoard, line, col, orientation)) {
						state = new CiladaSearchNode(this);
						piece.putInBoard(state.usedBoard, line, col, orientation);
						state.remaining -= piece.getUsedCells();
						state.nextPiece++;

						successors.add(state);
					}
				}
			}
		}

		return successors;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append('\n');
		for (int i = 0; i < baseBoard.length; i++) {
			for (int j = 0; j < baseBoard[0].length; j++) {
				if (usedBoard[i][j] == 0) {
					builder.append('_');
				} else {
					builder.append(usedBoard[i][j]);
				}
				builder.append('(');
				builder.append(baseBoard[i][j]);
				builder.append(')');
				builder.append(' ');
			}
			builder.append("|\n");
		}
		builder.append("--\n");
		return builder.toString();
	}

}

/**
 * This enum represents a Piece. There is a fixed set of pieces, represented by
 * letters.
 * 
 * @author Pablo A. Sampaio
 */
enum Piece {
	A("O+"), B("#+"), C("#O"), D("++"), E("##"), F("OO"), G("#O", "#"), H("+O", "#"), I("O#", "+"), J("++", "#"), K(
			"OO", "#"), L("O#", "O"), M("+#", "O"), N("#+", "O");

	char[][] cells;
	int usedCells;
	final int width, height; // in default orientation: DOWN

	/**
	 * Creates a piece, as a matrix of chars, where each different char is a
	 * different pattern in cell of the piece (to match with the same pattern of
	 * the board). The matrix is a given as a series of strings, from the
	 * uppermost, to the lowest. Space and non given chars (in shorter strings)
	 * are considered as empty cells.
	 */
	Piece(String... lines) {
		height = lines.length;
		width = lines[0].length();
		cells = new char[height][width];
		usedCells = 0;

		for (int l = 0; l < height; l++) {
			for (int c = 0; c < lines[l].length(); c++) {
				char pattern = lines[l].charAt(c);
				if (pattern != ' ') {
					cells[l][c] = pattern;
					usedCells++;
				}
			}
		}
	}

	public int getUsedCells() {
		return usedCells;
	}

	Pos translate(int line, int col, PieceOrientation d) {
		switch (d) {
		case DOWN: // default
			return new Pos(line, col);
		case UP:
			return new Pos(height - line - 1, width - col - 1);
		case CW:
			return new Pos(height - col - 1, line);
		case CounterCW:
			return new Pos(col, width - line - 1);
		}
		return null;
	}

	public char getCell(Pos p) {
		return cells[p.line][p.column];
	}

	public int width(PieceOrientation orientation) {
		switch (orientation) {
		case DOWN: // default
		case UP:
			return width;
		case CW:
		case CounterCW:
			return height;
		}
		return -1;
	}

	public int height(PieceOrientation orientation) {
		switch (orientation) {
		case DOWN: // default
		case UP:
			return height;
		case CW:
		case CounterCW:
			return width;
		}
		return -1;
	}

	/**
	 * Checks if the piece matches with the patterns on the board, considering
	 * the given position for the top-left part of the piece (the top left part
	 * of its bounding rectangle, indeed) of the piece rotated in the given
	 * orientation.
	 */
	public boolean matches(char[][] baseBoard, int startLine, int startColumn, PieceOrientation orientation) {
		Pos position;
		char cell;

		int orientedHeight = height(orientation);
		int orientedWidth = width(orientation);

		if ((startLine + orientedHeight - 1) >= baseBoard.length
				|| (startColumn + orientedWidth - 1) >= baseBoard[0].length) {
			return false;
		}

		for (int ln = 0; ln < orientedHeight; ln++) {
			for (int col = 0; col < orientedWidth; col++) {
				position = translate(ln, col, orientation);
				cell = getCell(position);
				if (cell != 0 && cell != baseBoard[startLine + ln][startColumn + col]) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Checks if there is free space for the piece on the board, considering the
	 * given position for the top-left part of the piece (the top left part of
	 * its bounding rectangle, indeed) of the piece rotated in the given
	 * orientation.
	 */
	public boolean fitsInFreeSpace(char[][] usedBoard, int startLine, int startColumn, PieceOrientation orientation) {
		Pos position;
		char cell;

		int orientedHeight = height(orientation);
		int orientedWidth = width(orientation);

		if ((startLine + orientedHeight - 1) >= usedBoard.length
				|| (startColumn + orientedWidth - 1) >= usedBoard[0].length) {
			return false;
		}

		for (int ln = 0; ln < orientedHeight; ln++) {
			for (int col = 0; col < orientedWidth; col++) {
				position = translate(ln, col, orientation);
				cell = getCell(position);
				if (cell != 0 && usedBoard[startLine + ln][startColumn + col] != 0) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Puts (writes) the piece in the board.
	 */
	public void putInBoard(char[][] usedBoard, int startLine, int startColumn, PieceOrientation orientation) {
		Pos position;
		char cell;

		int orientedHeight = height(orientation);
		int orientedWidth = width(orientation);
		char pieceLabel = toString().charAt(0);

		for (int ln = 0; ln < orientedHeight; ln++) {
			for (int col = 0; col < orientedWidth; col++) {
				position = translate(ln, col, orientation);
				cell = getCell(position);
				if (cell != 0) {
					usedBoard[startLine + ln][startColumn + col] = pieceLabel;
				}
			}
		}

	}

	public String toString(PieceOrientation orientation) {
		StringBuilder str = new StringBuilder();
		for (int ln = 0; ln < height(orientation); ln++) {
			for (int col = 0; col < width(orientation); col++) {
				Pos p = translate(ln, col, orientation);
				str.append(cells[p.line][p.column]);
			}
			str.append('\n');
		}
		return str.toString();
	}

}

enum PieceOrientation {
	UP, DOWN, // default case
	CW, // clockwise
	CounterCW
};

class Pos {
	final int line;
	final int column;

	public Pos(int a, int b) {
		line = a;
		column = b;
	}
}
