package search_library.sample_problems;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import search_library.AbstractSearchNode;

/**
 * 
 * This class represents a search node for the 8-puzzle problem. <br>
 * The 8-puzzle is a sliding puzzle that consists of a frame of numbered square
 * tiles in random order with one tile missing. <br>
 * The object of the puzzle is to place the tiles in order by making sliding
 * moves that use the empty space.
 * 
 * @author Alison Carrera
 *
 */

public class The8Puzzle extends AbstractSearchNode {

	// Actual matrix
	private int matrix[][];
	// Object Matrix
	private int matrixDestiny[][];
	// List of expanded nodes
	private LinkedList<AbstractSearchNode> expanded;

	/**
	 * Constructor of the 8-puzzle problem.
	 * 
	 * @param matrix
	 *            Actual matrix
	 * @param matrixDestiny
	 *            Object Matrix
	 */
	public The8Puzzle(int matrix[][], int matrixDestiny[][]) {

		this.matrix = matrix;
		this.matrixDestiny = matrixDestiny;

	}

	public int[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(int[][] matrix) {
		this.matrix = matrix;
	}

	public int[][] getMatrixDestiny() {
		return matrixDestiny;
	}

	public void setMatrixDestiny(int[][] matrixDestiny) {
		this.matrixDestiny = matrixDestiny;
	}

	public LinkedList<AbstractSearchNode> getExpanded() {
		return expanded;
	}

	public void setExpanded(LinkedList<AbstractSearchNode> expanded) {
		this.expanded = expanded;
	}

	@Override
	public boolean isGoal() {
		return Arrays.deepEquals(matrix, matrixDestiny);
	}

	@Override
	public LinkedList<AbstractSearchNode> expand() {

		expanded = new LinkedList<AbstractSearchNode>();

		LinkedList<Integer> sucessors = detectItems();

		for (int i = 2; i < sucessors.size(); i += 2) {

			int[][] matrixTemp = deepCopy(this.matrix);

			this.moveItems(matrixTemp, sucessors.get(i), sucessors.get(i + 1), sucessors.get(0), sucessors.get(1));

			The8Puzzle puzzle = new The8Puzzle(matrixTemp, matrixDestiny);

			puzzle.setFatherNode(this);

			boolean control = false;

			The8Puzzle nodeTemp = this;
			if (this.getDepth() > 0) {
				while (nodeTemp.getFatherNode() != null) {
					nodeTemp = (The8Puzzle) nodeTemp.getFatherNode();

					LinkedList<AbstractSearchNode> expanded2 = nodeTemp.getExpanded();

					for (int j = 0; j < expanded2.size(); j++) {
						if (Arrays.deepEquals(puzzle.getMatrix(), ((The8Puzzle) expanded2.get(j)).getMatrix())
								|| Arrays.deepEquals(puzzle.getMatrix(), ((The8Puzzle) nodeTemp).getMatrix())) {
							control = true;
						}

					}

				}
			}

			// number of tiles that are not in the correct place (not counting
			// the blank)
			puzzle.setCurrentCost(tilesWrongPosition(matrixTemp, matrixDestiny));

			// Heuristica h(x), quantidade de passos para o estado inicial +
			// g(x), quantidade de passos para o estado objetivo
			puzzle.setFutureCostEstimate((this.getDistance(matrixTemp, nodeTemp.getMatrix()) + this.getDistance(
					matrixTemp, matrixDestiny)));

			if (!control) {
				expanded.add(puzzle);
			}

		}

		return expanded;
	}

	/**
	 * Number of tiles that are not in the correct place (not counting the
	 * blank)
	 * 
	 * @param matrix
	 *            Actual matrix
	 * @param matrixDestiny
	 *            Object matrix
	 * @return Number of tiles that are not in the correct place
	 */
	private int tilesWrongPosition(int[][] matrix, int[][] matrixDestiny) {
		int count = 0;

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {

				if (matrix[i][j] != matrixDestiny[i][j]) {
					count++;
				}

			}
		}

		return count;
	}

	/**
	 * Calculate Manhattan distance.
	 * 
	 * @param matrix
	 *            Actual Matrix
	 * @param matrixDestiny
	 *            Object Matrix
	 * @return Manhattan distance cost.
	 */
	private int getDistance(int[][] matrix, int[][] matrixDestiny) {

		LinkedList<MatrixHelper> helper = new LinkedList<MatrixHelper>();
		LinkedList<MatrixHelper> helperDestiny = new LinkedList<MatrixHelper>();

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {

				int value = matrix[i][j];

				MatrixHelper h = new MatrixHelper();
				h.value = value;
				h.i = i + 1;
				h.j = j + 1;

				helper.add(h);

			}
		}

		Collections.sort(helper, new Comparator<MatrixHelper>() {
			@Override
			public int compare(MatrixHelper h1, MatrixHelper h2) {
				return Integer.valueOf(h1.value).compareTo(h2.value);
			}
		});

		for (int i = 0; i < matrixDestiny.length; i++) {
			for (int j = 0; j < matrixDestiny[0].length; j++) {

				int value = matrixDestiny[i][j];

				MatrixHelper h = new MatrixHelper();
				h.value = value;
				h.i = i + 1;
				h.j = j + 1;

				helperDestiny.add(h);

			}
		}

		Collections.sort(helperDestiny, new Comparator<MatrixHelper>() {
			@Override
			public int compare(MatrixHelper h1, MatrixHelper h2) {
				return Integer.valueOf(h1.value).compareTo(h2.value);
			}
		});

		int cost = 0;

		for (int i = 0; i < helper.size(); i++) {

			int value1 = Math.abs(helper.get(i).i - helperDestiny.get(i).i);

			int value2 = Math.abs(helper.get(i).j - helperDestiny.get(i).j);

			cost += value1 + value2;
		}

		return cost;
	}

	/**
	 * Make copy of original matrix.
	 * 
	 * @param original
	 *            Original matrix
	 * @return Same matrix but in other memory position.
	 */
	public static int[][] deepCopy(int[][] original) {
		if (original == null) {
			return null;
		}

		final int[][] result = new int[original.length][];
		for (int i = 0; i < original.length; i++) {
			result[i] = Arrays.copyOf(original[i], original[i].length);

		}
		return result;
	}

	@Override
	public String toString() {

		String result = "";
		result += "\n";
		for (int i = 0; i < this.matrix.length; i++) {

			for (int j = 0; j < this.matrix[0].length; j++) {
				result += matrix[i][j] + " ";
			}

			result += "\n";
		}
		return result;
	}

	/**
	 * Generate final matrix successor.
	 * 
	 * @param matrix
	 *            Actual matrix.
	 * @param iOrigin
	 *            Intercalate i position from origin
	 * @param jOrigin
	 *            Intercalate j position from origin
	 * @param iDestiny
	 *            Intercalate i position from destiny
	 * @param jDestiny
	 *            Intercalate j position from destiny
	 */
	private void moveItems(int matrix[][], int iOrigin, int jOrigin, int iDestiny, int jDestiny) {
		int i1 = matrix[iOrigin][jOrigin];
		int i2 = matrix[iDestiny][jDestiny];

		matrix[iOrigin][jOrigin] = i2;
		matrix[iDestiny][jDestiny] = i1;
	}

	/**
	 * Generate the successors of a matrix.
	 * 
	 * @return Successors of a matrix.
	 */
	private LinkedList<Integer> detectItems() {

		LinkedList<Integer> sucessors = new LinkedList<Integer>();

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {

				if (matrix[i][j] == 0) {
					sucessors.add(i);
					sucessors.add(j);

					if ((i - 1) >= 0) {
						sucessors.add(i - 1);
						sucessors.add(j);
					}

					if ((i + 1) <= 2) {
						sucessors.add(i + 1);
						sucessors.add(j);
					}

					if ((j - 1) >= 0) {
						sucessors.add(i);
						sucessors.add(j - 1);
					}

					if ((j + 1) <= 2) {
						sucessors.add(i);
						sucessors.add(j + 1);
					}

					break;
				}

			}
		}

		return sucessors;
	}

}

/**
 * This is a helper class where is used for distance calculation. A matrix
 * [i][j] has a value j.
 * 
 * @author Alison Carrera
 *
 */
class MatrixHelper {
	int value;
	int i;
	int j;
}