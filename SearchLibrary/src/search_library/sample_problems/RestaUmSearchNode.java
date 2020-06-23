package search_library.sample_problems;

import java.util.LinkedList;

import search_library.AbstractSearchNode;

/**
 * A search node class for the puzzle called <b>"Resta Um"</b>. In this puzzle, you
 * have a set of homogeneous pieces in a board. Your only action is to use one piece A
 * to remove an adjacent piece B. This can be done only if there is also an adjacent free 
 * cell forming a vertical or horizontal alignment <b>"A - B - free cell"</b>. In this 
 * case, you move piece A to the free cell, then remove B.<br>
 * <br>
 * In the standard initial board, the cells are in shape of a cross, and they start all 
 * covered with pieces, except by the central cell. There are variations for the initial 
 * board however.
 * 
 * @author Pablo A. Sampaio
 */
public class RestaUmSearchNode extends AbstractSearchNode {
	// 	  +		- piece
	// 	  #		- not a cell
	// <space> 	- free cell
	private static final char[][] DEFAULT_BOARD = new char[][] { 
		    { '#', '#', '+', '+', '+', '#', '#' },
			{ '#', '#', '+', '+', '+', '#', '#' }, 
			{ '+', '+', '+', '+', '+', '+', '+' },
			{ '+', '+', '+', ' ', '+', '+', '+' }, 
			{ '+', '+', '+', '+', '+', '+', '+' },
			{ '#', '#', '+', '+', '+', '#', '#' },
			{ '#', '#', '+', '+', '+', '#', '#' },};

	private char[][] usedBoard;
	private int remaining; // remaining pieces in the board
	private String lastAction;

	public RestaUmSearchNode() {
		this(DEFAULT_BOARD);
	}

	public RestaUmSearchNode(char[][] board) {
		usedBoard = new char[board.length][board[0].length];
		remaining = 0;
		lastAction = null; //initial state

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				usedBoard[i][j] = board[i][j];
				if (board[i][j] == '+') {
					remaining ++;
				}
			}
		}
		setCurrentCost(getDepth());
	}

	/**
	 * Creates a copy as a child state.
	 */
	RestaUmSearchNode(RestaUmSearchNode father, String action) {
		char[][] fatherBoard = father.usedBoard;
		usedBoard = new char[fatherBoard.length][fatherBoard[0].length];
		lastAction = action;
		for (int i = 0; i < usedBoard.length; i++) {
			for (int j = 0; j < usedBoard[0].length; j++) {
				usedBoard[i][j] = father.usedBoard[i][j];
			}
		}
		remaining = father.remaining;
		setFatherNode(father);
		setCurrentCost(getDepth());
	}

	@Override
	public boolean isGoal() {
		return remaining == 1;
	}

	@Override
	public LinkedList<AbstractSearchNode> expand() {
		LinkedList<AbstractSearchNode> successors = new LinkedList<>();
		if (remaining == 1) {
			return successors;
		}
		
		RestaUmSearchNode state;
		
		for (int line = 0; line < usedBoard.length; line++) {
			for (int col = 0; col < usedBoard[0].length; col++) {
				
				if (usedBoard[line][col] == '+') {
					// para cada orientacao, testa se pode jogar
					if (canPlayDown(line, col)) {
						state = new RestaUmSearchNode(this, "down");
						state.usedBoard[line][col] = ' ';
						state.usedBoard[line+1][col] = ' ';
						state.remaining --;
						state.usedBoard[line+2][col] = '+';
						successors.add(state);
					} 
					if (canPlayUp(line, col)) {
						state = new RestaUmSearchNode(this, "up");
						state.usedBoard[line][col] = ' ';
						state.usedBoard[line-1][col] = ' ';
						state.remaining --;
						state.usedBoard[line-2][col] = '+';
						successors.add(state);
					} 
					if (canPlayLeft(line, col)) {
						state = new RestaUmSearchNode(this, "left");
						state.usedBoard[line][col] = ' ';
						state.usedBoard[line][col-1] = ' ';
						state.remaining --;
						state.usedBoard[line][col-2] = '+';
						successors.add(state);
					} 
					if (canPlayRight(line, col)) {
						state = new RestaUmSearchNode(this, "right");
						state.usedBoard[line][col] = ' ';
						state.usedBoard[line][col+1] = ' ';
						state.remaining --;
						state.usedBoard[line][col+2] = '+';
						successors.add(state);
					}
				}
			
			} //for
		} //for

		return successors;
	}
	
	boolean canPlayUp(int line, int col) {
		if (line < 2)
			return false;
		int posB = usedBoard[line-1][col];
		int posC = usedBoard[line-2][col];
		return (posB == '+') && (posC == ' ' || posC == '_');
	}
	
	boolean canPlayDown(int line, int col) {
		if (line >= usedBoard.length - 2)
			return false;
		int posB = usedBoard[line+1][col];
		int posC = usedBoard[line+2][col];
		return (posB == '+') && (posC == ' ' || posC == '_');		
	}

	boolean canPlayRight(int line, int col) {
		if (col >= usedBoard[0].length - 2)
			return false;
		int posB = usedBoard[line][col+1];
		int posC = usedBoard[line][col+2];
		return (posB == '+') && (posC == ' ' || posC == '_');		
	}

	boolean canPlayLeft(int line, int col) {
		if (col < 2)
			return false;
		int posB = usedBoard[line][col-1];
		int posC = usedBoard[line][col-2];
		return (posB == '+') && (posC == ' ' || posC == '_');		
	}
	
	@Override
	public boolean equals(Object o) {
		//TODO !!!!!
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (lastAction != null) {
			builder.append('\n');
			builder.append(lastAction);
		}
		builder.append('\n');
		for (int i = 0; i < usedBoard.length; i++) {
			for (int j = 0; j < usedBoard[0].length; j++) {
				if (usedBoard[i][j] == '_' || usedBoard[i][j] == ' ') {
					builder.append('_');
				} else if (usedBoard[i][j] == '#') {
					builder.append(' ');
				} else {
					builder.append(usedBoard[i][j]);
				}
			}
			builder.append("\n");
		}
		builder.append("\n");
		return builder.toString();
	}

}


