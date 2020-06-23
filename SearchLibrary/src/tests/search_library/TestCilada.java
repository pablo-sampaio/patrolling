package tests.search_library;

import java.util.List;

import search_library.AbstractSearchNode;
import search_library.methods.DepthFirstSearch;
import search_library.methods.SMAStarSearch;
import search_library.sample_problems.CiladaSearchNode;

public class TestCilada {

	public static void mainOld(String[] args) {
//		System.out.println(Piece.K.toString(PieceOrientation.DOWN));
//		System.out.println(Piece.K.toString(PieceOrientation.UP));
//		System.out.println(Piece.K.toString(PieceOrientation.CW));
//		System.out.println(Piece.K.toString(PieceOrientation.CounterCW));

		CiladaSearchNode initialState = new CiladaSearchNode("AABCDDEFGIKN");

		System.out.println(initialState);

		List<AbstractSearchNode> children = initialState.expand();
		System.out.println("FILHOS: " + children.size());

		for (AbstractSearchNode c : children) {
			System.out.println(c);
		}
	}

	public static void main(String[] args) {
		CiladaSearchNode initialState = new CiladaSearchNode("AABCDDEFGIKN");
		// DepthFirstSearch searchAlgorithm = new DepthFirstSearch();
		SMAStarSearch searchAlgorithm = new SMAStarSearch(20);

		AbstractSearchNode result = searchAlgorithm.search(initialState);

		System.out.println(result);
	}

}
