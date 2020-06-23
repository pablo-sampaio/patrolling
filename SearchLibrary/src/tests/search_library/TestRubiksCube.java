package tests.search_library;

import java.util.HashSet;
import java.util.LinkedList;

import search_library.methods.IterativeDeepeningSearch;
import search_library.methods.UniformCostSearch;
import search_library.sample_problems.RubiksCube;
import search_library.sample_problems.RubiksCubeSearchNode;
import search_library.sample_problems.RubiksCube.Actions;

public class TestRubiksCube {

	public static void main(String[] args) {
		// testCube();
		testSearch();
	}

	public static void testSearch() {
		LinkedList<Actions> actions = new LinkedList<Actions>();

		actions.add(Actions.SPIN_UPPER_LINE_LEFT);
		actions.add(Actions.SPIN_UPPER_LINE_RIGHT);
		actions.add(Actions.SPIN_LOWER_LINE_LEFT);
		actions.add(Actions.SPIN_LOWER_LINE_RIGHT);
		actions.add(Actions.SPIN_LEFT_COLUMN_DOWN);
		actions.add(Actions.SPIN_LEFT_COLUMN_UP);
		actions.add(Actions.SPIN_RIGHT_COLUMN_DOWN);
		actions.add(Actions.SPIN_RIGHT_COLUMN_UP);
		actions.add(Actions.SPIN_FRONT_LINE_CW);
		actions.add(Actions.SPIN_FRONT_LINE_COUNTER_CW);
		actions.add(Actions.SPIN_REAR_LINE_CW);
		actions.add(Actions.SPIN_REAR_LINE_COUNTER_CW);

		RubiksCube startState = new RubiksCube();

		startState.setAllFaces(new char[][][] {
				// UP:
				{ { 'y', 'y', 'b' }, { 'g', 'g', 'w' }, { 'g', 'g', 'w' } },
				// LEFT:
				{ { 'b', 'o', 'o' }, { 'b', 'y', 'g' }, { 'r', 'y', 'g' } },
				// FRONT:
				{ { 'y', 'y', 'g' }, { 'r', 'r', 'g' }, { 'r', 'r', 'w' } },
				// RIGHT:
				{ { 'r', 'r', 'o' }, { 'w', 'w', 'o' }, { 'b', 'b', 'o' } },
				// DOWN:
				{ { 'y', 'y', 'r' }, { 'b', 'b', 'r' }, { 'b', 'w', 'g' } },
				// REAR:
				{ { 'y', 'b', 'w' }, { 'o', 'o', 'w' }, { 'o', 'o', 'w' } }, });

		// //UP:
		// {{'g', 'g', 'r'},
		// {'y', 'y', 'y'},
		// {'y', 'y', 'y'}
		// },
		// //LEFT:
		// {{'w', 'r', 'r'},
		// {'b', 'r', 'r'},
		// {'b', 'r', 'r'}
		// },
		// //FRONT:
		// {{'g', 'g', 'g'},
		// {'w', 'g', 'g'},
		// {'w', 'g', 'g'}
		// },
		// //RIGHT:
		// {{'o', 'o', 'y'},
		// {'o', 'o', 'o'},
		// {'o', 'o', 'o'}
		// },
		// //DOWN:
		// {{'b', 'w', 'w'},
		// {'b', 'w', 'w'},
		// {'o', 'w', 'w'}
		// },
		// //REAR:
		// {{'y', 'b', 'b'},
		// {'y', 'b', 'b'},
		// {'r', 'r', 'b'}
		// },
		// });

		RubiksCubeSearchNode searchNode = new RubiksCubeSearchNode(startState, new HashSet(actions));
		// System.out.println(searchNode);

		// UniformCostSearch searcher = new UniformCostSearch();
		// BreadthFirstSearch searcher = new BreadthFirstSearch();
		IterativeDeepeningSearch searcher = new IterativeDeepeningSearch();

		RubiksCubeSearchNode result = (RubiksCubeSearchNode) searcher.search(searchNode);

		System.out.println(result);
	}

	public static void testCube() {
		RubiksCube cube1 = new RubiksCube();
		RubiksCube cube2 = new RubiksCube(cube1);

		cube2.setAllFaces(new char[][][] {
				// UP:
				{ { 'b', 'w', 'b' }, { 'b', 'w', 'b' }, { 'g', 'w', 'g' } },
				// LEFT:
				{ { 'r', 'r', 'w' }, { 'y', 'b', 'y' }, { 'y', 'g', 'o' } },
				// FRONT:
				{ { 'o', 'b', 'o' }, { 'r', 'o', 'o' }, { 'y', 'y', 'y' } },
				// RIGHT:
				{ { 'y', 'o', 'o' }, { 'w', 'g', 'w' }, { 'r', 'g', 'w' } },
				// DOWN:
				{ { 'b', 'b', 'b' }, { 'o', 'y', 'y' }, { 'g', 'g', 'g' } },
				// REAR:
				{ { 'r', 'r', 'r' }, { 'o', 'r', 'r' }, { 'w', 'g', 'w' } }, });

		// System.out.println(cube1);
		// System.out.println("Solution? " + cube1.isSolution());
		// System.out.println();

		System.out.println(cube2);
		System.out.println("Solution? " + cube2.isSolution());
		System.out.println();

		// System.out.println(" ======= ROTATE UP =======");
		// cube2.moveUp();
		// System.out.println(cube2);
		// System.out.println("Solution? " + cube2.isSolution());
		//
		// System.out.println("======= ROTATE DOWN =======");
		// cube2.moveDown();
		// System.out.println(cube2);
		// System.out.println("Solution? " + cube2.isSolution());

		// System.out.println(" ======= ROTATE LEFT =======");
		// cube2.moveLeft();
		// System.out.println(cube2);
		// System.out.println("Solution? " + cube2.isSolution());
		//
		// System.out.println("======= ROTATE RIGHT =======");
		// cube2.moveRight();
		// System.out.println(cube2);
		// System.out.println("Solution? " + cube2.isSolution());

		// System.out.println(" ======= ROTATE CW =======");
		// cube2.moveClockwise();
		// System.out.println(cube2);
		// System.out.println("Solution? " + cube2.isSolution());
		//
		// System.out.println(" ======= ROTATE Counter CW =======");
		// cube2.moveCounterClockwise();
		// System.out.println(cube2);
		// System.out.println("Solution? " + cube2.isSolution());

		// System.out.println(" ======= SPIN UPPER LINE - left =======");
		// cube2.spinUpperLineLeft();
		// System.out.println(cube2);
		// System.out.println("Solution? " + cube2.isSolution());
		// System.out.println();
		//
		// System.out.println(" ======= SPIN UPPER LINE - right =======");
		// cube2.spinUpperLineRight();
		// System.out.println(cube2);
		// System.out.println("Solution? " + cube2.isSolution());
		// System.out.println();

		// System.out.println(" ======= SPIN LOWER LINE - left =======");
		// cube2.spinLowerLineLeft();
		// System.out.println(cube2);
		// System.out.println("Solution? " + cube2.isSolution());
		// System.out.println();
		//
		// System.out.println(" ======= SPIN LOWER LINE - right =======");
		// cube2.spinLowerLineRight();
		// System.out.println(cube2);
		// System.out.println("Solution? " + cube2.isSolution());
		// System.out.println();

		// System.out.println(" ======= SPIN LEFT COLUMN - up =======");
		// cube2.spinLeftColumnUp();;
		// System.out.println(cube2);
		// System.out.println("Solution? " + cube2.isSolution());
		// System.out.println();
		//
		// System.out.println(" ======= SPIN LEFT COLUMN - down =======");
		// cube2.spinLeftColumnDown();
		// System.out.println(cube2);
		// System.out.println("Solution? " + cube2.isSolution());
		// System.out.println();

		System.out.println(" ======= SPIN RIGHT COLUMN - up =======");
		cube2.spinRightColumnUp();
		System.out.println(cube2);
		System.out.println("Solution? " + cube2.isSolution());
		System.out.println();

		System.out.println(" ======= SPIN RIGHT COLUMN - down =======");
		cube2.spinRightColumnDown();
		System.out.println(cube2);
		System.out.println("Solution? " + cube2.isSolution());
		System.out.println();

		// System.out.println(" ======= SPIN FRONT LINE - CW =======");
		// cube2.spinFrontLineClockwise();
		// System.out.println(cube2);
		// System.out.println("Solution? " + cube2.isSolution());
		// System.out.println();
		//
		// System.out.println(" ======= SPIN FRONT LINE - Counter CW =======");
		// cube2.spinFrontLineCounterClockwise();
		// System.out.println(cube2);
		// System.out.println("Solution? " + cube2.isSolution());
		// System.out.println();

		// System.out.println(" ======= SPIN REAR LINE - CW =======");
		// cube2.spinRearLineClockwise();
		// System.out.println(cube2);
		// System.out.println("Solution? " + cube2.isSolution());
		// System.out.println();
		//
		// System.out.println(" ======= SPIN REAR LINE - Counter CW =======");
		// cube2.spinRearLineCounterClockwise();
		// System.out.println(cube2);
		// System.out.println("Solution? " + cube2.isSolution());
		// System.out.println();
	}

}
